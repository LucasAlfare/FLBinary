# FLBinary
This repository contains some classes to help me deal with binaries.

# Download
You can use this library with [Jitpack](https://jitpack.io/). Add the following to your `build.gradle`:
```
repositories {
  ...
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.LucasAlfare:FLBinary:v1.1'
}
```

Or if you are using `kotlin-DSL` in an `build.gradle.kts` file:
```
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.LucasAlfare:FLBinary:v1.1")
}
```

You can also find how to get the library if you are using another dependency manager system, such as `Maven` in this [Jitpack page](https://jitpack.io/#LucasAlfare/FLBinary/v1.1).

# How to use it

You can write bytes to a raw container using the class `Writer`:
```
fun main() {
  val myWriter = Writer()
  myWriter.writeBoolean(true)
}
```

If is needed to read bytes, use the `Reader` class:
```
fun main() {
  val myData = arrayOf(0x1, 0x2, 0x3, 0x4)
  val myReader = Reader(myData)
  println(myReader.read4Bytes()) //prints `0x1234`
}
```