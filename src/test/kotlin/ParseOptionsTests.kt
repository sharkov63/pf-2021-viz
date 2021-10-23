import java.io.File
import kotlin.test.*

internal class ParseOptionsTests {

    @Test
    fun `Empty args`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf())
        )
    }

    @Test
    fun `Input file -i`() {
        assertEquals(
            Options(File("file.txt"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-i", "file.txt"))
        )
    }

    @Test
    fun `Input file --input`() {
        assertEquals(
            Options(File("file.txt"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--input", "file.txt"))
        )
    }

    @Test
    fun `Input file with spaces`() {
        assertEquals(
            Options(File("file with spaces.txt"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-i", "file with spaces.txt"))
        )
    }

    @Test
    fun `Input file long path`() {
        assertEquals(
            Options(File("dir/subdir/subsubdir/data"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-i", "dir/subdir/subsubdir/data"))
        )
    }

    @Test
    fun `Sort option --no-sort`() {
        assertEquals(
            Options(null, SortOption.NONE, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--no-sort"))
        )
    }

    @Test
    fun `Sort option --nsort`() {
        assertEquals(
            Options(null, SortOption.NONE, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--nsort"))
        )
    }

    @Test
    fun `Sort option --sort`() {
        assertEquals(
            Options(null, SortOption.SORT, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--sort"))
        )
    }

    @Test
    fun `Sort option --reverse-sort`() {
        assertEquals(
            Options(null, SortOption.REVERSE, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--reverse-sort"))
        )
    }

    @Test
    fun `Sort option --rsort`() {
        assertEquals(
            Options(null, SortOption.REVERSE, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--rsort"))
        )
    }

    @Test
    fun `Diagram type -d`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.BAR, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "bar"))
        )
    }

    @Test
    fun `Diagram type --diagram`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.LINE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("--diagram", "line"))
        )
    }

    @Test
    fun `Diagram type all descriptions`() {
        diagramTypeByDescription.forEach { (desc, type) ->
            assertEquals(
                Options(null, DEFAULT_SORT_OPTION, type, DEFAULT_DIAGRAM_SCALE,null),
                parseOptions(listOf("-d", desc))
            )
        }
    }

    @Test
    fun `Diagram type invalid description`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "abracadabra"))
        )
    }

    @Test
    fun `Diagram type ignore case`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.PIE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "PIE"))
        )
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.LINE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "Line"))
        )
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.BAR, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "hIsToGrAm"))
        )
    }

    @Test
    fun `Diagram scale -s`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, 100f, null),
            parseOptions(listOf("-s", "100"))
        )
    }

    @Test
    fun `Diagram scale --scale`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, 200f, null),
            parseOptions(listOf("--scale", "200"))
        )
    }

    @Test
    fun `Diagram scale fractional`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, 239.2007f, null),
            parseOptions(listOf("-s", "239.2007"))
        )
    }

    @Test
    fun `Diagram scale zero`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, MIN_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "0"))
        )
    }

    @Test
    fun `Diagram scale negative`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, MIN_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "-300"))
        )
    }

    @Test
    fun `Diagram scale too small`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, MIN_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "10"))
        )
    }

    @Test
    fun `Diagram scale NaN`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "abracadabra"))
        )
    }

    @Test
    fun `Diagram scale Float-NaN`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "NaN"))
        )
    }

    @Test
    fun `Diagram scale Infinity`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, MAX_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "Infinity"))
        )
    }

    @Test
    fun `Diagram scale too large`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, MAX_DIAGRAM_SCALE, null),
            parseOptions(listOf("-s", "1000000"))
        )
    }

    @Test
    fun `Output file -o`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, File("diagram.png")),
            parseOptions(listOf("-o", "diagram.png"))
        )
    }

    @Test
    fun `Output file --output`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, File("diagram.png")),
            parseOptions(listOf("--output", "diagram.png"))
        )
    }

    @Test
    fun `Output file with spaces`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, File("very cool diagram.png")),
            parseOptions(listOf("-o", "very cool diagram.png"))
        )
    }

    @Test
    fun `Output file long path`() {
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, File("documents/reports/2020/october/visualized data.png")),
            parseOptions(listOf("-o", "documents/reports/2020/october/visualized data.png"))
        )
    }

    @Test
    fun `Combined arguments`() {
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.PIE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-i", "in.txt", "-d", "pie"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.LINE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "line", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, 200f, null),
            parseOptions(listOf("-i", "in.txt", "-s", "200"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-i", "in.txt", "-o", "out.png"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DEFAULT_DIAGRAM_TYPE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-o", "out.png", "-i", "in.txt"))
        )
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.BAR, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-d", "bar", "-o", "out.png"))
        )
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.LINE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-o", "out.png", "-d", "line"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.PIE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-i", "in.txt", "-d", "pie", "-o", "out.png"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.LINE, 300f, null),
            parseOptions(listOf("-i", "in.txt", "-d", "line", "-s", "300"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.PIE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-i", "in.txt", "-o", "out.png", "-d", "pie"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.LINE, 300f, null),
            parseOptions(listOf("-i", "in.txt", "-s", "300", "-d", "line"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.PIE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-d", "pie", "-i", "in.txt", "-o", "out.png"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.PIE, DEFAULT_DIAGRAM_SCALE, File("out.png")),
            parseOptions(listOf("-d", "pie", "-o", "out.png", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), DEFAULT_SORT_OPTION, DiagramType.PIE, 300f, File("out.png")),
            parseOptions(listOf("-d", "pie", "-o", "out.png", "-s", "300", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), SortOption.SORT, DiagramType.PIE, 300f, File("out.png")),
            parseOptions(listOf("-d", "pie", "--sort", "-o", "out.png", "-s", "300", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), SortOption.REVERSE, DiagramType.PIE, 300f, File("out.png")),
            parseOptions(listOf("--rsort", "-d", "pie", "-o", "out.png", "-s", "300", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), SortOption.REVERSE, DiagramType.PIE, 300f, File("out.png")),
            parseOptions(listOf("-d", "pie", "-o", "out.png", "--reverse-sort", "-s", "300", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), SortOption.SORT, DiagramType.PIE, 300f, File("out.png")),
            parseOptions(listOf("-d", "pie", "-o", "out.png", "-s", "300", "--sort", "-i", "in.txt"))
        )
        assertEquals(
            Options(File("in.txt"), SortOption.NONE, DiagramType.PIE, 300f, File("out.png")),
            parseOptions(listOf("-d", "pie", "-o", "out.png", "-s", "300", "-i", "in.txt", "--nsort"))
        )
    }

    @Test
    fun `Invalid arguments`() {
        System.setSecurityManager(NoExitSecurityManager())
        assertFails { parseOptions(listOf("-i")) }
        assertFails { parseOptions(listOf("-d")) }
        assertFails { parseOptions(listOf("-o")) }
        assertFails { parseOptions(listOf("-k", "something")) }
        assertFails { parseOptions(listOf("-i", "-d", "line")) }
        assertFails { parseOptions(listOf("-i", "in.txt", "-o", "-t", "line")) }
        assertFails { parseOptions(listOf("", "", "", "")) }
        assertFails { parseOptions(listOf("--sort", "true")) }
        assertFails { parseOptions(listOf("-s", "--sort", "20")) }
        assertFails { parseOptions(listOf("-sort", "-i", "in.txt")) }
        assertFails { parseOptions(listOf("--bubbleSort", "-o", "out.txt")) }
        assertFails { parseOptions(listOf("-o", "--rsort", "out.txt")) }
    }

    @Test
    fun `Contradicting options`() {
        // Currently, only the last one is evaluated
        assertEquals(
            Options(null, DEFAULT_SORT_OPTION, DiagramType.LINE, DEFAULT_DIAGRAM_SCALE, null),
            parseOptions(listOf("-d", "pie", "-d", "line"))
        )
    }
}
