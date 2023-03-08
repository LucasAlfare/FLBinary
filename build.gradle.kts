plugins {
    kotlin("jvm") version "1.8.0"
    application
    `maven-publish`
}

group = "com.lucasalfare.flbinary"
version = "v1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            from(components["kotlin"])
        }
    }
}
