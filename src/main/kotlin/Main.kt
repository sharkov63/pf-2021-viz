import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun main(args: Array<String>) {
    // Check for help message
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val rawOptions = parseRawOptions(args.toList())

    val silentMode = parseSilentModeOption(rawOptions[Option.SILENT_MODE])
    if (silentMode) {
        val dummyStream = PrintStream(ByteArrayOutputStream())
        System.setOut(dummyStream)
    }

    // Retrieve actual options
    val inputFile = parseFile(rawOptions[Option.INPUT_FILE])
    val sortOption = parseSortOption(rawOptions[Option.SORT_OPTION])
    val diagramType = parseDiagramType(rawOptions[Option.DIAGRAM_TYPE])
    val diagramScale = diagramScaleOptionParser.parse(rawOptions[Option.DIAGRAM_SCALE])
    val diagramPadding = diagramPaddingOptionParser.parse(rawOptions[Option.DIAGRAM_PADDING])
    val outputFile = parseFile(rawOptions[Option.OUTPUT_FILE])
    val noWindow = parseNoWindowOption(rawOptions[Option.NO_WINDOW_OPTION])

    val data = readData(inputFile)
    if (data.isEmpty()) {
        return exitEmptyData()
    }

    val orderedData = when (sortOption) {
        SortOption.NONE -> data
        SortOption.SORT -> data.sortedBy { it.value }
        SortOption.REVERSE -> data.sortedByDescending { it.value }
        SortOption.LEX -> data.sortedBy { it.label }
        SortOption.LEX_REVERSE -> data.sortedByDescending { it.label }
    }

    val diagram = getDiagram(orderedData, diagramScale, diagramPadding, diagramType)

    if (outputFile != null) {
        writeDiagramToPNGFile(outputFile, diagram)
    }

    if (!noWindow) {
        createDiagramWindow("pf-2021-viz", diagram)
    } else {
        println("Window wasn't created: --no-window option is active.")
    }
}