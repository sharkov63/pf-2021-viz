import kotlin.test.*

internal class ReadDataTests {

    @Test
    fun `parseDataElementOrNull Correct`() {
        assertEquals(DataElement("label", 2f), parseDataElementOrNull("label 2.0"))
    }

    @Test
    fun `parseDataElementOrNull Label with spaces`() {
        assertEquals(DataElement("label with spaces", 0f), parseDataElementOrNull("label with spaces 0"))
    }

    @Test
    fun `parseDataElementOrNull Many spaces`() {
        assertEquals(DataElement("many    spaces", 998244353f), parseDataElementOrNull("many    spaces        998244353"))
    }

    @Test
    fun `parseDataElementOrNull Leading spaces`() {
        assertEquals(DataElement("   label", 12.3f), parseDataElementOrNull("   label 12.3"))
    }

    @Test
    fun `parseDataElementOrNull Trailing spaces`() {
        assertEquals(DataElement("label", 16.9f), parseDataElementOrNull("label 16.9      "))
    }

    @Test
    fun `parseDataElementOrNull Label is a number`() {
        assertEquals(DataElement("10", 10f), parseDataElementOrNull("10    10 "))
    }

    @Test
    fun `parseDataElementOrNull Label is a negative number`() {
        assertEquals(DataElement("-10", 11f), parseDataElementOrNull("-10    11 "))
    }

    @Test
    fun `parseDataElementOrNull Empty line`() {
        assertEquals(null, parseDataElementOrNull(""))
    }

    @Test
    fun `parseDataElementOrNull Line with only blanks`() {
        assertEquals(null, parseDataElementOrNull("      \n \n    \n "))
    }

    @Test
    fun `parseDataElementOrNull One token`() {
        assertEquals(null, parseDataElementOrNull("oneToken"))
    }

    @Test
    fun `parseDataElementOrNull Value is negative`() {
        assertEquals(null, parseDataElementOrNull("label -1.0"))
    }

    @Test
    fun `parseDataElementOrNull Value is a string`() {
        assertEquals(null, parseDataElementOrNull("label NotANumber"))
    }

    @Test
    fun `parseDataElementOrNull Value is NaN`() {
        assertEquals(null, parseDataElementOrNull("label NaN"))
    }

    @Test
    fun `parseDataElementOrNull Value is Infinity`() {
        assertEquals(null, parseDataElementOrNull("label Infinity"))
    }

    @Test
    fun `parseDataFromLines All in`() {
        assertEquals(
            DataWithSkipStats(
                listOf(
                    DataElement("foo", 1f),
                    DataElement("bar", 2f),
                    DataElement("baz", 3f),
                ),
                0
            ),
            parseDataFromLines(listOf(
                "foo 1.0",
                "bar   2",
                "baz   3.000000    ",
            ))
        )
    }

    @Test
    fun `parseDataFromLines Skipped one`() {
        assertEquals(
            DataWithSkipStats(
                listOf(
                    DataElement("Label with spaces", 5f),
                    DataElement("     Leading spaces", 12.578f),
                ),
                1
            ),
            parseDataFromLines(listOf(
                "Label with spaces 5",
                "Some invalid line",
                "     Leading spaces      12.578000000",
            ))
        )
    }
}
