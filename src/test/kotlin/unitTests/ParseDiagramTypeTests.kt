package unitTests

import DEFAULT_DIAGRAM_TYPE
import DiagramType
import diagramTypeByDescription
import parseDiagramType
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParseDiagramTypeTests {

    @Test
    fun `Parse diagram type null`() {
        assertEquals(DEFAULT_DIAGRAM_TYPE, parseDiagramType(null))
    }

    @Test
    fun `Parse diagram type all`() {
        for ((description, type) in diagramTypeByDescription) {
            assertEquals(type, parseDiagramType(description))
        }
    }

    @Test
    fun `Parse diagram type ignore case`() {
        assertEquals(DiagramType.PIE, parseDiagramType("PIE"))
        assertEquals(DiagramType.LINE, parseDiagramType("Line"))
        assertEquals(DiagramType.BAR, parseDiagramType("hIsToGrAm"))
    }

    @Test
    fun `Parse diagram type unknown keyword`() {
        assertEquals(DEFAULT_DIAGRAM_TYPE, parseDiagramType("abracadara"))
        assertEquals(DEFAULT_DIAGRAM_TYPE, parseDiagramType("xyz0123"))
    }
}