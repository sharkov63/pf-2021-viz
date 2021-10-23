import org.jetbrains.skija.*
import kotlin.math.*

// TODO("Area diagram")

const val MIN_DIAGRAM_SCALE = 100f
const val MAX_DIAGRAM_SCALE = 2000f
const val DEFAULT_DIAGRAM_SCALE = 400f

fun parseDiagramScale(text: String): Float {
    val scale = text.toFloatOrNull()
    return when {
        scale == null || scale.isNaN() -> {
            println("Invalid diagram scale ($text). Falling back to default ($DEFAULT_DIAGRAM_SCALE).")
            DEFAULT_DIAGRAM_SCALE
        }
        scale.isInfinite() || scale > MAX_DIAGRAM_SCALE -> {
            println("Diagram scale ($text) is too large. Falling back to maximum possible ($MAX_DIAGRAM_SCALE).")
            MAX_DIAGRAM_SCALE
        }
        scale < MIN_DIAGRAM_SCALE -> {
            println("Diagram scale ($text) is too small. Falling back to minimum possible ($MIN_DIAGRAM_SCALE).")
            MIN_DIAGRAM_SCALE
        }
        else -> scale
    }
}



/* Paints */

fun fillPaintByColorCode(colorCode: Int) = Paint().apply {
    color = colorCode
    mode = PaintMode.FILL
    strokeWidth = 1f
}

val BLACK_FILL_PAINT = fillPaintByColorCode(0xFF000000.toInt())
val BLACK_STROKE_PAINT = fillPaintByColorCode(0xFF000000.toInt()).apply {
    mode = PaintMode.STROKE
}
val LIGHT_GREY_STROKE_PAINT = fillPaintByColorCode(0xFFAAAAAA.toInt()).apply {
    mode = PaintMode.STROKE
}

/* Fonts */
val TYPEFACE = FontMgr.getDefault().matchFamilyStyle("Lucida Sans Unicode", FontStyle.NORMAL)
val FONT = Font(TYPEFACE, 20f)



fun unionRects(rect1: Rect, rect2: Rect) = Rect(
    min(rect1.left, rect2.left),
    min(rect1.top, rect2.top),
    max(rect1.right, rect2.right),
    max(rect1.bottom, rect2.bottom),
)




/**
 * Generic [Diagram] class.
 *
 * Contains diagram [data] and some statistics,
 * as well as abstract functions.
 */
abstract class Diagram(val data: Data, val scale: Float) : Drawable() {
    // TODO("Diagram title")
    // TODO("Add selectable options")

    val values = data.map { it.value }
    val labels = data.map { it.label }

    /* Stats */
    val minValue = values.minOf { it }
    val maxValue = values.maxOf { it }
    val sumValues = values
        .sumOf { it.toDouble() }
        .toFloat()
}

/**
 * Build [Diagram] with [data] of given [diagramType], with given [scale].
 */
fun getDiagram(data: Data, scale: Float, diagramType: DiagramType): Diagram {
    return when (diagramType) {
        DiagramType.BAR -> BarDiagram(data, scale)
        DiagramType.PIE -> PieDiagram(data, scale)
        DiagramType.LINE -> LineDiagram(data, scale)
    }
}