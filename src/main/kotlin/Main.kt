fun main(args: Array<String>) {
    // Check for help message
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val rawOptions = parseRawOptions(args.toList())

    val inputFile = parseFile(rawOptions[Option.INPUT_FILE])
    val sortOption = parseSortOption(rawOptions[Option.SORT_OPTION])
    val diagramType = parseDiagramType(rawOptions[Option.DIAGRAM_TYPE])
    val diagramScale = parseDiagramScale(rawOptions[Option.DIAGRAM_SCALE])
    val outputFile = parseFile(rawOptions[Option.OUTPUT_FILE])

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

    val diagram = getDiagram(orderedData, diagramScale, diagramType)

    if (outputFile != null) {
        writeDiagramToPNGFile(outputFile, diagram)
    }

    createDiagramWindow("pf-2021-viz", diagram)
}