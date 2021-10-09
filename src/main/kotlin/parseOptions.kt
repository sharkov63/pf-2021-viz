import java.io.File

data class Options(val inputFile: File?, val diagramType: DiagramType, val outputFile: File?)


fun readOptionsFromArgs(args: List<String>): Options? {
    return Options(null, DiagramType.BAR, null)
}