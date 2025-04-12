plugins {
  alias(libs.plugins.kotlin.jvm)
  application
  `maven-publish`
}

group = "com.lucasalfare.flbinary"
version = libs.versions.fl.binary

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
