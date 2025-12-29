// SPDX-License-Identifier: Apache-2.0

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api("org.apache.httpcomponents.core5:httpcore5-testing:5.+")
    api(project(":httpclient5"))
    api("org.slf4j:slf4j-api:1.+")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.+")
    api("org.apache.logging.log4j:log4j-core:2.+")

    testImplementation("org.apache.httpcomponents.core5:httpcore5-reactive:5.+")
    testImplementation(project(":httpclient5-cache"))
    testImplementation(project(":httpclient5-fluent"))
    testImplementation(project(":httpclient5-websocket"))
    testImplementation("com.kohlschutter.junixsocket:junixsocket-core:2.+")
    testImplementation("io.reactivex.rxjava3:rxjava:3.+")
    testImplementation("org.testcontainers:testcontainers:2.+")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.+")
}

description = "Apache HttpClient Integration Tests"
