import java.io.File

/**
 * This component contains possible program options.
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
// TODO("Add no window mode")
// TODO("Add custom padding")


enum class Option(val requiresArgument: Boolean) {
    INPUT_FILE(true),
    SORT_OPTION(false),
    DIAGRAM_TYPE(true),
    DIAGRAM_SCALE(true),
    OUTPUT_FILE(true),
}

fun getOptionByKeywordOrNull(keyword: String): Option? {
    return when (keyword) {
        "-i", "--input" -> Option.INPUT_FILE
        "--sort",
        "--rsort", "--reverse-sort",
        "--lsort", "--lex-sort",
        "--lrsort", "--rlsort",
        "--lex-reverse-sort", "--reverse-lex-sort",
        "--nsort", "--no-sort" -> Option.SORT_OPTION
        "-d", "--diagram" -> Option.DIAGRAM_TYPE
        "-s", "--scale" -> Option.DIAGRAM_SCALE
        "-o", "--output" -> Option.OUTPUT_FILE
        else -> null
    }
}


/**
 * Parses options from a list of program arguments.
 *
 * Returns null, if arguments are invalid.
 */
fun parseRawOptions(args: List<String>): Map<Option, String> {
    val options: MutableMap<Option, String> = mutableMapOf()
    var index = 0
    while (index < args.size) {
        val optionKeyword = args[index]
        val option = getOptionByKeywordOrNull(optionKeyword)
        if (option == null) {
            exitUnknownOption(optionKeyword)
            throw Exception("Couldn't exit in exitOptionRequiresParameter($optionKeyword)!")
        }
        if (option.requiresArgument) {
            if (index + 1 == args.size) {
                exitOptionRequiresParameter(option)
            }
            options[option] = args[index + 1]
            index += 2
        } else {
            options[option] = optionKeyword
            index++
        }
    }
    return options
}



fun parseFile(filename: String?): File? {
    return if (filename != null)
        File(filename)
    else
        null
}