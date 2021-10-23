import org.jetbrains.skija.*
import kotlin.math.*

// TODO("Area diagram")

/**
 * This component contains diagram classes,
 * and their drawing algorithms.
 */

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
abstract class Diagram(val data: Data) {
    // TODO("Diagram title")
    // TODO("Add selectable options")

    val PNG_PADDING = 5

    val values = data.map { it.value }
    val labels = data.map { it.label }

    /* Stats */
    val minValue = values.minOf { it }
    val maxValue = values.maxOf { it }
    val sumValues = values
        .sumOf { it.toDouble() }
        .toFloat()


    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     *
     * This function should only be called
     * on specific subclasses of [Diagram] class.
     */
    abstract fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float)

    /**
     * Predicts bounding rectangle,
     * if the diagram will be drawn at x0 = y0 = 0 with size [size]
     */
    abstract fun bounds(size: Float): Rect


    /**
     * Returns the PNG data of the diagram.
     */
    fun getPNGData(size: Float): ByteArray? {
        val diagramBounds = bounds(size)

        val bitmap = Bitmap()
        bitmap.imageInfo = ImageInfo(
            diagramBounds.width.toInt() + 2 * PNG_PADDING,
            diagramBounds.height.toInt() + 2 * PNG_PADDING,
            ColorType.BGRA_8888,
            ColorAlphaType.PREMUL
        )
        bitmap.allocPixels()
        val canvas = Canvas(bitmap)
        draw(canvas, -diagramBounds.left + PNG_PADDING, -diagramBounds.top + PNG_PADDING, size)
        canvas.readPixels(bitmap, 0, 0)

        val image = Image.makeFromBitmap(bitmap)
        val pngData = image.encodeToData(EncodedImageFormat.PNG) ?: return null
        return pngData.bytes
    }
}

/**
 * Build [Diagram] with [data] of given [diagramType].
 */
fun getDiagram(data: Data, diagramType: DiagramType): Diagram {
    return when (diagramType) {
        DiagramType.BAR -> BarDiagram(data)
        DiagramType.PIE -> PieDiagram(data)
        DiagramType.LINE -> LineDiagram(data)
    }
}