import java.io.File


/**
 * This component parses possible program options.
 *
 * Currently, there are three options:
 * -i for input file
 * -d for diagram type
 * -o for output file
 */

// TODO("Add cropBottom option for line and bar diagrams")
// TODO("Add --sort option")

data class Options(val inputFile: File?, val diagramType: DiagramType, val diagramScale: Float, val outputFile: File?)


/**
 * Parses options from a list of program arguments.
 *
 * Returns null, if arguments are invalid.
 */
fun parseOptions(args: List<String>): Options? {
    if (args.size % 2 == 1) {
        // Wrong parity
        return null
    }

    // Default options
    var inputFile: File? = null
    var diagramType = DEFAULT_DIAGRAM_TYPE
    var diagramScale = DEFAULT_DIAGRAM_SCALE
    var outputFile: File? = null

    // Go through each pair of tokens
    for (i in args.indices step 2) {
        val option = args[i]
        val param = args[i + 1]
        when (option) {
            "-i", "--input" -> inputFile = File(param)
            "-d", "--diagram" -> diagramType = parseDiagramType(param)
            "-s", "--scale" -> diagramScale = parseDiagramScale(param)
            "-o", "--output" -> outputFile = File(param)
            else -> return null // invalid option
        }
    }

    return Options(inputFile, diagramType, diagramScale, outputFile)
}
