plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api(project(":httpclient5-cache"))
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.apache.logging.log4j.log4j.slf4j.impl)
    api(libs.io.micrometer.micrometer.core)
    api(libs.io.micrometer.micrometer.observation)

    testImplementation(libs.org.apache.logging.log4j.log4j.core)
    testImplementation(libs.io.micrometer.micrometer.registry.prometheus)
    testImplementation(libs.io.micrometer.micrometer.tracing)
    testImplementation(libs.io.micrometer.micrometer.tracing.bridge.otel)
    testImplementation(libs.io.opentelemetry.opentelemetry.sdk)
    testImplementation(libs.io.opentelemetry.opentelemetry.sdk.testing)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.org.apache.commons.commons.compress)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
}

description = "Apache HttpClient Observation"
