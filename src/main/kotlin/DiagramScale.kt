const val MIN_DIAGRAM_SCALE = 100f
const val MAX_DIAGRAM_SCALE = 2000f
const val DEFAULT_DIAGRAM_SCALE = 400f

fun parseDiagramScale(argument: String?): Float {
    if (argument == null) {
        return DEFAULT_DIAGRAM_SCALE
    }
    val scale = argument.toFloatOrNull()
    return when {
        scale == null || scale.isNaN() -> {
            println("Invalid diagram scale ($argument). Falling back to default ($DEFAULT_DIAGRAM_SCALE).")
            DEFAULT_DIAGRAM_SCALE
        }
        scale.isInfinite() || scale > MAX_DIAGRAM_SCALE -> {
            println("Diagram scale ($argument) is too large. Falling back to maximum possible ($MAX_DIAGRAM_SCALE).")
            MAX_DIAGRAM_SCALE
        }
        scale < MIN_DIAGRAM_SCALE -> {
            println("Diagram scale ($argument) is too small. Falling back to minimum possible ($MIN_DIAGRAM_SCALE).")
            MIN_DIAGRAM_SCALE
        }
        else -> scale
    }
}
