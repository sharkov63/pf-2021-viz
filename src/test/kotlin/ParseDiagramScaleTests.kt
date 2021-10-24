import kotlin.test.*

internal class ParseDiagramScaleTests {

    private val tolerance = 0.001f

    @Test
    fun `Parse diagram scale null`() {
        assertEquals(DEFAULT_DIAGRAM_SCALE, parseDiagramScale(null), tolerance)
    }

    @Test
    fun `Parse diagram scale`() {
        assertEquals(200f, parseDiagramScale("200"), tolerance)
    }

    @Test
    fun `Parse diagram scale fractional`() {
        assertEquals(239.2007f, parseDiagramScale("239.2007"), tolerance)
    }

    @Test
    fun `Diagram scale zero`() {
        assertEquals(MIN_DIAGRAM_SCALE, parseDiagramScale("0"), tolerance)
    }

    @Test
    fun `Diagram scale negative`() {
        assertEquals(MIN_DIAGRAM_SCALE, parseDiagramScale("-300"), tolerance)
    }

    @Test
    fun `Diagram scale too small`() {
        assertEquals(MIN_DIAGRAM_SCALE, parseDiagramScale("10"), tolerance)
    }

    @Test
    fun `Diagram scale NaN`() {
        assertEquals(DEFAULT_DIAGRAM_SCALE, parseDiagramScale("abracadabra"), tolerance)
    }

    @Test
    fun `Diagram scale Float-NaN`() {
        assertEquals(DEFAULT_DIAGRAM_SCALE, parseDiagramScale("NaN"), tolerance)
    }

    @Test
    fun `Diagram scale Infinity`() {
        assertEquals(MAX_DIAGRAM_SCALE, parseDiagramScale("Infinity"), tolerance)
    }

    @Test
    fun `Diagram scale too large`() {
        assertEquals(MAX_DIAGRAM_SCALE, parseDiagramScale("1000000"), tolerance)
    }
}