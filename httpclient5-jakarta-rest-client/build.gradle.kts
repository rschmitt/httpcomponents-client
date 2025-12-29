// SPDX-License-Identifier: Apache-2.0

plugins {
    id("buildlogic.java-conventions")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    api(project(":httpclient5"))
    api("jakarta.ws.rs:jakarta.ws.rs-api:4.+")
    api("org.apache.httpcomponents.core5:httpcore5-jackson3:5.+")

    testImplementation("org.apache.httpcomponents.core5:httpcore5-testing:5.+")
    testImplementation("org.slf4j:slf4j-api:1.+")
    testImplementation("org.assertj:assertj-core:3.+")
}

description = "Jakarta REST Client for Apache HttpClient"
