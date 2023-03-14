@file:Suppress("MemberVisibilityCanBePrivate")
@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

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

  fun getData(): UByteArray {
    val res = UByteArray(data.size)
    data.toIntArray().forEachIndexed { index, i ->
      res[index] = i.toUByte()
    }
    return res
  }

  override fun toString() = windowedValues(data = getData(), from = 0, to = data.size)
}
