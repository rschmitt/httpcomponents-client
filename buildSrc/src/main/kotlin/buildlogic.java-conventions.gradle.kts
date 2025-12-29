// SPDX-License-Identifier: Apache-2.0

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repository.apache.org/snapshots")
    }
    mavenCentral()
}

group = rootProject.group
version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.+"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.hamcrest:hamcrest:3.+")
    testImplementation("org.mockito:mockito-core:4.+")
    testImplementation("org.mockito:mockito-junit-jupiter:4.+")
    testImplementation("org.apache.logging.log4j:log4j-core:2.+")
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.apache.httpcomponents.core5:httpcore5:5.+") {
        // TODO: Check for `httpcore5` specifically
        if (gradle.includedBuilds.isEmpty()) {
            artifact {
                classifier = "tests"      // For non-composite builds
            }
        } else {
            targetConfiguration = "tests" // For composite builds
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.withType<Test>() {
    useJUnitPlatform {
        excludeTags("slow", "time-out", "stale-conn")
    }
}
