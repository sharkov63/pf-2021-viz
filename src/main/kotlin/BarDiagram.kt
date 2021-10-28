import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Bar diagram.
 *
 * Inherits from [PlaneDiagram].
 *
 * Data with negative values is not allowed.
 */
class BarDiagram(data: Data, scale: Float, padding: Float, rawColorCode: Int) : PlaneDiagram(data, scale, padding, false) {

    companion object {
        const val MIN_BAR_WIDTH_COEFFICIENT = 0.12f
        const val BAR_PADDING = 2f
        const val X_GAP_COEFFICIENT = 0.05f
    }

    private val barPaint = fillPaintByColorCode(rawColorCode or 0xFF000000.toInt())

    override val ruler: PlaneDiagramRuler
    override val horizontalLabels: PlaneDiagramHorizontalLabels

    private val barWidth: Float
    private val xGap: Float

    override val xStep: Float
    override val xMargin: Float

    init {
        checkDataCorrectness()

        barWidth = max(maxLabelWidth + BAR_PADDING, scale * MIN_BAR_WIDTH_COEFFICIENT)
        xGap = scale * X_GAP_COEFFICIENT
        xStep = barWidth + xGap
        xMargin = barWidth / 2

        ruler = PlaneDiagramRuler(this, xStep * (data.size - 1) + barWidth, false)
        horizontalLabels = PlaneDiagramHorizontalLabels(this, xStep, false)
    }



    private fun checkDataCorrectness() {
        val negativeElement = data.find { it.value < 0 }
        if (negativeElement != null) {
            exitNegativeValues(DiagramType.BAR, negativeElement)
        }
    }


    /**
     * Draws diagram on [canvas] at pivot point [pivotX], [pivotY].
     */
    override fun draw(canvas: Canvas, pivotX: Float, pivotY: Float) {
        val x0 = pivotX + padding
        val y0 = pivotY + padding
        val y1 = y0 + scale
        val yCoords = getYCoords(y0)

        // Draw bars
        for (i in data.indices) {
            val x = x0 + xStep * i
            val y = yCoords[i]
            canvas.drawRect(Rect(x, y, x + barWidth, y1), barPaint)
        }

        horizontalLabels.draw(canvas, x0 + barWidth / 2, y1)
        ruler.draw(canvas, x0, y0)
    }
}