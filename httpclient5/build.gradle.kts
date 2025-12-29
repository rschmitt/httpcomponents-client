// SPDX-License-Identifier: Apache-2.0

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
    api("org.apache.httpcomponents.core5:httpcore5:5.+")
    api("org.apache.httpcomponents.core5:httpcore5-h2:5.+")
    api("org.slf4j:slf4j-api:1.+")
    compileOnly("org.apache.commons:commons-compress:1.+")
    compileOnly("org.conscrypt:conscrypt-openjdk-uber:2.+")
    compileOnly("com.github.luben:zstd-jni:1.+")
    compileOnly("com.aayushatharva.brotli4j:brotli4j:1.+")

    testImplementation("org.apache.httpcomponents.core5:httpcore5-reactive:5.+")
    testImplementation("io.reactivex.rxjava3:rxjava:3.+")
    testImplementation("com.kohlschutter.junixsocket:junixsocket-core:2.+")
    testImplementation("commons-io:commons-io:2.+")
    testImplementation("org.apache.commons:commons-compress:1.+")
    testImplementation("org.conscrypt:conscrypt-openjdk-uber:2.+")
    testImplementation("com.github.luben:zstd-jni:1.+")
    testImplementation("com.aayushatharva.brotli4j:brotli4j:1.+")
    testImplementation("com.aayushatharva.brotli4j:native-$os-$arch:1.+")
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
