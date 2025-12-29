plugins {
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":httpclient5"))
    api(libs.org.slf4j.slf4j.api)
    api(libs.org.ehcache.modules.ehcache.api)
    api(libs.org.apache.logging.log4j.log4j.slf4j.impl)
    api(libs.net.spy.spymemcached)
    api(libs.com.github.ben.manes.caffeine.caffeine)

    testImplementation(libs.org.apache.logging.log4j.log4j.core)
    testImplementation(libs.org.hamcrest.hamcrest)
    testImplementation(libs.org.mockito.mockito.core)
    testImplementation(project(":httpclient5", configuration = "tests"))
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
}

description = "Apache HttpClient Cache"

val testsJar by tasks.registering(Jar::class) {
    archiveClassifier = "tests"
    from(sourceSets["test"].output)
}

(publishing.publications["maven"] as MavenPublication).artifact(testsJar)
