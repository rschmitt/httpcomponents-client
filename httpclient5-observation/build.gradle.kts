// SPDX-License-Identifier: Apache-2.0

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api(project(":httpclient5-cache"))
    api("org.slf4j:slf4j-api:1.+")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.+")
    api("io.micrometer:micrometer-core:1.+")
    api("io.micrometer:micrometer-observation:1.+")

    testImplementation("io.micrometer:micrometer-registry-prometheus:1.+")
    testImplementation("io.micrometer:micrometer-tracing:1.+")
    testImplementation("io.micrometer:micrometer-tracing-bridge-otel:1.+")
    testImplementation("io.opentelemetry:opentelemetry-sdk:1.+")
    testImplementation("io.opentelemetry:opentelemetry-sdk-testing:1.+")
    testImplementation("org.apache.commons:commons-compress:1.+")
}

description = "Apache HttpClient Observation"
