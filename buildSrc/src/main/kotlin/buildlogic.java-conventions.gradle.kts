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
    implementation(platform(project(":")))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.apache.httpcomponents.core5:httpcore5") {
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

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
