fun main(args: Array<String>) {
    // Check for help message
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val (inputFile, sortOption, diagramType, diagramScale, outputFile) = parseOptions(args.toList())

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