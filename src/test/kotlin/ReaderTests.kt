import com.lucasalfare.flbinary.Reader
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class ReaderTests {

  @Test
  fun testAdvancePosition() {
    val reader = Reader(UByteArray(10) { it.toUByte() })

    // verify initial position
    assertEquals(0, reader.position)

    // advances 1 unit
    reader.advancePosition(1)
    assertEquals(1, reader.position)

    // advances 2 units
    reader.advancePosition(2)
    assertEquals(3, reader.position)

    // advances 3 units
    reader.advancePosition(3)
    assertEquals(6, reader.position)
  }

  @Test
  fun testRead1Byte() {
    val reader = Reader(UByteArray(5) { it.toUByte() })

    // Read the first byte, which is 0
    val byte = reader.read1Byte()
    assertEquals(0, byte)
  }

  @Test
  fun testRead2Bytes() {
    val reader = Reader(UByteArray(5) { it.toUByte() })

    // Read 2 bytes (0 and 1), forming the integer 0x0001
    val result = reader.read2Bytes()
    assertEquals(0x0001, result)
  }

  @Test
  fun testRead3Bytes() {
    val reader = Reader(UByteArray(5) { it.toUByte() })

    // Read 3 bytes (0, 1, and 2), forming the integer 0x000102
    val result = reader.read3Bytes()
    assertEquals(0x000102, result)
  }

  @Test
  fun testRead4Bytes() {
    val reader = Reader(UByteArray(5) { it.toUByte() })

    // Read 4 bytes (0, 1, 2, and 3), forming the long integer 0x00010203
    val result = reader.read4Bytes()
    assertEquals(0x00010203, result)
  }
}