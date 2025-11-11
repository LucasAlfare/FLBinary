@file:Suppress("MemberVisibilityCanBePrivate")
@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

import java.io.File
import java.io.FileOutputStream

/**
 * Append-only binary writer for general-purpose binary data assembly.
 *
 * This class provides a simple, safe and well-documented API to build binary blobs
 * by appending bytes to an internal buffer. It is intentionally minimalistic:
 * - append-only semantics: every write operation appends bytes to the end of the buffer;
 *   there is no implicit overwrite or seeking built into the API.
 * - uses unsigned bytes (UByte) internally to avoid signed/unsigned conversion pitfalls.
 * - provides convenience methods for writing 1, 2, 3 and 4 byte big-endian integers,
 *   raw byte arrays and strings (low-byte-per-char). It is not opinionated about any
 *   particular file format — you can use it to build PNG, WAV, custom binary blobs, etc.
 *
 * Design notes and guarantees
 * - Append-only: Methods never overwrite previously appended bytes. If you need to
 *   modify data at arbitrary offsets you must create a separate buffer, compose the
 *   pieces you need, or post-process the produced ByteArray.
 * - No automatic resizing constraints: internal representation is a MutableList<UByte>,
 *   so capacity grows transparently. Conversions to ByteArray/UByteArray copy the data.
 * - Thread-safety: NOT thread-safe. If you intend to use a Writer concurrently across
 *   threads, synchronize externally.
 * - Complexity: append operations are amortized O(1). Converting to arrays or saving to disk
 *   is O(n) and copies data once.
 *
 * Typical usage pattern:
 * - Create a Writer
 * - Append headers and bodies sequentially
 * - Optionally build sub-buffers in separate Writer instances, convert them to UByteArray,
 *   then append them into the main writer (useful when chunk sizes must be known before writing
 *   their length fields)
 * - Convert to ByteArray or save to file
 *
 * Example:
 * ```
 * val w = Writer()
 * w.writeString("HEAD")
 * w.write4Bytes(12)           // big-endian 4-byte integer
 * val body = Writer()
 * body.writeString("BODYDATA")
 * w.writeBytes(body.toUByteArray())
 * w.saveToFile("output.bin")
 * ```
 */
class Writer {

  // Internal storage for appended bytes.
  private val buffer = mutableListOf<UByte>()

  /**
   * Return the number of bytes currently stored in the buffer.
   *
   * Equivalent to the position at which the next write will append.
   *
   * @return current buffer size in bytes
   */
  fun tell(): Int = buffer.size

  /**
   * Append a single byte value (0..255) to the end of the buffer.
   *
   * @param value integer in the range 0..255
   * @throws IllegalArgumentException if value is outside 0..255
   * @complexity Amortized O(1)
   */
  fun write1Byte(value: Int) {
    require(value in 0..0xFF) { "Value must be in 0..255" }
    buffer.add((value and 0xFF).toUByte())
  }

  /**
   * Append a boolean as a single byte: 1 for true, 0 for false.
   *
   * @param value boolean to append
   * @complexity Amortized O(1)
   */
  fun writeBoolean(value: Boolean) {
    write1Byte(if (value) 1 else 0)
  }

  /**
   * Append two bytes (big-endian) representing the low 16 bits of [value].
   *
   * This writes the high-order byte first, then the low-order byte.
   *
   * @param value integer in range 0..65535 (0..0xFFFF)
   * @throws IllegalArgumentException if value is outside allowed range
   * @complexity Amortized O(1)
   */
  fun write2Bytes(value: Int) {
    require(value in 0..0xFFFF) { "Value must fit in 2 bytes (0..65535)" }
    buffer.add(((value ushr 8) and 0xFF).toUByte())
    buffer.add((value and 0xFF).toUByte())
  }

  /**
   * Append three bytes (big-endian) representing the low 24 bits of [value].
   *
   * @param value integer in range 0..16777215 (0..0xFFFFFF)
   * @throws IllegalArgumentException if value is outside allowed range
   * @complexity Amortized O(1)
   */
  fun write3Bytes(value: Int) {
    require(value in 0..0xFFFFFF) { "Value must fit in 3 bytes (0..16777215)" }
    buffer.add(((value ushr 16) and 0xFF).toUByte())
    buffer.add(((value ushr 8) and 0xFF).toUByte())
    buffer.add((value and 0xFF).toUByte())
  }

  /**
   * Append four bytes (big-endian) representing the low 32 bits of [value].
   *
   * Accepts a Long to allow writing unsigned 32-bit ranges (0..0xFFFFFFFF).
   *
   * @param value long in range 0..4294967295 (0..0xFFFFFFFF)
   * @throws IllegalArgumentException if value is outside allowed range
   * @complexity Amortized O(1)
   */
  fun write4Bytes(value: Long) {
    require(value in 0L..0xFFFFFFFFL) { "Value must fit in 4 bytes (0..4294967295)" }
    buffer.add(((value ushr 24) and 0xFF).toInt().toUByte())
    buffer.add(((value ushr 16) and 0xFF).toInt().toUByte())
    buffer.add(((value ushr 8) and 0xFF).toInt().toUByte())
    buffer.add((value and 0xFF).toInt().toUByte())
  }

