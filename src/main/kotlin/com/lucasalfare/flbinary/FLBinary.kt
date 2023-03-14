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
    //position += 1
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
    //position += 2
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
    //position += 3
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
    //position += 4
    advancePosition(4)
    return res.toLong()
  }

  /**
   * This function takes the next values that matches the
   * current position plus [length] param and converts then
   * to its respective [Char] values.
   *
   * After that, those [Chars] are appended to an empty
   * [String], which is the main result of this function.
   */
  fun readString(length: Int): String? {
    if (position + length > data.size) return null
    var result = ""
    data.slice(position..(position - 1 + length))
      .forEach {
        result += Char(it.toInt())
      }
    //position += length
    advancePosition(length)
    return result
  }

  /**
   * This function just skips current reading position to its current
   * position plus the specified [length].
   */
  fun advancePosition(length: Int = 1) {
    this.position += length
  }

  fun windowedValues(from: Int = 0, to: Int = 0): String {
    var res = ""
    data.slice(from..to).forEachIndexed { index, i ->
      res += Integer.toHexString(i.toInt()).padStart(2, '0')
      if ((index + 1) % 10 == 0) {
        res += "\n"
      }
    }
    return res
  }

  /**
   * Prints this writer data as a simple table of max width 10 bytes.
   *
   * Also, the bytes are printed as their hexadecimal representations.
   */
  override fun toString() = windowedValues(from = 0, to = data.size)
}

/**
 * This class encapsulates the task of writing bytes to an single Array.
 *
 * Normally the [data] field represents bytes that should be recorded to an file.
 *
 * This class writes numbers of type [Int] directly as [Int]s to its root data.
 */
class Writer {

  /**
   * The current writing data container.
   */
  private val data = mutableListOf<Int>()

  fun write1Byte(value: Int) {
    data += value
  }

  fun writeBoolean(value: Boolean) {
    this.write1Byte(if (value) 1 else 0)
  }

  fun write2Bytes(value: Int) {
    data += value shr 8
    data += value and 0xff
  }

  fun write3Bytes(value: Int) {
    data += ((value shr 16) and 0xff)
    data += ((value shr 8) and 0xff)
    data += ((value shr 0) and 0xff)
  }

  fun write4Bytes(value: Long) {
    data += ((value shr 24) and 0xff).toInt()
    data += ((value shr 16) and 0xff).toInt()
    data += ((value shr 8) and 0xff).toInt()
    data += ((value shr 0) and 0xff).toInt()
  }

  fun writeString(value: String) {
    value.toCharArray().forEach {
      write1Byte(it.code)
    }
  }

  fun clearWritingData() {
    data.clear()
  }

  fun getData() = data.toIntArray()

  override fun toString(): String {
    var res = ""
    data.forEachIndexed { index, i ->
      res += "0x${Integer.toHexString(i).padStart(2, '0')} "
      if ((index + 1) % 10 == 0) {
        res += "\n"
      }
    }
    return res
  }
}
