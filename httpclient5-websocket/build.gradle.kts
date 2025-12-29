// SPDX-License-Identifier: Apache-2.0

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api(project(":httpclient5-cache"))
    api("org.apache.httpcomponents.core5:httpcore5-h2:5.+")
    api("org.slf4j:slf4j-api:1.+")

    testImplementation("org.apache.commons:commons-compress:1.+")
}

description = "Apache HttpClient WebSocket"
