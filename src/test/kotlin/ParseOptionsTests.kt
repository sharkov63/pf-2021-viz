import java.io.File
import kotlin.test.*

internal class ParseOptionsTests {

    @Test
    fun `Empty args`() {
        assertEquals(
            Options(null, DEFAULT_DIAGRAM_TYPE, null),
            parseOptions(listOf())
        )
    }

    @Test
    fun `Input file -i`() {
        assertEquals(
            Options(File("file.txt"), DEFAULT_DIAGRAM_TYPE, null),
            parseOptions(listOf("-i", "file.txt"))
        )
    }

    @Test
    fun `Input file --input`() {
        assertEquals(
            Options(File("file.txt"), DEFAULT_DIAGRAM_TYPE, null),
            parseOptions(listOf("--input", "file.txt"))
        )
    }

    @Test
    fun `Input file with spaces`() {
        assertEquals(
            Options(File("file with spaces.txt"), DEFAULT_DIAGRAM_TYPE, null),
            parseOptions(listOf("-i", "file with spaces.txt"))
        )
    }

    @Test
    fun `Input file long path`() {
        assertEquals(
            Options(File("dir/subdir/subsubdir/data"), DEFAULT_DIAGRAM_TYPE, null),
            parseOptions(listOf("-i", "dir/subdir/subsubdir/data"))
        )
    }

    @Test
    fun `Diagram type -d`() {
        assertEquals(
            Options(null, DiagramType.BAR, null),
            parseOptions(listOf("-d", "bar"))
        )
    }

    @Test
    fun `Diagram type --diagram`() {
        assertEquals(
            Options(null, DiagramType.LINE, null),
            parseOptions(listOf("--diagram", "line"))
        )
    }

    @Test
    fun `Diagram type all descriptions`() {
        diagramTypeByDescription.forEach { (desc, type) ->
            assertEquals(
                Options(null, type,null),
                parseOptions(listOf("-d", desc))
            )
        }
    }

    @Test
    fun `Diagram type invalid description`() {
        assertEquals(
            Options(null, DEFAULT_DIAGRAM_TYPE, null),
            parseOptions(listOf("-d", "abracadabra"))
        )
    }

    @Test
    fun `Diagram type ignore case`() {
        assertEquals(
            Options(null, DiagramType.PIE, null),
            parseOptions(listOf("-d", "PIE"))
        )
        assertEquals(
            Options(null, DiagramType.LINE, null),
            parseOptions(listOf("-d", "Line"))
        )
        assertEquals(
            Options(null, DiagramType.BAR, null),
            parseOptions(listOf("-d", "hIsToGrAm"))
        )
    }

    @Test
    fun `Output file -o`() {
        assertEquals(
            Options(null, DEFAULT_DIAGRAM_TYPE, File("diagram.png")),
            parseOptions(listOf("-o", "diagram.png"))
        )
    }

    @Test
    fun `Output file --output`() {
        assertEquals(
            Options(null, DEFAULT_DIAGRAM_TYPE, File("diagram.png")),
            parseOptions(listOf("--output", "diagram.png"))
        )
    }

    @Test
    fun `Output file with spaces`() {
        assertEquals(
            Options(null, DEFAULT_DIAGRAM_TYPE, File("very cool diagram.png")),
            parseOptions(listOf("-o", "very cool diagram.png"))
        )
    }

    @Test
    fun `Output file long path`() {
        assertEquals(
            Options(null, DEFAULT_DIAGRAM_TYPE, File("documents/reports/2020/october/visualized data.png")),
            parseOptions(listOf("-o", "documents/reports/2020/october/visualized data.png"))
        )
    }

    @Test
    fun `Combined arguments`() {
        assertEquals(
            Options(File("in.txt"), DiagramType.PIE, null),
            parseOptions(listOf("-i", "in.txt", "-d", "pie"))
        )
        assertEquals(
            Options(File("in.txt"), DiagramType.LINE, null),
            parseOptions(listOf("-d", "line", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_DIAGRAM_TYPE, File("out.png")),
            parseOptions(listOf("-i", "in.txt", "-o", "out.png"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_DIAGRAM_TYPE, File("out.png")),
            parseOptions(listOf("-o", "out.png", "-i", "in.txt"))
        )
        assertEquals(
            Options(null, DiagramType.BAR, File("out.png")),
            parseOptions(listOf("-d", "bar", "-o", "out.png"))
        )
        assertEquals(
            Options(null, DiagramType.LINE, File("out.png")),
            parseOptions(listOf("-o", "out.png", "-d", "line"))
        )
        assertEquals(
            Options(File("in.txt"), DiagramType.PIE, File("out.png")),
            parseOptions(listOf("-i", "in.txt", "-d", "pie", "-o", "out.png"))
        )
        assertEquals(
            Options(File("in.txt"), DiagramType.PIE, File("out.png")),
            parseOptions(listOf("-i", "in.txt", "-o", "out.png", "-d", "pie"))
        )
        assertEquals(
            Options(File("in.txt"), DiagramType.PIE, File("out.png")),
            parseOptions(listOf("-d", "pie", "-i", "in.txt", "-o", "out.png"))
        )
        assertEquals(
            Options(File("in.txt"), DiagramType.PIE, File("out.png")),
            parseOptions(listOf("-d", "pie", "-o", "out.png", "-i", "in.txt"))
        )
    }

    @Test
    fun `Invalid arguments`() {
        assertNull(parseOptions(listOf("-i")))
        assertNull(parseOptions(listOf("-d")))
        assertNull(parseOptions(listOf("-o")))
        assertNull(parseOptions(listOf("-k", "something")))
        assertNull(parseOptions(listOf("-i", "-d", "line")))
        assertNull(parseOptions(listOf("-i", "in.txt", "-o", "-t", "line")))
        assertNull(parseOptions(listOf("", "", "", "")))
    }

    @Test
    fun `Contradicting options`() {
        // Currently, only the last one is evaluated
        assertEquals(
            Options(null, DiagramType.LINE, null),
            parseOptions(listOf("-d", "pie", "-d", "line"))
        )
    }
}
