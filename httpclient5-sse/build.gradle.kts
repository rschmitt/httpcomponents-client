// SPDX-License-Identifier: Apache-2.0

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api("org.slf4j:slf4j-api:1.+")

    testImplementation(project(":httpclient5", configuration = "tests"))
}

description = "Apache HttpClient sse"
