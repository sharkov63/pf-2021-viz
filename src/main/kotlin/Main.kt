fun main(args: Array<String>) {
    // Check for help message
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val (inputFile, diagramType, diagramScale, outputFile) = parseOptions(args.toList()) ?: return exitInvalidArgs()

    val data = readData(inputFile)
    if (data.isEmpty()) {
        return exitEmptyData()
    }

    val diagram = getDiagram(data, diagramScale, diagramType)

    if (outputFile != null) {
        writeDiagramToPNGFile(outputFile, diagram)
    }

    createDiagramWindow("pf-2021-viz", diagram)
}