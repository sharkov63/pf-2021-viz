import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Line diagram (graph).
 *
 * Inherits from [PlaneDiagram]
 */
class LineDiagram(data: Data, scale: Float) : PlaneDiagram(data, scale, true, true, true) {

    companion object {
        const val GRAPH_COLOR_CODE = 0xFF4F86C6.toInt()

        const val LINE_STROKE_WIDTH_COEFFICIENT = 0.005f
        const val POINT_STROKE_WIDTH_COEFFICIENT = 0.012f
        const val MIN_X_MARGIN_COEFFICIENT = 0.05f
        const val MIN_X_STEP_COEFFICIENT = 0.15f
        const val X_STEP_INDENT_COEFFICIENT = 0.035f
    }

    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float) {
        val font = getFontBySize(scale)
        val linePaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = LINE_STROKE_WIDTH_COEFFICIENT * scale
        }
        val pointPaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = POINT_STROKE_WIDTH_COEFFICIENT * scale
        }

        // Prepare geometric values
        val y1 = y0 + scale
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val xMargin = max(maxLabelWidth / 2, scale * MIN_X_MARGIN_COEFFICIENT)
        val x1 = x0 + xMargin
        val xStep = max(maxLabelWidth + scale * X_STEP_INDENT_COEFFICIENT, scale * MIN_X_STEP_COEFFICIENT)
        val points = data.mapIndexed { i, _ ->
            Pair(x1 + xStep * i, yCoords[i])
        }
        val pointsFlatten = points
            .flatMap { (x, y) -> listOf(x, y) }
            .toFloatArray()

        horizontalLabels.draw(canvas, x1, scale, xStep, y1)

        // Draw lines and points
        canvas.drawPolygon(pointsFlatten, linePaint)
        points.forEach { (x, y) ->
            canvas.drawPoint(x, y, pointPaint)
        }

        ruler.draw(canvas, x0, y0, scale, x1 + xStep * (data.size - 1) + xMargin)
    }

    /**
     * Get bounding [Rect] of diagram.
     */
    override fun bounds(): Rect {
        val font = getFontBySize(scale)

        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val xStep = max(maxLabelWidth + scale * X_STEP_INDENT_COEFFICIENT, scale * MIN_X_STEP_COEFFICIENT)

        val rulerBound = ruler.bounds(scale)
        val horizontalLabelsBound = horizontalLabels.bounds(scale, xStep, scale)
        return unionRects(rulerBound, horizontalLabelsBound)
    }
}