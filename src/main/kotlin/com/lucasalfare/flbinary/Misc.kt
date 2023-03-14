@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

import kotlin.math.max
import kotlin.math.min

/**
 * Builds the data visualization on its range designed by the [from] and [to] values.
 *
 * @return the visualization as a [String] value.
 */
fun windowedValues(data: UByteArray, from: Int = 0, to: Int = 0, tableWidth: Int = 10): String {
  val start = max(from, 0)
  val end = min(to, data.size)
  var res = ""
  data
    .slice(start..end)
    .forEachIndexed { index, byte ->
      val byteAsHexadecimal = Integer.toHexString(byte.toInt())
      res += "${byteAsHexadecimal.padStart(2, '0')} "
      if ((index + 1) % tableWidth == 0) {
        res += "\n"
      }
    }

  return res
}
