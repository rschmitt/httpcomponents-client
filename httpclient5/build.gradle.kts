plugins {
    id("buildlogic.java-conventions")
}

val osName = System.getProperty("os.name").lowercase()
val os = when {
    osName.contains("mac") -> "osx"
    osName.contains("windows") -> "windows"
    else -> "linux"
}
val arch = System.getProperty("os.arch")

dependencies {
    api(libs.org.apache.httpcomponents.core5.httpcore5)
    api(libs.org.apache.httpcomponents.core5.httpcore5.h2)
    api(libs.org.slf4j.slf4j.api)
    compileOnly(libs.org.apache.commons.commons.compress)
    compileOnly(libs.org.conscrypt.conscrypt.openjdk.uber)
    compileOnly(libs.com.github.luben.zstd.jni)
    compileOnly(libs.com.aayushatharva.brotli4j.brotli4j)

    testImplementation(libs.org.apache.httpcomponents.core5.httpcore5.reactive)
    testImplementation(libs.io.reactivex.rxjava3.rxjava)
    testImplementation(libs.org.apache.logging.log4j.log4j.slf4j.impl)
    testImplementation(libs.org.apache.logging.log4j.log4j.core)
    testImplementation(libs.com.kohlschutter.junixsocket.junixsocket.core)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.org.junit.platform.junit.platform.launcher)
    testImplementation(libs.org.hamcrest.hamcrest)
    testImplementation(libs.org.mockito.mockito.core)
    testImplementation(libs.commons.io.commons.io)
    testImplementation(libs.org.apache.commons.commons.compress)
    testImplementation(libs.org.conscrypt.conscrypt.openjdk.uber)
    testImplementation(libs.com.github.luben.zstd.jni)
    testImplementation(libs.com.aayushatharva.brotli4j.brotli4j)
    testImplementation("com.aayushatharva.brotli4j:native-$os-$arch")
}

description = "Apache HttpClient"

val testsJar by tasks.registering(Jar::class) {
    archiveClassifier.set("tests")
    from(sourceSets["test"].output)
}

val tests by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(tests.name, testsJar)
}
