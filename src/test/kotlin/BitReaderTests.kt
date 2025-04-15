import com.lucasalfare.flbinary.Reader
import com.lucasalfare.flbinary.readBits
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class BitReaderTests {

  // base data bytes for all tests
  private val bytes = "4D 54 68 64 00 00 00 06 00 00 00 01 00 60"
    .split(" ")
    .map { it.toUByte(16) }
    .toUByteArray()

  // base reader object for the tests
  private val reader = Reader(bytes)

  @Test
  fun `test readBits() success`() {
    // we test reading 11 bits to a single variable at once
    val elevenBits = reader.readBits(11)

    // our expected value could have a trailing '0' at start, but is not needed
    assertEquals(0b1001101_010L, elevenBits)
  }
}