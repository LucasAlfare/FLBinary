plugins {
    kotlin("jvm") version "1.8.0"
    application
    `maven-publish`
}

group = "com.lucasalfare.flbinary"
version = "v1.4"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

/*
Source: https://stackoverflow.com/a/67770253
 */
tasks.create("incrementVersion") {
    group = "fl_tasks"
    description = "Increments the version in this build file everywhere it is used."
    fun generateVersion(): String {
        val updateMode = properties["mode"] ?: "minor" // By default, update the minor
        val (oldMajor, oldMinor, oldPatch) = (version.toString()).split(".").map(String::toInt)
        var (newMajor, newMinor, newPatch) = arrayOf(oldMajor, oldMinor, 0)
        when (updateMode) {
            "major" -> newMajor = (oldMajor + 1).also { newMinor = 0 }
            "minor" -> newMinor = oldMinor + 1
            else -> newPatch = oldPatch + 1
        }
        return "$newMajor.$newMinor.$newPatch"
    }
    doLast {
        val newVersion = properties["overrideVersion"] as String? ?: generateVersion()
        val oldContent = buildFile.readText()
        val newContent = oldContent.replace("""= "$version"""", """= "$newVersion"""")
        buildFile.writeText(newContent)
    }
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
