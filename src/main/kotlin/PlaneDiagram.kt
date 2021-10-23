import org.jetbrains.skija.*

/**
 * Diagram on a 2D plane:
 * y-axis is for values;
 * x-axis is for labels.
 *
 * [cropBottom] is a flag, which determines
 * whenever the value axis should start from zero (cropBottom = false),
 * or from the smallest value in data (cropBottom = true).
 *
 * Incorporates [BarDiagram] and [LineDiagram].
 */
abstract class PlaneDiagram(data: Data, scale: Float, val cropBottom: Boolean) : Diagram(data, scale) {

    companion object {
        const val FONT_SIZE_COEFFICIENT = 0.03f
    }

    val font: Font

    abstract val ruler: PlaneDiagramRuler
    abstract val horizontalLabels: PlaneDiagramHorizontalLabels

    abstract val xStep: Float
    abstract val xMargin: Float

    val labelRects: List<Rect>
    val labelWidths: List<Float>
    val labelHeights: List<Float>
    val maxLabelWidth: Float
    val maxLabelHeight: Float

    init {
        font = FONT.makeWithSize(scale * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true // bold labels
        }
        labelRects = labels.map { label -> font.measureText(label) }
        labelWidths = labelRects.map { rect -> rect.width }
        labelHeights = labelRects.map { rect -> rect.height }
        maxLabelWidth = labelWidths.maxOf { it }
        maxLabelHeight = labelHeights.maxOf { it }
    }


    /**
     * Get coordinates for value (y) axis, starting from [y0]
     */
    fun getYCoords(y0: Float): List<Float> {
        return data.map { y0 + scale - (it.value - ruler.begin) / ruler.range * scale }
    }


    /**
     * Get bounding [Rect] of diagram.
     */
    override fun bounds(): Rect {
        val rulerBounds = ruler.bounds()
        val horizontalLabelsBounds = horizontalLabels.bounds().offset(xMargin, scale)
        return unionRects(rulerBounds, horizontalLabelsBounds)
    }
}