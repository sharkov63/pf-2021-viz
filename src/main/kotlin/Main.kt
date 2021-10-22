fun main(args: Array<String>) {
    // Check for help message
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val options = parseOptions(args.toList()) ?: return exitInvalidArgs()

    // Read data
    val (data, skippedRecords) = readDataWithSkipStats(options.inputFile)
    println("Successfully read ${data.size + skippedRecords} records.")
    if (skippedRecords > 0) {
        println("$skippedRecords of those records are invalid and thus, omitted.")
    }
    if (data.isEmpty()) {
        return exitEmptyData()
    }

    val diagram = when (options.diagramType) {
        DiagramType.BAR -> BarDiagram(data)
        DiagramType.PIE -> PieDiagram(data)
        DiagramType.LINE -> LineDiagram(data)
    }

    val size = 400f

    // PNG output
    if (options.outputFile != null) {
        writeDiagramToFile(options.outputFile, diagram, size)
    }

    createDiagramWindow("pf-2021-viz", diagram, size)
}