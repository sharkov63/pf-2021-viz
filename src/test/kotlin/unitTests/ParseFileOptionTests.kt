package unitTests

import parseFile
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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