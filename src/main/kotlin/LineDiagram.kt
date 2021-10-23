import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Line diagram (graph).
 *
 * Inherits from [PlaneDiagram]
 */
class LineDiagram(data: Data, scale: Float, val fillArea: Boolean) : PlaneDiagram(data, scale, !fillArea) {

    companion object {
        const val GRAPH_COLOR_CODE = 0xFF4F86C6.toInt()
        const val AREA_COLOR_CODE = 0xFFAFC1D7.toInt()

        const val LINE_STROKE_WIDTH_COEFFICIENT = 0.005f
        const val POINT_STROKE_WIDTH_COEFFICIENT = 0.012f
        const val MIN_X_MARGIN_COEFFICIENT = 0.05f
        const val MIN_X_STEP_COEFFICIENT = 0.15f
        const val X_STEP_INDENT_COEFFICIENT = 0.035f
    }

    override val ruler: PlaneDiagramRuler
    override val horizontalLabels: PlaneDiagramHorizontalLabels

    override val xStep: Float
    override val xMargin: Float

    init {
        checkDataCorrectness()

        xStep = max(maxLabelWidth + scale * X_STEP_INDENT_COEFFICIENT, scale * MIN_X_STEP_COEFFICIENT)
        xMargin = if (!fillArea) {
            max(maxLabelWidth / 2, scale * MIN_X_MARGIN_COEFFICIENT)
        } else {
            0f
        }

        ruler = PlaneDiagramRuler(this, xStep * (data.size - 1) + 2 * xMargin, true)
        horizontalLabels = PlaneDiagramHorizontalLabels(this, xStep, true)
    }



    private fun checkDataCorrectness() {
        if (fillArea) {
            val negativeElement = data.find { it.value < 0 }
            if (negativeElement != null) {
                exitNegativeValues(DiagramType.AREA, negativeElement)
            }
        }
    }


    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float) {
        val linePaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = LINE_STROKE_WIDTH_COEFFICIENT * scale
        }
        val areaPaint = fillPaintByColorCode(AREA_COLOR_CODE)
        val pointPaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = POINT_STROKE_WIDTH_COEFFICIENT * scale
            strokeCap = PaintStrokeCap.ROUND
        }

        // Prepare geometric values
        val y1 = y0 + scale
        val yCoords = getYCoords(y0)
        val x1 = x0 + xMargin
        val points = data.mapIndexed { i, _ ->
            Pair(x1 + xStep * i, yCoords[i])
        }
        val pointsFlatten = points
            .flatMap { (x, y) -> listOf(x, y) }
            .toFloatArray()

        if (fillArea) {
            for (i in 0..data.size - 2) {
                val pointA = Point(x1 + xStep * i, yCoords[i])
                val pointB = Point(x1 + xStep * (i + 1), yCoords[i + 1])
                val pointPA = Point(pointA.x, y1)
                val pointPB = Point(pointB.x, y1)
                canvas.drawTriangleFan(arrayOf(pointA, pointPA, pointPB, pointB), null, areaPaint)
            }
        }

        horizontalLabels.draw(canvas, x1, y1)

        // Draw lines and points
        canvas.drawPolygon(pointsFlatten, linePaint)
        points.forEach { (x, y) ->
            canvas.drawPoint(x, y, pointPaint)
        }

        ruler.draw(canvas, x0, y0)
    }
}