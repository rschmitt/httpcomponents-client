plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api(libs.org.slf4j.slf4j.api)

    testImplementation(project(":httpclient5", configuration = "tests"))
    testImplementation(libs.org.apache.logging.log4j.log4j.slf4j.impl)
    testImplementation(libs.org.apache.logging.log4j.log4j.core)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.params)
    testImplementation(libs.org.hamcrest.hamcrest)
    testImplementation(libs.org.mockito.mockito.core)
}

description = "Apache HttpClient Fluent"
