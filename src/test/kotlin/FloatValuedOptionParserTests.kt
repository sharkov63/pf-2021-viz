import kotlin.test.*

internal class FloatValuedOptionParserTests {

    private val tolerance = 0.001f
    private val defaultValue = 400f
    private val minValue = 100f
    private val maxValue = 2000f
    private val parser = FloatValuedOptionParser(
        "some value",
        defaultValue,
        minValue,
        maxValue
    )

    @Test
    fun `Float valued option parser null`() {
        assertEquals(defaultValue, parser.parse(null), tolerance)
    }

    @Test
    fun `Float valued option parser`() {
        assertEquals(200f, parser.parse("200"), tolerance)
    }

    @Test
    fun `Float valued option parser fractional`() {
        assertEquals(239.2007f, parser.parse("239.2007"), tolerance)
    }

    @Test
    fun `Float valued option parser zero`() {
        assertEquals(minValue, parser.parse("0"), tolerance)
    }

    @Test
    fun `Float valued option parser negative`() {
        assertEquals(minValue, parser.parse("-300"), tolerance)
    }

    @Test
    fun `Float valued option parser too small`() {
        assertEquals(minValue, parser.parse("10"), tolerance)
    }

    @Test
    fun `Float valued option parser NaN`() {
        assertEquals(defaultValue, parser.parse("abracadabra"), tolerance)
    }

    @Test
    fun `Float valued option parser Float-NaN`() {
        assertEquals(defaultValue, parser.parse("NaN"), tolerance)
    }

    @Test
    fun `Float valued option parser Infinity`() {
        assertEquals(maxValue, parser.parse("Infinity"), tolerance)
    }

    @Test
    fun `Float valued option parser too large`() {
        assertEquals(maxValue, parser.parse("1000000"), tolerance)
    }
}