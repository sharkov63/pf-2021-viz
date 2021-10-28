import org.jetbrains.skija.*
import kotlin.math.*


const val DEFAULT_DIAGRAM_PADDING = 50f
const val MIN_DIAGRAM_PADDING = 0f
const val MAX_DIAGRAM_PADDING = 1000f
val diagramPaddingOptionParser = FloatValuedOptionParser(
    "diagram padding",
    DEFAULT_DIAGRAM_PADDING,
    MIN_DIAGRAM_PADDING,
    MAX_DIAGRAM_PADDING
)

const val DEFAULT_DIAGRAM_SCALE = 400f
const val MIN_DIAGRAM_SCALE = 100f
const val MAX_DIAGRAM_SCALE = 2000f
val diagramScaleOptionParser = FloatValuedOptionParser(
    "diagram scale",
    DEFAULT_DIAGRAM_SCALE,
    MIN_DIAGRAM_SCALE,
    MAX_DIAGRAM_SCALE
)



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
abstract class Diagram(val data: Data, val scale: Float, val padding: Float) : Drawable() {
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
 * Build [Diagram] with [data] of given [type], with given [scale] and [padding].
 */
fun getDiagram(data: Data, scale: Float, padding: Float, rawColorCode: Int, type: DiagramType): Diagram {
    return when (type) {
        DiagramType.BAR -> BarDiagram(data, scale, padding, rawColorCode)
        DiagramType.PIE -> PieDiagram(data, scale, padding)
        DiagramType.LINE -> LineDiagram(data, scale, padding, rawColorCode, false)
        DiagramType.AREA -> LineDiagram(data, scale, padding, rawColorCode, true)
    }
}