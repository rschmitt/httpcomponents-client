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

package org.apache.hc.client5.http.examples;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;

import java.io.IOException;

public class SocketLeak {
    public static void main(final String[] args) throws IOException, InterruptedException {
        System.out.printf("This Java version SHOULD%s exhibit the bug where two garbage collections are required "
            + "to clean up leaked sockets.%n", isAffected() ? "" : " NOT");

        final Process monitor = runSocketMonitor();
        try {
            System.out.println("Leaking client; sockets should be in ESTABLISHED");
            leakConnections();
            Thread.sleep(3_000);

            if (isAffected()) {
                System.out.println("Running garbage collector; sockets should be in CLOSE_WAIT");
            } else {
                System.out.println("Running garbage collector; no sockets should be listed");
            }
            System.gc();
            Thread.sleep(3_000);

            if (isAffected()) {
                System.out.println("Running garbage collector again; no sockets should be listed");
                System.gc();
                Thread.sleep(3_000);
            }
        } finally {
            monitor.destroyForcibly();
        }
        System.exit(0);
    }

    private static boolean isAffected() {
        final String javaVersion = System.getProperty("java.specification.version");
        return "11".equals(javaVersion) || "17".equals(javaVersion);
    }

    private static Process runSocketMonitor() throws IOException {
        return new ProcessBuilder()
            .command("./socket-monitor.sh")
            .inheritIO()
            .start();
    }

    @SuppressWarnings("resource")
    private static void leakConnections() throws IOException {
        final PoolingHttpClientConnectionManager connMgr = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .build();
        final CloseableHttpClient client = HttpClientBuilder.create()
            .setConnectionManager(connMgr)
            .build();
        client.execute(new HttpGet("https://www.google.com/"), new BasicHttpClientResponseHandler());
        client.execute(new HttpGet("https://www.amazon.com/"), new BasicHttpClientResponseHandler());
        client.execute(new HttpGet("https://aws.amazon.com/"), new BasicHttpClientResponseHandler());
    }
}
