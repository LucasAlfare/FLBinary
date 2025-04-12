@file:Suppress("MemberVisibilityCanBePrivate")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

/**
 * This class encapsulates the task of reading bytes to an single Array.
 *
 * This classes uses as main data source an array of type [UByteArray] in
 * order to avoid cast/conversions problems. For example, the hex value
 * [0xCC], when converted to Int, is turned to something like [0xFFFFFFCC].
 *
 * This result is totally expected by the [Int] type specification however,
 * this is not appropriated to application. Then, to avoid this, using
 * _unsigned bytes_ should be fine.
 *
 * Finally, the methods are written in the form "readXByte", this means that
 * the method will take a sequence of that amount of elements from the [data]
 * field and merge those into a single number.
 */
class Reader(var data: UByteArray) {

  /**
   * This field hold the current position of where the
   * bytes are being stored.
   */
  var position = 0

  /**
   * Reads a single byte in the specified position.
   * The return type is Int.
   */
  fun read1Byte(customPosition: Int = position): Int {
    val a = data[customPosition]
    val res = a.toInt()
    advancePosition()
    return res
  }

  /**
   * Reads a single byte in the specified position
   * and interprets it as a Boolean value.
   *
   * Returns [true] if current byte is equals to 1 or [false]
   * if current byte is equals to 0.
   */
  fun readBoolean(customPosition: Int = position) = read1Byte(customPosition) == 1

  /**
   * Reads the next 2 bytes (counting from current position)
   * and packs then into a single number of type [Int].
   *
   * This function automatically advances the current position.
   */
  fun read2Bytes(customPosition: Int = position): Int {
    val a = data[customPosition + 0].toInt()
    val b = data[customPosition + 1].toInt()
    val res = (a shl 8) or b
    advancePosition(2)
    return res
  }

  /**
   * Reads the next 3 bytes (counting from current position)
   * and packs then into a single number of type [Int].
   *
   * This function automatically advances the current position.
   */
  fun read3Bytes(customPosition: Int = position): Int {
    val a = data[customPosition + 0].toInt()
    val b = data[customPosition + 1].toInt()
    val c = data[customPosition + 2].toInt()
    val res = ((a shl 16) or ((b shl 8))) or c
    advancePosition(3)
    return res
  }

  /**
   * Reads the next 4 bytes (counting from current position)
   * and packs then into a single number of type [Long].
   *
   * This function automatically advances the current position.
   */
  fun read4Bytes(customPosition: Int = position): Long {
    val a = data[customPosition + 0].toInt()
    val b = data[customPosition + 1].toInt()
    val c = data[customPosition + 2].toInt()
    val d = data[customPosition + 3].toInt()
    val res = (((a shl 24) or (b shl 16)) or (c shl 8)) or d
    advancePosition(4)
    return res.toLong()
  }

  /**
   * This function takes the next values that match the
   * current position plus the [length] parameter and converts them
   * to their respective [Char] values.
   *
   * After that, those [Char]s are appended to a result array,
   * which is converted to a string at the end.
   */
  fun readString(length: Int): String? {
    if (position + length > data.size) return null

    // Directly create a character array for the result
    val result = CharArray(length)

    // Fill the result array directly
    for (i in 0 until length) {
      result[i] = data[position + i].toInt().toChar()
    }

    advancePosition(length)
    return String(result)
  }

  /**
   * This function just skips current reading position to its current
   * position plus the specified [length].
   */
  fun advancePosition(length: Int = 1) {
    this.position += length
  }
}