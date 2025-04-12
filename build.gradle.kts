plugins {
  kotlin("jvm") version "2.0.0"
  application
  `maven-publish`
}

group = "com.lucasalfare.flbinary"
version = "v1.7"

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
  jvmToolchain(21)
}

publishing {
  publications {
    create<MavenPublication>("Maven") {
      from(components["kotlin"])
    }
  }
}
