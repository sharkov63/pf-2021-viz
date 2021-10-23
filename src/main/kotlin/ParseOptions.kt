import java.io.File


/**
 * This component parses possible program options.
 *
 * Currently, there are four options:
 * "-i", "--input" for input file
 * "-d", "--diagram" for diagram type
 * "-s", "--scale" for scale
 * "-o", "--output" for output file
 *
 * and three possible flags for sort option:
 * "--no-sort" "--nsort"
 * "--sort"
 * "--reverse-sort" "--rsort"
 */
// TODO("Add cropBottom option for line and bar diagrams")


data class Options(
    val inputFile: File?,
    val sortOption: SortOption,
    val diagramType: DiagramType,
    val diagramScale: Float,
    val outputFile: File?,
)



enum class SortOption {
    NONE,
    SORT,
    REVERSE,
    LEX,
    LEX_REVERSE,
}
val DEFAULT_SORT_OPTION = SortOption.NONE

val sortOptionByKeyWord = mapOf(
    "--sort" to SortOption.SORT,
    "--rsort" to SortOption.REVERSE,
    "--reverse-sort" to SortOption.REVERSE,
    "--lsort" to SortOption.LEX,
    "--lex-sort" to SortOption.LEX,
    "--lrsort" to SortOption.LEX_REVERSE,
    "--rlsort" to SortOption.LEX_REVERSE,
    "--lex-reverse-sort" to SortOption.LEX_REVERSE,
    "--reverse-lex-sort" to SortOption.LEX_REVERSE,
    "--nsort" to SortOption.NONE,
    "--no-sort" to SortOption.NONE,
)


/**
 * Parses options from a list of program arguments.
 *
 * Returns null, if arguments are invalid.
 */
fun parseOptions(args: List<String>): Options {
    // Default options
    var inputFile: File? = null
    var sortOption = DEFAULT_SORT_OPTION
    var diagramType = DEFAULT_DIAGRAM_TYPE
    var diagramScale = DEFAULT_DIAGRAM_SCALE
    var outputFile: File? = null

    var index = 0
    while (index < args.size) {
        val option = args[index]
        if (sortOptionByKeyWord.containsKey(option)) {
            sortOption = sortOptionByKeyWord.getValue(option)
            index++
        } else {
            if (index + 1 == args.size) {
                exitOptionRequiresParameter(option)
            }
            val parameter = args[index + 1]
            when (option) {
                "-i", "--input" -> inputFile = File(parameter)
                "-d", "--diagram" -> diagramType = parseDiagramType(parameter)
                "-s", "--scale" -> diagramScale = parseDiagramScale(parameter)
                "-o", "--output" -> outputFile = File(parameter)
                else -> exitUnknownOption(option)
            }
            index += 2
        }
    }

    return Options(inputFile, sortOption, diagramType, diagramScale, outputFile)
}
