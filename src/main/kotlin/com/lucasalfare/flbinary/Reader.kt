@file:Suppress("MemberVisibilityCanBePrivate")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

/**
 * This class encapsulates the task of reading bytes to a single array, interpreting them as signed integer values.
 *
 * This class uses an array of type [UByteArray] as its main data source to avoid casting and conversion problems
 * that arise when using signed integers. For example, the hex value [0xCC], when converted to [Int], becomes something
 * like [0xFFFFFFCC], which is expected by the [Int] type specification but is not appropriate for this use case.
 * To avoid this issue, unsigned bytes are used, and the class ensures that only unsigned values are processed correctly.
 *
 * All reading methods in this class are written in the form "readXByte", meaning the method will take a sequence of
 * `X` bytes from the [data] field and merge them into a single number of type [Int] or [Long].
 *
 * The class also includes automatic position tracking to manage the current read position in the [data] array, advancing
 * as data is read, ensuring that the sequence of bytes is handled in a linear fashion.
 */
class Reader(var data: UByteArray) {

  /**
   * Holds the current position where the next byte will be read.
   * This is updated as data is read from the [data] array.
   */
  var position = 0

  /**
   * Reads a single byte from the specified position in the [data] array.
   * The returned value is an [Int], representing the byte as a signed integer.
   *
   * @param customPosition The position in the array from where the byte will be read.
   * Defaults to the current position.
   * @return The signed integer value of the byte at the specified position.
   */
  fun read1Byte(customPosition: Int = position): Int {
    // Ensure the position is within bounds
    require(customPosition < data.size) { "Position out of bounds" }

    val byte = data[customPosition].toInt()
    advancePosition()
    return byte
  }

  /**
   * Reads a single byte from the given position and interprets it as a Boolean.
   *
   * The byte must be either `0` or `1`. Returns `true` if the byte is `1`, and `false` if it is `0`.
   *
   * If the byte has a value other than `0` or `1`, the behavior depends on [strictMode]:
   * - If `strictMode` is `true`, throws [NoSuchElementException].
   * - If `strictMode` is `false`, returns `false` by default.
   *
   * @param position The position in the array to read the byte from. Defaults to the current position.
   * @param strictMode Whether to throw an exception if the value is not 0 or 1. Defaults to `false`.
   * @return The boolean value corresponding to the byte read.
   * @throws NoSuchElementException if the byte is not 0 or 1 and [strictMode] is enabled.
   */
  fun readBoolean(
    position: Int = this.position,
    strictMode: Boolean = false
  ): Boolean {
    return when (val byteValue = read1Byte(position)) {
      1 -> true
      0 -> false
      else -> {
        if (strictMode) {
          throw NoSuchElementException("Expected byte value 0 or 1 at position $position, but got: $byteValue")
        }
        false
      }
    }
  }

  /**
   * Reads the next 2 bytes (starting from the current position) and packs them into a single signed integer of type [Int].
   * The function automatically advances the current position by 2 bytes.
   *
   * @param customPosition The position in the array from where the bytes will be read.
   * Defaults to the current position.
   * @return The signed integer value formed by the 2 bytes read.
   */
  fun read2Bytes(customPosition: Int = position): Int {
    // Ensure the position is within bounds
    require(customPosition + 1 < data.size) { "Position out of bounds for 2-byte read" }

    val byte1 = data[customPosition].toInt()
    val byte2 = data[customPosition + 1].toInt()
    val result = (byte1 shl 8) or byte2
    advancePosition(2)
    return result
  }

  /**
   * Reads the next 3 bytes (starting from the current position) and packs them into a single signed integer of type [Int].
   * The function automatically advances the current position by 3 bytes.
   *
   * @param customPosition The position in the array from where the bytes will be read.
   * Defaults to the current position.
   * @return The signed integer value formed by the 3 bytes read.
   */
  fun read3Bytes(customPosition: Int = position): Int {
    // Ensure the position is within bounds
    require(customPosition + 2 < data.size) { "Position out of bounds for 3-byte read" }

    val byte1 = data[customPosition].toInt()
    val byte2 = data[customPosition + 1].toInt()
    val byte3 = data[customPosition + 2].toInt()
    val result = (byte1 shl 16) or (byte2 shl 8) or byte3
    advancePosition(3)
    return result
  }

  /**
   * Reads the next 4 bytes (starting from the current position) and packs them into a single signed long of type [Long].
   * The function automatically advances the current position by 4 bytes.
   *
   * @param customPosition The position in the array from where the bytes will be read.
   * Defaults to the current position.
   * @return The signed long value formed by the 4 bytes read.
   */
  fun read4Bytes(customPosition: Int = position): Long {
    // Ensure the position is within bounds
    require(customPosition + 3 < data.size) { "Position out of bounds for 4-byte read" }

    val byte1 = data[customPosition].toInt()
    val byte2 = data[customPosition + 1].toInt()
    val byte3 = data[customPosition + 2].toInt()
    val byte4 = data[customPosition + 3].toInt()
    val result = (byte1 shl 24) or (byte2 shl 16) or (byte3 shl 8) or byte4
    advancePosition(4)
    return result.toLong()
  }

  /**
   * Reads a string of characters from the [data] array starting at the current position.
   * The length of the string is determined by the [length] parameter, and the bytes are converted to characters.
   *
   * The function reads the specified number of bytes, converts them to [Char] values, and appends them to a result array,
   * which is then converted to a string.
   *
   * @param length The number of bytes to read and convert to characters.
   * @return The string formed by the characters, or throws [IllegalArgumentException] if the length exceeds the available data.
   */
  fun readString(length: Int): String {
    // Ensure the position is within bounds
    require(position + length <= data.size) { "Position out of bounds for string read" }

    val result = CharArray(length)
    for (i in 0 until length) {
      result[i] = data[position + i].toInt().toChar()
    }

    advancePosition(length)
    return String(result)
  }

  /**
   * Reads a sequence of bytes from the current position in the data buffer.
   *
   * This method extracts exactly [numBytes] bytes starting from the current
   * position, advances the internal position cursor by [numBytes], and returns
   * the extracted bytes as a [UByteArray].
   *
   * @param numBytes The number of bytes to read from the current position.
   * @return A [UByteArray] containing the read bytes.
   *
   * @throws IllegalArgumentException if the requested range exceeds the data buffer size.
   */
  fun readBytes(numBytes: Int): UByteArray {
    val start = this.position
    val end = start + numBytes

    require(end <= data.size) { "Requested byte range exceeds buffer size" }

    val result = this.data.sliceArray(start until end)
    advancePosition(numBytes)
    return result
  }

  /**
   * Advances the current position by the specified [length] (in bytes).
   * This is used to move the read position forward as bytes are consumed.
   *
   * @param length The number of bytes to advance the position by. Defaults to 1 byte.
   *
   * @return The position itself. Helps to store it without extra calls.
   */
  fun advancePosition(length: Int = 1): Int {
    require(position + length <= data.size) { "Can not to advance to the next position [${position + length}], it overflows data size [${data.size}]." }
    position += length
    return position
  }
}