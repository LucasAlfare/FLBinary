[![](https://jitpack.io/v/LucasAlfare/FLBinary.svg)](https://jitpack.io/#LucasAlfare/FLBinary)


# FLBinary
This repository contains some classes to help me to deal with binaries for JVM.

Since I feel more confortable reading/writing bytes by myself I decided to write this simple library to do that, in order to avoid rewriting this code always I need it.

The implementation is very simple but if you don't understand the meaning of `reading` and `writing` bytes we can discuss it a bit.

# Download
You can use this library with [Jitpack](https://jitpack.io/). Add the following to your `build.gradle`:
```groovy
repositories {
  ...
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.LucasAlfare:FLBinary:1.7'
}
```

Or if you are using `kotlin-DSL` in an `build.gradle.kts` file:
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.LucasAlfare:FLBinary:1.7")
}
```

You can also find how to get the library if you are using another dependency manager system, such as `Maven` in this [Jitpack page](https://jitpack.io/#LucasAlfare/FLBinary/v1.6).

## Downloading directly from GitHub:

You can grab this project directly from its [GitHub page](https://github.com/LucasAlfare/FLBinary) with [Source Dependencies](https://blog.gradle.org/introducing-source-dependencies), from Gradle tool. First, add this to your `settings.gradle.kts`:

```kotlin
sourceControl {
  gitRepository(java.net.URI("https://github.com/LucasAlfare/FLBinary")) {
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
  val myData = byteArrayOf(0x1, 0x2, 0x3, 0x4)
  val myReader = Reader(myData.toUByteArray()) //you must add explicit annotation for this experimental feature
  println(myReader.read4Bytes()) //prints `0x1234`
}
```

Note that the type of the bytes container (data) is the kotlin expereimental type `UByteArray`. This type comes to Kotlin to help to deal with unsigned numbers, in this case bytes. In this project, this type was choose because to work directly with bytes normally is a job to run around the unsigned format of these numbers.

# Implementation insights

Now let's discus a bit about the implementation of reading/writing in this library.

## Reading

"Read" bytes means to take a sequence of `n` bytes and packing then into a single number. For example, the sequence `data = [0xA, 0xB, 0xC, 0xD]` can be read in various formats:
- Performing `read1Byte()` will take the designed amount, in this case 1/one (`n`), then packs all these values into a single number. In this case we retrieved only one single valye from the array, then the result return is the value retrieved itself (`0xA`);
- In other hand, we we perform `read3Bytes()` we should be able to get the result `0xABC`, once it represents the sequence of three (3) numbers from the main data.

The above reading examples results in different numbers. Either `0xA` and `0xABC` can be extracted from the same data, but they are clearly different (convert then to decimal to a better visualization of their values).

The reason to perform things like that is that other programmers can define his own data formats. For example, raw binaries formats has their own structures and should be presented as their specifications defines. In other words, if a file contains that previous `data` example, reading then in different ways can represent the presence of any kind of related information.

Also, reading bytes normally targets numeric types that can hold the respective amount of information. Take attention to main common `Int` variable type, it can hold "signed" numbers with 32 bits length, in other words, numbers with 4 bytes of size. On the other side, "reading only 1 byte" can be stored into types such as `Byte`.

Note that each language/tool can implement those types in their own way. For example, while Java uses different types for numbers in different ranges of bit lenghts, Javascript don't -- particulary I think Javascript system very weird/hard to work directly, once all numbers are treated as floating point numbers.

This library uses `Kotlin`, then the types are very friendly handled. Also, Kotlin is introducing the very experimental feature of the "Unsigned Types", which is very useful for a library like this. We hope this features comes to main API soon!

## Writing

"Write" bytes follows the same idea about bytes discussed above but instead of _packing bytes_ to a _single number_ we perform the reverse job: we take a single number and cracks it into `n` bytes. Taking the same example above, if we perform `write4Bytes()` on the input number `0xABCD` we should get as output the `data` array (`data = [0xA, 0xB, 0xC, 0xD]`).

## Bytes endianness
{TODO}

## _Buffering_
{TODO}
