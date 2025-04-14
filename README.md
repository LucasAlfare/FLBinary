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

## License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/LucasAlfare/FLBinary/blob/master/LICENSE) file for details.

---

## Author

Developed and maintained by [Francisco Lucas](https://github.com/LucasAlfare).