  /**
   * Append the contents of a UByteArray to the buffer.
   *
   * This copies each element of [bytes] into the writer's internal buffer.
   *
   * @param bytes array to append
   * @complexity O(n) where n = bytes.size
   */
  fun writeBytes(bytes: UByteArray) {
    for (b in bytes) buffer.add(b)
  }

  /**
   * Append a String by converting each character to its low byte (char.code & 0xFF).
   *
   * Important: this method writes the low byte of each Char. For multi-byte encodings
   * (UTF-8, UTF-16) encode the string to bytes and call [writeBytes] instead:
   * `writeBytes(myString.toByteArray(Charsets.UTF_8).toUByteArray())`.
   *
   * @param value string to append (low byte per character)
   * @complexity O(n) where n = value.length
   */
  fun writeString(value: String) {
    for (c in value) buffer.add((c.code and 0xFF).toUByte())
  }

  /**
   * Convert the internal buffer to a UByteArray copy.
   *
   * The returned array is a fresh copy; further writes to this Writer won't affect the returned array.
   *
   * @return new UByteArray containing the buffer contents
   * @complexity O(n)
   */
  fun toUByteArray(): UByteArray {
    val out = UByteArray(buffer.size)
    for (i in buffer.indices) out[i] = buffer[i]
    return out
  }

  /**
   * Convert the internal buffer to a signed ByteArray copy.
   *
   * JVM IO APIs usually expect ByteArray; this converts each UByte to Byte directly.
   *
   * @return new ByteArray containing the buffer contents
   * @complexity O(n)
   */
  fun toByteArray(): ByteArray {
    val out = ByteArray(buffer.size)
    for (i in buffer.indices) out[i] = buffer[i].toByte()
    return out
  }

  /**
   * Save the current buffer to a file at [path]. The file will be overwritten if it exists.
   *
   * This method performs a single copy to ByteArray and then writes to disk.
   * Use this for finalizing the assembled blob.
   *
   * @param path filesystem path for the output file
   * @throws java.io.IOException if the file cannot be written
   * @complexity O(n) for copying + I/O
   */
  fun saveToFile(path: String) {
    val file = File(path)
    FileOutputStream(file).use { fos ->
      fos.write(toByteArray())
      fos.flush()
    }
  }

  /**
   * Clear the internal buffer. After calling this method, `tell()` returns 0.
   *
   * Use this to reuse the same writer instance for multiple independent outputs.
   *
   * @complexity O(n)
   */
  fun clear() {
    buffer.clear()
  }

  companion object {
    /**
     * Build a binary "chunk" composed of: id bytes, 4-byte big-endian length, then body bytes.
     *
     * The function is intentionally general-purpose. Many binary formats use a fixed-length ID
     * followed by a length field and a body; this helper creates such a structure:
     *
     * Result layout:
     *   [ id bytes (id.length bytes) ] [ 4-byte big-endian length ] [ body bytes ]
     *
     * Use it when you want a canonical "id + 4-byte-length + body" layout.
     *
     * @param id identifier string. The low byte of each character is taken.
     *           Must not be empty.
     * @param body body bytes to include
     * @return UByteArray containing id + 4-byte length + body
     * @throws IllegalArgumentException if id is empty or body is too large for 4-byte length
     * @complexity O(n) where n = id.length + body.size
     */
    fun buildChunk(id: String, body: UByteArray): UByteArray {
      require(id.isNotEmpty()) { "Chunk id must not be empty" }
      val idBytes = UByteArray(id.length) { index -> (id[index].code and 0xFF).toUByte() }
      val length = body.size.toLong()
      require(length in 0L..0xFFFFFFFFL) { "Body too large for 4-byte length" }

      val result = mutableListOf<UByte>()
      // append id bytes
      for (b in idBytes) result.add(b)
      // append 4-byte big-endian length
      result.add(((length ushr 24) and 0xFF).toInt().toUByte())
      result.add(((length ushr 16) and 0xFF).toInt().toUByte())
      result.add(((length ushr 8) and 0xFF).toInt().toUByte())
      result.add((length and 0xFF).toInt().toUByte())
      // append body
      for (b in body) result.add(b)
      return result.toUByteArray()
    }

    // Internal helper to convert MutableList<UByte> to UByteArray efficiently
    private fun MutableList<UByte>.toUByteArray(): UByteArray {
      val out = UByteArray(this.size)
      for (i in this.indices) out[i] = this[i]
      return out
    }
  }
}