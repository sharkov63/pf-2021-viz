package unitTests

import DEFAULT_SORT_OPTION
import SortOption
import parseSortOption
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParseSortOptionTests {

    @Test
    fun `Parse sort option null`() {
        assertEquals(DEFAULT_SORT_OPTION, parseSortOption(null))
    }

    @Test
    fun `Parse sort option`() {
        assertEquals(SortOption.SORT, parseSortOption("--sort"))
        assertEquals(SortOption.REVERSE, parseSortOption("--rsort"))
        assertEquals(SortOption.REVERSE, parseSortOption("--reverse-sort"))
        assertEquals(SortOption.LEX, parseSortOption("--lsort"))
        assertEquals(SortOption.LEX, parseSortOption("--lex-sort"))
        assertEquals(SortOption.LEX_REVERSE, parseSortOption("--lrsort"))
        assertEquals(SortOption.LEX_REVERSE, parseSortOption("--rlsort"))
        assertEquals(SortOption.LEX_REVERSE, parseSortOption("--lex-reverse-sort"))
        assertEquals(SortOption.LEX_REVERSE, parseSortOption("--reverse-lex-sort"))
        assertEquals(SortOption.NONE, parseSortOption("--nsort"))
        assertEquals(SortOption.NONE, parseSortOption("--no-sort"))
    }

    @Test
    fun `Parse sort option unknown keyword`() {
        assertEquals(DEFAULT_SORT_OPTION, parseSortOption("abracadabra"))
    }
}