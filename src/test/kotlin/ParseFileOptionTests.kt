import java.io.File
import kotlin.test.*

internal class ParseFileOptionTests {

    @Test
    fun `Parse File null`() {
        assertNull(parseFile(null))
    }

    @Test
    fun `Parse File`() {
        assertEquals(File("input.txt"), parseFile("input.txt"))
    }

    @Test
    fun `Parse File with spaces`() {
        assertEquals(File("file with spaces.txt"), parseFile("file with spaces.txt"))
    }

    @Test
    fun `Parse File long path`() {
        assertEquals(File("dir/subdir/subsubdir/data"), parseFile("dir/subdir/subsubdir/data"))
    }
}