[![](https://jitpack.io/v/LucasAlfare/FLBinary.svg)](https://jitpack.io/#LucasAlfare/FLBinary)


# FLBinary

**FLBinary** is a lightweight utility library designed to simplify binary reading and writing on the JVM.  
It was created to avoid repetitive boilerplate when manually working with byte streams in Kotlin or Java.

If you're comfortable manipulating bytes directly, this library provides an intuitive and minimalistic approach to handle binary data more effectively.

---

## Features

- Minimal and focused implementation
- Explicit byte-level control
- Kotlin-first API, but fully usable from Java
- No unnecessary dependencies
- No extra overheads

---

## Getting Started

### Using JitPack

FLBinary is available via [JitPack](https://jitpack.io/).

#### Gradle (Kotlin DSL)

Add the following to your `build.gradle.kts` file:  
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.LucasAlfare:FLBinary:1.7")
}
```

#### Using Version Catalogs (Gradle 7+)

If you're using [Version Catalogs](https://docs.gradle.org/current/userguide/version_catalogs.html), add the following to your `libs.versions.toml`:  
```toml
[versions]
flbinary = "1.7"

[libraries]
flbinary = { module = "com.github.LucasAlfare:FLBinary", version.ref = "flbinary" }
```

Then, reference it in your build script as a dependency:  
```kotlin
dependencies {
    implementation(libs.flbinary)
}
```

#### Maven & Others

Check [this JitPack page](https://jitpack.io/#LucasAlfare/FLBinary/1.7) for setup instructions tailored to your build tool.

---

## How to Use

The `Reader` class is designed to facilitate reading signed integers and strings from a `UByteArray` in a structured and safe way. It abstracts away the complexities of dealing with signed and unsigned conversions, allowing you to work directly with binary data.

### Basic Usage

1. **Initialize the Reader with a `UByteArray`**  
   Create an instance of `Reader` by passing a `UByteArray` containing your binary data.

    ```kotlin
    val bytes: UByteArray = ubyteArrayOf(0x01u, 0x02u, 0x03u, 0x04u)
    val reader = Reader(bytes)
    ```

2. **Read Values**  
   Use the available `readXBytes()` methods to read signed integers from the binary data:
   
   - `read1Byte()` – Reads 1 byte and returns an `Int`
   - `read2Bytes()` – Reads 2 bytes and returns an `Int`
   - `read3Bytes()` – Reads 3 bytes and returns an `Int`
   - `read4Bytes()` – Reads 4 bytes and returns a `Long`

   Optionally, pass a custom position to read from a specific index without affecting the current position.

    ```kotlin
    val oneByte = reader.read1Byte()
    val twoBytes = reader.read2Bytes()
    val threeBytes = reader.read3Bytes()
    val fourBytes = reader.read4Bytes()
    ```

3. **Read Boolean Values**  
   Use `readBoolean()` to interpret a single byte as a Boolean (`1` = `true`, `0` = `false`).

    ```kotlin
    val flag = reader.readBoolean()
    ```

4. **Read Strings**  
   Use `readString(length)` to read a sequence of bytes and convert them into a string of the specified length.

    ```kotlin
    val text = reader.readString(5)
    ```

5. **Manage Position**  
   The internal position advances automatically after each read. If you need to skip bytes manually, use `advancePosition(length)`.

```kotlin
reader.advancePosition(3) // skips 3 bytes
```

### Example

- Read 2 bytes as an integer
- Read a boolean value
- Read a 4-byte long
- Read a string of length 5

    ```kotlin
    val bytes = ubyteArrayOf(0x00u, 0x02u, 0x01u, 0x00u, 0x00u, 0x00u, 0x2Au, 0x48u, 0x65u, 0x6Cu, 0x6Cu, 0x6Fu)
    val reader = Reader(bytes)
    
    val two = reader.read2Bytes()       // 0x0002
    val boolean = reader.readBoolean()  // true (0x01)
    val number = reader.read4Bytes()    // 42 (0x0000002A)
    val string = reader.readString(5)   // "Hello"
    ```

Always ensure that the reads do not exceed the bounds of the data array, as each method includes basic validation and will throw if attempting to read past the end of the array.

--- 

## License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/LucasAlfare/FLBinary/blob/master/LICENSE) file for details.

---

## Author

Developed and maintained by [Francisco Lucas](https://github.com/LucasAlfare).
