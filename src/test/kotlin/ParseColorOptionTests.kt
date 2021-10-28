import kotlin.test.*

internal class ParseColorOptionTests {

    @Test
    fun `Parse color option null`() {
        assertEquals(DEFAULT_COLOR_OPTION, parseColorOption(null))
    }

    @Test
    fun `Parse color option keywords`() {
        wordColorDescriptions.forEach { (keyword, color) ->
            assertEquals(color, parseColorOption(keyword))
        }
    }

    @Test
    fun `Parse color option keywords upper case`() {
        assertEquals(wordColorDescriptions["red"]!!, parseColorOption("Red"))
        assertEquals(wordColorDescriptions["green"]!!, parseColorOption("GREEN"))
        assertEquals(wordColorDescriptions["purple"]!!, parseColorOption("pUrPlE"))
    }

    @Test
    fun `Parse color option HEX codes`() {
        assertEquals(0x012345, parseColorOption("012345"))
        assertEquals(0xabcdef, parseColorOption("#abcdef"))
        assertEquals(0xba9876, parseColorOption("ba9876"))
        assertEquals(0xffaabb, parseColorOption("fFaAbB"))
        assertEquals(0xddccee, parseColorOption("#DdCcEe"))
    }

    @Test
    fun `Parse sort option unknown keyword`() {
        assertEquals(DEFAULT_COLOR_OPTION, parseColorOption("abracadabra"))
    }
}