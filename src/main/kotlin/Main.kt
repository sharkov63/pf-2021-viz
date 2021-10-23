fun main(args: Array<String>) {
    // Check for help message
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val options = parseOptions(args.toList()) ?: return exitInvalidArgs()

    val data = readData(options.inputFile)
    if (data.isEmpty()) {
        return exitEmptyData()
    }

    val diagram = when (options.diagramType) {
        DiagramType.BAR -> BarDiagram(data)
        DiagramType.PIE -> PieDiagram(data)
        DiagramType.LINE -> LineDiagram(data)
    }

    val size = 400f

    if (options.outputFile != null) {
        writeDiagramToPNGFile(options.outputFile, diagram, size)
    }

    createDiagramWindow("pf-2021-viz", diagram, size)
}