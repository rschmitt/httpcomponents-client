// SPDX-License-Identifier: Apache-2.0

plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api("org.slf4j:slf4j-api:1.+")
    api("org.ehcache.modules:ehcache-api:3.11.+")
    api("org.apache.logging.log4j:log4j-slf4j-impl:2.+")
    api("net.spy:spymemcached:2.+")
    api("com.github.ben-manes.caffeine:caffeine:2.+")

    testImplementation(project(":httpclient5", configuration = "tests"))
}

description = "Apache HttpClient Cache"

val testsJar by tasks.registering(Jar::class) {
    archiveClassifier = "tests"
    from(sourceSets["test"].output)
}

(publishing.publications["maven"] as MavenPublication).artifact(testsJar)
