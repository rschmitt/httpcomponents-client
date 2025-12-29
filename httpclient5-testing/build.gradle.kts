plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.org.apache.httpcomponents.core5.httpcore5.testing)
    api(project(":httpclient5"))
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.apache.logging.log4j.log4j.slf4j.impl)
    api(libs.org.apache.logging.log4j.log4j.core)

    testImplementation(libs.org.apache.httpcomponents.core5.httpcore5.reactive)
    testImplementation(project(":httpclient5-cache"))
    testImplementation(project(":httpclient5-fluent"))
    testImplementation(libs.com.kohlschutter.junixsocket.junixsocket.core)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.org.hamcrest.hamcrest)
    testImplementation(libs.org.mockito.mockito.core)
    testImplementation(libs.io.reactivex.rxjava3.rxjava)
    testImplementation(libs.org.testcontainers.testcontainers)
    testImplementation(libs.org.testcontainers.junit.jupiter)
}

description = "Apache HttpClient Integration Tests"
