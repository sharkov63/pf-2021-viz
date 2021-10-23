import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Bar diagram.
 *
 * Inherits from [PlaneDiagram].
 *
 * Data with negative values is not allowed.
 */
class BarDiagram(data: Data, scale: Float) : PlaneDiagram(data, scale, false) {

    companion object {
        // Blue color for bars
        val BAR_PAINT = fillPaintByColorCode(0xFF4F86C6.toInt())

        const val MIN_BAR_WIDTH_COEFFICIENT = 0.12f
        const val BAR_PADDING = 2f
        const val X_GAP_COEFFICIENT = 0.05f
    }

    override val ruler: PlaneDiagramRuler
    override val horizontalLabels: PlaneDiagramHorizontalLabels

    private val barWidth: Float
    private val xGap: Float

    override val xStep: Float

    init {
        checkDataCorrectness()

        ruler = PlaneDiagramRuler(this, false)
        horizontalLabels = PlaneDiagramHorizontalLabels(this, false)

        barWidth = max(horizontalLabels.maxLabelWidth + BAR_PADDING, scale * MIN_BAR_WIDTH_COEFFICIENT)
        xGap = scale * X_GAP_COEFFICIENT
        xStep = barWidth + xGap
    }



    private fun checkDataCorrectness() {
        val negativeElement = data.find { it.value < 0 }
        if (negativeElement != null) {
            exitNegativeValues(DiagramType.BAR, negativeElement)
        }
    }


    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float) {
        val y1 = y0 + scale
        val yCoords = getYCoords(y0)
        val x2 = x0 + xStep * (data.size - 1) + barWidth

        // Draw bars
        for (i in data.indices) {
            val x = x0 + xStep * i
            val y = yCoords[i]
            canvas.drawRect(Rect(x, y, x + barWidth, y1), BAR_PAINT)
        }

        horizontalLabels.draw(canvas, x0 + barWidth / 2, xStep, y1)
        ruler.draw(canvas, x0, y0, scale, x2)
    }
}