import com.lucasalfare.flbinary.Reader
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

@OptIn(ExperimentalUnsignedTypes::class)
class ReaderTests {

  // base data bytes for all tests
  private val bytes = "4D 54 68 64 00 00 00 06 00 00 00 01 00 60"
    .split(" ")
    .map { it.toUByte(16) }
    .toUByteArray()

  // base reader object for the tests
  private val reader = Reader(bytes)

  @BeforeTest
  fun setup() {
    reader.position = 0
  }

  @Test
  fun `test testAdvancePosition() success`() {
    // obviously we can not perform more readings after advancing to the end
    assertDoesNotThrow { reader.advancePosition(bytes.size) }
  }

  @Test
  fun `test testAdvancePosition() failure`() {
    assertThrows<IllegalArgumentException> { reader.advancePosition(50) }
  }

  @Test
  fun `readBoolean returns true when byte is 1`() {
    reader.advancePosition(11)
    assertTrue(reader.readBoolean())
  }

  @Test
  fun `readBoolean returns false when byte is 0`() {
    reader.advancePosition(10)
    assertFalse(reader.readBoolean())
  }

  @Test
  fun `readBoolean throws when invalid byte and strictMode is true`() {
    reader.advancePosition(bytes.size - 1)
    assertThrows<NoSuchElementException> {
      reader.readBoolean(strictMode = true)
    }
  }

  @Test
  fun `readBoolean returns false when invalid byte and strictMode is false`() {
    reader.advancePosition(bytes.size - 1)
    assertFalse(reader.readBoolean(strictMode = false))
  }

  @Test
  fun `test read2Bytes() success`() {
    assertTrue(reader.read2Bytes() == 0x4D54)
  }

  @Test
  fun `test read3Bytes() success`() {
    assertTrue(reader.read3Bytes() == 0x4D5468)
  }

  @Test
  fun `test read4Bytes() success`() {
    assertTrue(reader.read4Bytes() == 0x4D546864L)
  }

  @Test
  fun `readString reads correct characters`() {
    reader.advancePosition(0)
    val str = reader.readString(2)
    assertEquals("MT", str)
  }

  @Test
  fun `readString reads full header chunk`() {
    reader.advancePosition(0)
    val str = reader.readString(4)
    assertEquals("MThd", str)
  }

  @Test
  fun `readString updates reader position`() {
    reader.readString(4)
    assertEquals(4, reader.position)
  }

  @Test
  fun `readString throws when reading past end`() {
    reader.advancePosition(bytes.size - 2)
    assertThrows<IllegalArgumentException> {
      reader.readString(3)
    }
  }

  @Test
  fun `readBytes reads correct number of bytes`() {
    reader.advancePosition(0)
    val result = reader.readBytes(4)
    assertContentEquals(ubyteArrayOf(0x4Du, 0x54u, 0x68u, 0x64u), result)
  }

  @Test
  fun `readBytes returns empty array when reading zero bytes`() {
    val result = reader.readBytes(0)
    assertTrue(result.isEmpty())
  }

  @Test
  fun `readBytes updates reader position after read`() {
    reader.readBytes(3)
    assertEquals(3, reader.position)
  }

  @Test
  fun `readBytes throws when reading past the end of data`() {
    reader.advancePosition(bytes.size - 2)
    assertThrows<IllegalArgumentException> {
      reader.readBytes(3)
    }
  }
}