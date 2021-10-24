import java.io.File

/**
 * This component contains possible program options.
 *
 * Current available options:
 *     -i, --input --- for input file;
 *     sort option:
 *         --sort --- sort in value order;
 *         --rsort, --reverse-sort --- sort in descending value order;
 *         --lsort, --lex-sort --- sort lexicographically by labels;
 *         --lrsort, --rlsort, --lex-reverse-sort, --reverse-lex-sort --- sort lexicographically by labels in reverse order;
 *         --nsort, --no-sort --- leave order as is;
 *     -d, --diagram --- for diagram type;
 *     -s, --scale --- for diagram scale;
 *     -p, --padding --- for diagram padding;
 *     -o, --output --- for output file;
 *     --no-window --- to not create window;
 *     --silent, --quiet --- to enable silent mode;
 */

// TODO("Add cropBottom option for line and bar diagrams")


enum class Option(val requiresArgument: Boolean) {
    INPUT_FILE(true),
    SORT_OPTION(false),
    DIAGRAM_TYPE(true),
    DIAGRAM_SCALE(true),
    DIAGRAM_PADDING(true),
    OUTPUT_FILE(true),
    NO_WINDOW_OPTION(false),
    SILENT_MODE(false),
}

/**
 * A complete mapping from keywords to options.
 */
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
        "-p", "--padding" -> Option.DIAGRAM_PADDING
        "-o", "--output" -> Option.OUTPUT_FILE
        "--no-window" -> Option.NO_WINDOW_OPTION
        "--silent", "--quiet" -> Option.SILENT_MODE
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



fun parseFile(argument: String?): File? {
    return if (argument != null)
        File(argument)
    else
        null
}

fun parseNoWindowOption(argument: String?): Boolean {
    return argument == "--no-window"
}

fun parseSilentModeOption(argument: String?): Boolean {
    return when (argument) {
        "--silent", "--quiet" -> true
        else -> false
    }
}