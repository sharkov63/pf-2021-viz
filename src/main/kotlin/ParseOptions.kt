import java.io.File

data class Options(val inputFile: File?, val diagramType: DiagramType, val outputFile: File?)

// TODO("Add cropBottom option for line and bar diagrams")

fun parseOptions(args: List<String>): Options? {
    if (args.size % 2 == 1) return null

    var inputFile: File? = null
    var diagramType = DEFAULT_DIAGRAM_TYPE
    var outputFile: File? = null

    for (i in args.indices step 2) {
        val option = args[i]
        val param = args[i + 1]
        when (option) {
            "-i", "--input" -> inputFile = File(param)
            "-d", "--diagram" -> diagramType = diagramTypeByDescription.getOrDefault(param.lowercase(), DEFAULT_DIAGRAM_TYPE)
            "-o", "--output" -> outputFile = File(param)
            else -> return null
        }
    }

    return Options(inputFile, diagramType, outputFile)
}