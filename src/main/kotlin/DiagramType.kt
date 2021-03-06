/**
 * Supported diagram types.
 */
enum class DiagramType {
    BAR,
    PIE,
    LINE,
    AREA,
}

val DEFAULT_DIAGRAM_TYPE = DiagramType.BAR

/**
 * Possible keywords to specify diagram type.
 */
val diagramTypeByDescription = mapOf(
    "default" to DEFAULT_DIAGRAM_TYPE,
    "bar" to DiagramType.BAR,
    "histogram" to DiagramType.BAR,
    "column" to DiagramType.BAR,
    "pie" to DiagramType.PIE,
    "circle" to DiagramType.PIE,
    "round" to DiagramType.PIE,
    "line" to DiagramType.LINE,
    "graph" to DiagramType.LINE,
    "plot" to DiagramType.LINE,
    "curve" to DiagramType.LINE,
    "area" to DiagramType.AREA,
    "fill" to DiagramType.AREA,
)

fun parseDiagramType(argument: String?): DiagramType {
    if (argument == null) return DEFAULT_DIAGRAM_TYPE
    return diagramTypeByDescription.getOrElse(argument.lowercase()) {
        println("Unknown diagram type ($argument). Falling back to default ($DEFAULT_DIAGRAM_TYPE).")
        DEFAULT_DIAGRAM_TYPE
    }
}
