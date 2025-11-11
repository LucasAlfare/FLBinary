@file:OptIn(ExperimentalUnsignedTypes::class)

import com.lucasalfare.flbinary.Writer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class WriterTest {

  @Test
  fun `write1Byte appends single byte and tell reports size`() {
    val w = Writer()
    w.write1Byte(0xFF)
    assertEquals(1, w.tell())
    assertArrayEquals(byteArrayOf(0xFF.toByte()), w.toByteArray())
  }

  @Test
  fun `write1Byte throws on out of range values`() {
    val w = Writer()
    assertThrows(IllegalArgumentException::class.java) { w.write1Byte(-1) }
    assertThrows(IllegalArgumentException::class.java) { w.write1Byte(256) }
  }

  @Test
  fun `writeBoolean writes 1 for true and 0 for false`() {
    val w = Writer()
    w.writeBoolean(true)
    w.writeBoolean(false)
    assertArrayEquals(byteArrayOf(1, 0), w.toByteArray())
  }

  @Test
  fun `write2Bytes writes big endian two bytes`() {
    val w = Writer()
    w.write2Bytes(0x1234)
    assertArrayEquals(byteArrayOf(0x12.toByte(), 0x34.toByte()), w.toByteArray())
  }

  @Test
  fun `write2Bytes throws on out of range`() {
    val w = Writer()
    assertThrows(IllegalArgumentException::class.java) { w.write2Bytes(-1) }
    assertThrows(IllegalArgumentException::class.java) { w.write2Bytes(0x1_0000) } // 65536
  }

  @Test
  fun `write3Bytes writes big endian three bytes`() {
    val w = Writer()
    w.write3Bytes(0x012345)
    assertArrayEquals(byteArrayOf(0x01, 0x23, 0x45), w.toByteArray())
  }

  @Test
  fun `write3Bytes throws on out of range`() {
    val w = Writer()
    assertThrows(IllegalArgumentException::class.java) { w.write3Bytes(-1) }
    assertThrows(IllegalArgumentException::class.java) { w.write3Bytes(0x1_000000) } // > 24 bits
  }

  @Test
  fun `write4Bytes writes big endian four bytes for full unsigned range`() {
    val w = Writer()
    w.write4Bytes(0x89ABCDEFuL.toLong() and 0xFFFFFFFFL) // example value
    assertArrayEquals(
      byteArrayOf(0x89.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte()),
      w.toByteArray()
    )
    // test max unsigned 32 bit
    val w2 = Writer()
    w2.write4Bytes(0xFFFFFFFFL)
    assertArrayEquals(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()), w2.toByteArray())
  }

  @Test
  fun `write4Bytes throws when value too large or negative`() {
    val w = Writer()
    assertThrows(IllegalArgumentException::class.java) { w.write4Bytes(-1L) }
    assertThrows(IllegalArgumentException::class.java) { w.write4Bytes(0x1_0000_0000L) } // > 0xFFFFFFFF
  }

  @Test
  fun `writeBytes appends array contents`() {
    val w = Writer()
    val arr = ubyteArrayOf(0u, 1u, 255u)
    w.writeBytes(arr)
    assertArrayEquals(byteArrayOf(0, 1, (-1).toByte()), w.toByteArray())
  }

  @Test
  fun `writeBytes handles empty array`() {
    val w = Writer()
    w.writeBytes(UByteArray(0))
    assertEquals(0, w.tell())
  }

  @Test
  fun `writeString writes low byte of each char`() {
    val w = Writer()
    // 'A' = 65, 'Ā' = U+0100 -> low byte 0x00
    w.writeString("AĀ")
    val expected = byteArrayOf(65.toByte(), 0.toByte())
    assertArrayEquals(expected, w.toByteArray())
  }

  @Test
  fun `toUByteArray returns copy not affected by later writes`() {
    val w = Writer()
    w.write1Byte(10)
    val copy = w.toUByteArray()
    w.write1Byte(20)
    // original copy still length 1
    assertEquals(1, copy.size)
    // writer now has 2 bytes
    assertEquals(2, w.tell())
    // ensure returned copy values are correct and unchanged
    assertArrayEquals(byteArrayOf(10.toByte()), copy.toByteArray())
  }

  @Test
  fun `toByteArray converts values correctly signed`() {
    val w = Writer()
    w.write1Byte(0x80) // unsigned 128
    val signed = w.toByteArray()
    assertEquals((-128).toByte(), signed[0]) // compare como Byte assinado
  }

  @Test
  fun `clear empties buffer and resets tell`() {
    val w = Writer()
    w.write1Byte(1)
    w.write1Byte(2)
    assertEquals(2, w.tell())
    w.clear()
    assertEquals(0, w.tell())
    assertArrayEquals(byteArrayOf(), w.toByteArray())
  }

  @Test
  fun `saveToFile writes file bytes correctly`(@TempDir tempDir: Path) {
    val w = Writer()
    w.writeString("ABC")
    val file = tempDir.resolve("writer_test.bin")
    w.saveToFile(file.toString())
    val read = Files.readAllBytes(file)
    assertArrayEquals(byteArrayOf(65, 66, 67), read)
  }

  @Test
  fun `buildChunk composes id length and body correctly`() {
    val body = ubyteArrayOf(1u, 2u, 3u)
    val chunk = Writer.buildChunk("ID", body)
    // expected: 'I' 'D' then 4 byte length (0x00 00 00 03) then body
    val expected = ubyteArrayOf(
      'I'.code.toUByte(),
      'D'.code.toUByte(),
      0u, 0u, 0u, 3u,
      1u, 2u, 3u
    )
    assertArrayEquals(expected.toByteArray(), chunk.toByteArray())
  }

  @Test
  fun `buildChunk throws when id is empty`() {
    val body = ubyteArrayOf(1u)
    assertThrows(IllegalArgumentException::class.java) { Writer.buildChunk("", body) }
  }

  @Test
  fun `toUByteArray and toByteArray produce independent copies`() {
    val w = Writer()
    w.write1Byte(5)
    val ucopy = w.toUByteArray()
    val bcopy = w.toByteArray()
    // mutate writer
    w.write1Byte(6)
    // copies must remain unchanged
    assertArrayEquals(byteArrayOf(5), ucopy.toByteArray())
    assertArrayEquals(byteArrayOf(5), bcopy)
  }
}