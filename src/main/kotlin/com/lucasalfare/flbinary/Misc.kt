@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalUnsignedTypes::class)

package com.lucasalfare.flbinary

/**
 * Generates a hex dump of the given [UByteArray], similar to the output of the `xxd` command-line tool.
 *
 * Each line of the output includes:
 * - The offset in hexadecimal (8 digits),
 * - A group of up to [bytesPerLine] bytes in hexadecimal format, separated by spaces,
 * - A textual representation of the bytes, where non-printable characters are replaced with a dot (`.`).
 *
 * The format for each line is:
 * ```
 * 00000000 | 48 65 6C 6C 6F 20 77 6F 72 6C 64 21       | Hello world!
 * ```
 *
 * @param data The [UByteArray] to be dumped.
 * @param bytesPerLine The number of bytes to display per line (default is 16).
 * @return A [String] representing the formatted hex dump.
 */
fun hexDump(data: UByteArray, bytesPerLine: Int = 16): String = buildString {
  val totalLines = (data.size + bytesPerLine - 1) / bytesPerLine

  for (line in 0 until totalLines) {
    val offset = line * bytesPerLine
    val end = minOf(offset + bytesPerLine, data.size)
    val lineBytes = data.slice(offset until end)

    append("%08X".format(offset)).append(" | ")

    for (i in 0 until bytesPerLine) {
      append(
        if (offset + i < data.size)
          "%02X ".format(data[offset + i].toInt())
        else
          "   "
      )
    }

    append("| ")

    for (byte in lineBytes) {
      val c = byte.toInt().toChar()
      append(if (c.isLetterOrDigit() || c in ' '..'~') c else '.')
    }

    appendLine()
  }
}

// Extensions
fun Reader.toHexView(bytesPerLine: Int = 16): String =
  hexDump(this.data, bytesPerLine)

fun Writer.toHexView(bytesPerLine: Int = 16): String =
  hexDump(this.getData(), bytesPerLine)
