/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.hc.client5.testing.async;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.reactive.ReactiveEntityProducer;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.apache.hc.core5.util.ByteArrayBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;

public abstract class AbstractHttpReactiveFundamentalsTest<T extends CloseableHttpAsyncClient> extends AbstractIntegrationTestBase<T> {

    public AbstractHttpReactiveFundamentalsTest(final URIScheme scheme) {
        super(scheme);
    }

    @Test(timeout = 60_000)
    public void testSequentialGetRequests() throws Exception {
        final HttpHost target = start();
        for (int i = 0; i < 3; i++) {
            final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer();

            httpclient.execute(AsyncRequestBuilder.get(target + "/random/2048").build(), consumer, null);

            final Message<HttpResponse, Publisher<ByteBuffer>> response = consumer.getResponseFuture().get();
            Assert.assertThat(response, CoreMatchers.notNullValue());
            Assert.assertThat(response.getHead().getCode(), CoreMatchers.equalTo(200));

            final String body = publisherToString(response.getBody());
            Assert.assertThat(body, CoreMatchers.notNullValue());
            Assert.assertThat(body.length(), CoreMatchers.equalTo(2048));
        }
    }

    @Test(timeout = 2000)
    public void testSequentialHeadRequests() throws Exception {
        final HttpHost target = start();
        for (int i = 0; i < 3; i++) {
            final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer();

            httpclient.execute(AsyncRequestBuilder.head(target + "/random/2048").build(), consumer, null);

            final Message<HttpResponse, Publisher<ByteBuffer>> response = consumer.getResponseFuture().get();
            Assert.assertThat(response, CoreMatchers.notNullValue());
            Assert.assertThat(response.getHead().getCode(), CoreMatchers.equalTo(200));

            final String body = publisherToString(response.getBody());
            Assert.assertThat(body, CoreMatchers.nullValue());
        }
    }

    @Test(timeout = 60_000)
    public void testSequentialPostRequests() throws Exception {
        final HttpHost target = start();
        for (int i = 0; i < 3; i++) {
            final byte[] b1 = new byte[1024];
            final Random rnd = new Random(System.currentTimeMillis());
            rnd.nextBytes(b1);
            final Flowable<ByteBuffer> publisher = Flowable.just(ByteBuffer.wrap(b1));
            final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer();
            final AsyncRequestProducer request = AsyncRequestBuilder.post(target + "/echo/")
                .setEntity(new ReactiveEntityProducer(publisher, -1, ContentType.APPLICATION_OCTET_STREAM, null))
                .build();

            httpclient.execute(request, consumer, HttpClientContext.create(), null);

            final Future<Message<HttpResponse, Publisher<ByteBuffer>>> responseFuture = consumer.getResponseFuture();
            final Message<HttpResponse, Publisher<ByteBuffer>> responseMessage = responseFuture.get();
            Assert.assertThat(responseMessage, CoreMatchers.notNullValue());
            final HttpResponse response = responseMessage.getHead();
            Assert.assertThat(response.getCode(), CoreMatchers.equalTo(200));
            final byte[] b2 = publisherToByteArray(responseMessage.getBody());
            Assert.assertThat(b1, CoreMatchers.equalTo(b2));
        }
    }

    @Test(timeout = 60_000)
    public void testConcurrentPostRequests() throws Exception {
        final HttpHost target = start();
        final byte[] b1 = new byte[1024];
        final Random rnd = new Random(System.currentTimeMillis());
        rnd.nextBytes(b1);

        final int reqCount = 20;

        final Queue<Future<Message<HttpResponse, Publisher<ByteBuffer>>>> queue = new LinkedList<>();
        for (int i = 0; i < reqCount; i++) {
            final Flowable<ByteBuffer> publisher = Flowable.just(ByteBuffer.wrap(b1));
            final ReactiveEntityProducer producer = new ReactiveEntityProducer(publisher, -1,
                ContentType.APPLICATION_OCTET_STREAM, null);
            final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer();
            final AsyncRequestProducer request = AsyncRequestBuilder.post(target + "/echo/")
                .setEntity(producer)
                .build();

            httpclient.execute(request, consumer, HttpClientContext.create(), null);

            queue.add(consumer.getResponseFuture());
        }

        while (!queue.isEmpty()) {
            final Future<Message<HttpResponse, Publisher<ByteBuffer>>> future = queue.remove();
            final Message<HttpResponse, Publisher<ByteBuffer>> responseMessage = future.get();
            Assert.assertThat(responseMessage, CoreMatchers.notNullValue());
            final HttpResponse response = responseMessage.getHead();
            Assert.assertThat(response.getCode(), CoreMatchers.equalTo(200));
            final byte[] b2 = publisherToByteArray(responseMessage.getBody());
            Assert.assertThat(b1, CoreMatchers.equalTo(b2));
        }
    }

