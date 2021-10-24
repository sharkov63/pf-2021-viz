enum class SortOption {
    NONE,
    SORT,
    REVERSE,
    LEX,
    LEX_REVERSE,
}

val DEFAULT_SORT_OPTION = SortOption.NONE

fun parseSortOption(argument: String?): SortOption {
    return when (argument) {
        "--sort" -> SortOption.SORT
        "--rsort", "--reverse-sort" -> SortOption.REVERSE
        "--lsort", "--lex-sort" -> SortOption.LEX
        "--lrsort", "--rlsort", "--lex-reverse-sort", "--reverse-lex-sort" -> SortOption.LEX_REVERSE
        "--nsort", "--no-sort" -> SortOption.NONE
        null -> DEFAULT_SORT_OPTION
        else -> {
            println("Unknown sort option ($argument). Falling back to default ($DEFAULT_SORT_OPTION).")
            DEFAULT_SORT_OPTION
        }
    }
}
