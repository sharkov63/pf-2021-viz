import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Bar diagram.
 *
 * Inherits from [PlaneDiagram].
 *
 * Data with negative values is not allowed.
 */
class BarDiagram(data: Data) : PlaneDiagram(data, false, false, false) {

    companion object {
        // Blue color for bars
        val BAR_PAINT = fillPaintByColorCode(0xFF4F86C6.toInt())

        const val MIN_BAR_WIDTH_COEFFICIENT = 0.12f
        const val BAR_PADDING = 2f
        const val X_GAP_COEFFICIENT = 0.05f
    }

    init { // Check for data correctness
        val negativeElement = data.find { it.value < 0 }
        if (negativeElement != null) {
            exitNegativeValues(DiagramType.BAR, negativeElement)
        }
    }


    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float) {
        val font = getFontBySize(size)
        val y1 = y0 + size

        // Prepare geometric values
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val barWidth = max(maxLabelWidth + BAR_PADDING, size * MIN_BAR_WIDTH_COEFFICIENT)
        val xGap = size * X_GAP_COEFFICIENT
        val xStep = barWidth + xGap
        val x2 = x0 + xStep * (data.size - 1) + barWidth

        // Draw bars
        for (i in data.indices) {
            val x = x0 + xStep * i
            val y = yCoords[i]
            canvas.drawRect(Rect(x, y, x + barWidth, y1), BAR_PAINT)
        }

        horizontalLabels.draw(canvas, x0 + barWidth / 2, size, xStep, y1)

        ruler.draw(canvas, x0, y0, size, x2)
    }

    override fun bounds(size: Float): Rect {
        val font = getFontBySize(size)

        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val barWidth = max(maxLabelWidth + BAR_PADDING, size * MIN_BAR_WIDTH_COEFFICIENT)
        val xGap = size * X_GAP_COEFFICIENT
        val xStep = barWidth + xGap

        val rulerBound = ruler.bounds(size)
        val horizontalLabelsBound = horizontalLabels.bounds(size, xStep, size)
        return unionRects(rulerBound, horizontalLabelsBound)
    }
}