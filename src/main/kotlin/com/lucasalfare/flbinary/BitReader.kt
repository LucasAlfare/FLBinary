package com.lucasalfare.flbinary

import kotlin.math.ceil

/**
 * Reads an arbitrary number of bits (from 1 to 63) from the current position in a [Reader],
 * returning the result as a [Long] value.
 *
 * This function reads enough bytes to cover the requested number of bits, then extracts the
 * exact bits requested, discarding any extra bits from the most significant side (left).
 *
 * Due to the use of `readBytes()` in this function, the internal position cursor of the related
 * [Reader] is advanced, then this must be taken in account.
 *
 * @param numBits The number of bits to read, must be between 1 and 63 (inclusive).
 *
 * @return A [Long] value containing the `numBits` read from the stream. The bits are packed into the
 * least significant bits of the returned `Long`. For example, if `numBits` is 5, the result
 * will be a value from 0 to 31, with the 5 relevant bits stored in the lower portion of the `Long`.
 *
 * @throws IllegalArgumentException if `numBits` is not within the range [1..63].
 *
 * ### Example
 * Suppose the underlying bytes are:
 * ```
 * 0b11110000, 0b10101010  // 2 bytes: 1111000010101010
 * ```
 * Calling `readBits(12)` would:
 * - Read both bytes into a 16-bit buffer: `0b1111000010101010`
 * - Discard 4 leading bits (16 - 12): final result = `0b000010101010` (0x0AA)
 *
 * ### Notes
 * - This function assumes big-endian bit order: bits are read from most significant to least significant.
 * - The caller is responsible for ensuring that the reader is correctly positioned at the desired read point.
 */
@ExperimentalUnsignedTypes
fun Reader.readBits(numBits: Int): Long {
  require(numBits in 1..63) { "[numBits] must be between 1 and 63 to fit in a single [Long] value." }

  // we ceil (up) to know how many bytes is needed to retrieve all requested bits!
  val nBytesNeeded = ceil(numBits / 8.0).toInt()
  val bytes = this.readBytes(nBytesNeeded)

  var result = 0L
  for (byte in bytes) {
    result = (result shl 8) or (byte.toInt() and 0xFF).toLong()
  }

  val totalBitsRead = nBytesNeeded * 8
  val bitsToDiscard = (totalBitsRead - numBits)

  result = result shr bitsToDiscard
  return result
}