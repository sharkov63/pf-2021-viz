// TODO("Documentation")
// TODO("PNG output")

fun main(args: Array<String>) {
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val options = parseOptions(args.toList()) ?: return exitInvalidArgs()

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

    createDiagramWindow("pf-2021-viz", diagram)

    if (options.outputFile != null) {
        writeScreenshotToFile(options.outputFile)
    }
}