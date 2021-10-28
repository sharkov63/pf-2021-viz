package unitTests

import Option
import parseRawOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class ParseRawOptionsTests {

    @Test
    fun `Empty args`() {
        assertEquals(
            mapOf(),
            parseRawOptions(listOf())
        )
    }

    @Test
    fun `Input file -i`() {
        assertEquals(
            mapOf(Option.INPUT_FILE to "file.txt"),
            parseRawOptions(listOf("-i", "file.txt"))
        )
    }

    @Test
    fun `Input file --input`() {
        assertEquals(
            mapOf(Option.INPUT_FILE to "file.txt"),
            parseRawOptions(listOf("--input", "file.txt"))
        )
    }

    @Test
    fun `Sort option --no-sort`() {
        assertEquals(
            mapOf(Option.SORT_OPTION to "--no-sort"),
            parseRawOptions(listOf("--no-sort"))
        )
    }

    @Test
    fun `Sort option --nsort`() {
        assertEquals(
            mapOf(Option.SORT_OPTION to "--nsort"),
            parseRawOptions(listOf("--nsort"))
        )
    }

    @Test
    fun `Sort option --sort`() {
        assertEquals(
            mapOf(Option.SORT_OPTION to "--sort"),
            parseRawOptions(listOf("--sort"))
        )
    }

    @Test
    fun `Sort option --reverse-sort`() {
        assertEquals(
            mapOf(Option.SORT_OPTION to "--reverse-sort"),
            parseRawOptions(listOf("--reverse-sort"))
        )
    }

    @Test
    fun `Sort option --rsort`() {
        assertEquals(
            mapOf(Option.SORT_OPTION to "--rsort"),
            parseRawOptions(listOf("--rsort"))
        )
    }

    @Test
    fun `Diagram type -d`() {
        assertEquals(
            mapOf(Option.DIAGRAM_TYPE to "bar"),
            parseRawOptions(listOf("-d", "bar"))
        )
    }

    @Test
    fun `Diagram type --diagram`() {
        assertEquals(
            mapOf(Option.DIAGRAM_TYPE to "line"),
            parseRawOptions(listOf("--diagram", "line"))
        )
    }

    @Test
    fun `Diagram scale -s`() {
        assertEquals(
            mapOf(Option.DIAGRAM_SCALE to "100"),
            parseRawOptions(listOf("-s", "100"))
        )
    }

    @Test
    fun `Diagram scale --scale`() {
        assertEquals(
            mapOf(Option.DIAGRAM_SCALE to "200"),
            parseRawOptions(listOf("--scale", "200"))
        )
    }

    @Test
    fun `Output file -o`() {
        assertEquals(
            mapOf(Option.OUTPUT_FILE to "diagram.png"),
            parseRawOptions(listOf("-o", "diagram.png"))
        )
    }

    @Test
    fun `Output file --output`() {
        assertEquals(
            mapOf(Option.OUTPUT_FILE to "diagram.png"),
            parseRawOptions(listOf("--output", "diagram.png"))
        )
    }

    @Test
    fun `Combined arguments`() {
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "pie"),
            parseRawOptions(listOf("-i", "in.txt", "-d", "pie"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "line"),
            parseRawOptions(listOf("-d", "line", "-i", "in.txt"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_SCALE to "200"),
            parseRawOptions(listOf("-i", "in.txt", "-s", "200"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-i", "in.txt", "-o", "out.png"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-o", "out.png", "-i", "in.txt"))
        )
        assertEquals(
            mapOf(Option.DIAGRAM_TYPE to "bar", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-d", "bar", "-o", "out.png"))
        )
        assertEquals(
            mapOf(Option.DIAGRAM_TYPE to "line", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-o", "out.png", "-d", "line"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "pie", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-i", "in.txt", "-d", "pie", "-o", "out.png"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "line", Option.DIAGRAM_SCALE to "300"),
            parseRawOptions(listOf("-i", "in.txt", "-d", "line", "-s", "300"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "pie", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-i", "in.txt", "-o", "out.png", "-d", "pie"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "line", Option.DIAGRAM_SCALE to "300"),
            parseRawOptions(listOf("-i", "in.txt", "-s", "300", "-d", "line"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "pie", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-d", "pie", "-i", "in.txt", "-o", "out.png"))
        )
        assertEquals(
            mapOf(Option.INPUT_FILE to "in.txt", Option.DIAGRAM_TYPE to "pie", Option.OUTPUT_FILE to "out.png"),
            parseRawOptions(listOf("-d", "pie", "-o", "out.png", "-i", "in.txt"))
        )
        assertEquals(
            mapOf(
                Option.INPUT_FILE to "in.txt",
                Option.DIAGRAM_TYPE to "pie",
                Option.DIAGRAM_SCALE to "300",
                Option.OUTPUT_FILE to "out.png"
            ),
            parseRawOptions(listOf("-d", "pie", "-o", "out.png", "-s", "300", "-i", "in.txt"))
        )
    }

    @Test
    fun `Invalid arguments`() {
        System.setSecurityManager(NoExitSecurityManager())
        assertFails { parseRawOptions(listOf("-i")) }
        assertFails { parseRawOptions(listOf("-d")) }
        assertFails { parseRawOptions(listOf("-o")) }
        assertFails { parseRawOptions(listOf("-k", "something")) }
        assertFails { parseRawOptions(listOf("-i", "-d", "line")) }
        assertFails { parseRawOptions(listOf("-i", "in.txt", "-o", "-t", "line")) }
        assertFails { parseRawOptions(listOf("", "", "", "")) }
        assertFails { parseRawOptions(listOf("--sort", "true")) }
        assertFails { parseRawOptions(listOf("-s", "--sort", "20")) }
        assertFails { parseRawOptions(listOf("-sort", "-i", "in.txt")) }
        assertFails { parseRawOptions(listOf("--bubbleSort", "-o", "out.txt")) }
        assertFails { parseRawOptions(listOf("-o", "--rsort", "out.txt")) }
    }

    @Test
    fun `Contradicting options`() {
        // Currently, only the last one is evaluated
        assertEquals(
            mapOf(Option.DIAGRAM_TYPE to "line"),
            parseRawOptions(listOf("-d", "pie", "-d", "line"))
        )
    }
}