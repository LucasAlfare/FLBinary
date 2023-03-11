# FLBinary
This repository contains some classes to help me deal with binaries.

# Download
You can use this library with [Jitpack](https://jitpack.io/). Add the following to your `build.gradle`:
```groovy
repositories {
  ...
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.LucasAlfare:FLBinary:v1.1'
}
```

Or if you are using `kotlin-DSL` in an `build.gradle.kts` file:
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.LucasAlfare:FLBinary:v1.1")
}
```

You can also find how to get the library if you are using another dependency manager system, such as `Maven` in this [Jitpack page](https://jitpack.io/#LucasAlfare/FLBinary/v1.1).

## Downloading directly from GitHub:

You can grab this project directly from its [GitHub page](https://github.com/LucasAlfare/FLBinary) with [Source Dependencies](https://blog.gradle.org/introducing-source-dependencies), from Gradle tool. First, add this to your `settings.gradle.kts`:

```kotlin
sourceControl {
  gitRepository(URI("https://github.com/LucasAlfare/FLBinary")) {
    producesModule("com.lucasalfare.flbinary:FLBinary")
  }
}
```

After, add this to your `build.gradle.kts` to target the `master` branch of this repository:

```kotlin
implementation("com.lucasalfare.flbinary:FLBinary") {
  version {
    branch = "master"
  }
}
```

Or if you need some specific release, target its `tag` with:

```kotlin
implementation("com.lucasalfare.flbinary:FLBinary:XXX") //XXX=target tag
```

# How to use

You can write bytes to a raw container using the class `Writer`:
```kotlin
fun main() {
  val myWriter = Writer()
  myWriter.writeBoolean(true)
}
```

If is needed to read bytes, use the `Reader` class:
```kotlin
fun main() {
  val myData = arrayOf(0x1, 0x2, 0x3, 0x4)
  val myReader = Reader(myData)
  println(myReader.read4Bytes()) //prints `0x1234`
}
```