    @Test(timeout = 60_000)
    public void testRequestExecutionFromCallback() throws Exception {
        final HttpHost target = start();
        final int requestNum = 50;
        final AtomicInteger count = new AtomicInteger(requestNum);
        final Queue<Message<HttpResponse, Publisher<ByteBuffer>>> resultQueue = new ConcurrentLinkedQueue<>();
        final CountDownLatch countDownLatch = new CountDownLatch(requestNum);

        final FutureCallback<Message<HttpResponse, Publisher<ByteBuffer>>> callback = new FutureCallback<Message<HttpResponse, Publisher<ByteBuffer>>>() {
            @Override
            public void completed(final Message<HttpResponse, Publisher<ByteBuffer>> result) {
                try {
                    resultQueue.add(result);
                    if (count.decrementAndGet() > 0) {
                        final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer(this);
                        httpclient.execute(AsyncRequestBuilder.get(target + "/random/2048").build(), consumer, null);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void failed(final Exception ex) {
                countDownLatch.countDown();
            }

            @Override
            public void cancelled() {
                countDownLatch.countDown();
            }
        };

        final int threadNum = 5;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    if (!Thread.currentThread().isInterrupted()) {
                        final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer(callback);
                        httpclient.execute(AsyncRequestBuilder.get(target + "/random/2048").build(), consumer, null);
                    }
                }
            });
        }

        Assert.assertThat(countDownLatch.await(TIMEOUT.getDuration(), TIMEOUT.getTimeUnit()), CoreMatchers.equalTo(true));

        executorService.shutdownNow();
        executorService.awaitTermination(TIMEOUT.getDuration(), TIMEOUT.getTimeUnit());

        for (;;) {
            final Message<HttpResponse, Publisher<ByteBuffer>> response = resultQueue.poll();
            if (response == null) {
                break;
            }
            Assert.assertThat(response.getHead().getCode(), CoreMatchers.equalTo(200));
        }
    }

    @Test
    public void testBadRequest() throws Exception {
        final HttpHost target = start();
        final AsyncRequestProducer request = AsyncRequestBuilder.get(target + "/random/boom").build();
        final ReactiveResponseConsumer consumer = new ReactiveResponseConsumer();

        httpclient.execute(request, consumer, null);

        final Future<Message<HttpResponse, Publisher<ByteBuffer>>> future = consumer.getResponseFuture();
        final HttpResponse response = future.get().getHead();
        Assert.assertThat(response, CoreMatchers.notNullValue());
        Assert.assertThat(response.getCode(), CoreMatchers.equalTo(400));
    }

    static String publisherToString(final Publisher<ByteBuffer> publisher) throws Exception {
        final byte[] bytes = publisherToByteArray(publisher);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    static byte[] publisherToByteArray(final Publisher<ByteBuffer> publisher) throws Exception {
        final BasicFuture<byte[]> future = new BasicFuture<>(null);
        publisher.subscribe(new Subscriber<ByteBuffer>() {

            private ByteArrayBuffer buf;

            @Override
            public synchronized void onSubscribe(final Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public synchronized void onNext(final ByteBuffer src) {
                if (buf == null) {
                    buf = new ByteArrayBuffer(1024);
                }
                if (src.hasArray()) {
                    buf.append(src.array(), src.arrayOffset() + src.position(), src.remaining());
                } else {
                    while (src.hasRemaining()) {
                        buf.append(src.get());
                    }
                }
            }

            @Override
            public synchronized void onError(final Throwable t) {
                if (t instanceof Exception) {
                    future.failed((Exception) t);
                } else {
                    future.failed(new RuntimeException(t));
                }
                buf = null;
            }

            @Override
            public synchronized void onComplete() {
                future.completed(buf != null ? buf.toByteArray() : null);
                buf = null;
            }

        });
        return future.get();
    }
}
