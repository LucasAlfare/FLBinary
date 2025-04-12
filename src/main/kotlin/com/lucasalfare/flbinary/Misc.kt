@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

/**
 * Formats a section of a [UByteArray] as a hexadecimal dump with ASCII representation,
 * similar to the output of the `xxd` command-line tool.
 *
 * Each line includes:
 * - The offset (in hexadecimal) of the first byte in the line
 * - A sequence of bytes in hexadecimal, with an extra space after half the width
 * - The corresponding ASCII characters (printable ones only; others shown as '.')
 *
 * Example output:
 * ```
 * 00000000  48 65 6C 6C 6F 2C 20 77  6F 72 6C 64 21 0A       Hello, world!.
 * ```
 *
 * @param data The byte array to visualize.
 * @param from The starting index (inclusive). Defaults to 0.
 * @param to The ending index (exclusive). Defaults to [data].size.
 * @param tableWidth The number of bytes per line. Defaults to 16.
 * @return A string containing the formatted hex+ASCII dump.
 */
fun Reader.toHexView(
  data: UByteArray = this.data,
  from: Int = 0,
  to: Int = this.data.size,
  tableWidth: Int = 16
): String {
  val start = from.coerceAtLeast(0)
  val end = to.coerceAtMost(data.size)
  if (start >= end) return ""

  return buildString {
    var i = start
    while (i < end) {
      // Offset
      append("%08X  ".format(i))

      val lineEnd = (i + tableWidth).coerceAtMost(end)

      // Hex section
      for (j in i until lineEnd) {
        append("%02X ".format(data[j]))
      }

      // Padding if last line is shorter
      val missing = tableWidth - (lineEnd - i)
      repeat(missing) { append("   ") }

      append(" ")

      // ASCII section
      for (j in i until lineEnd) {
        val c = data[j].toInt().toChar()
        append(if (c.isLetterOrDigit() || c.isWhitespace() || c in '!'..'~') c else '.')
      }

      append('\n')
      i += tableWidth
    }
  }
}

/**
 * Formats a section of a [UByteArray] as a hexadecimal dump with ASCII representation,
 * similar to the output of the `xxd` command-line tool.
 *
 * Each line includes:
 * - The offset (in hexadecimal) of the first byte in the line
 * - A sequence of bytes in hexadecimal, with an extra space after half the width
 * - The corresponding ASCII characters (printable ones only; others shown as '.')
 *
 * Example output:
 * ```
 * 00000000  48 65 6C 6C 6F 2C 20 77  6F 72 6C 64 21 0A       Hello, world!.
 * ```
 *
 * @param data The byte array to visualize.
 * @param from The starting index (inclusive). Defaults to 0.
 * @param to The ending index (exclusive). Defaults to [data].size.
 * @param tableWidth The number of bytes per line. Defaults to 16.
 * @return A string containing the formatted hex+ASCII dump.
 */
fun Writer.toHexView(
  data: UByteArray = this.getData(),
  from: Int = 0,
  to: Int = this.getData().size,
  tableWidth: Int = 16
): String {
  val start = from.coerceAtLeast(0)
  val end = to.coerceAtMost(data.size)
  if (start >= end) return ""

  return buildString {
    var i = start
    while (i < end) {
      // Offset
      append("%08X  ".format(i))

      val lineEnd = (i + tableWidth).coerceAtMost(end)

      // Hex section
      for (j in i until lineEnd) {
        append("%02X ".format(data[j]))
      }

      // Padding if last line is shorter
      val missing = tableWidth - (lineEnd - i)
      repeat(missing) { append("   ") }

      append(" ")

      // ASCII section
      for (j in i until lineEnd) {
        val c = data[j].toInt().toChar()
        append(if (c.isLetterOrDigit() || c.isWhitespace() || c in '!'..'~') c else '.')
      }

      append('\n')
      i += tableWidth
    }
  }
}