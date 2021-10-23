/**
 * Supported diagram types.
 */
enum class DiagramType {
    BAR,
    PIE,
    LINE,
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
)