import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Line diagram (graph).
 *
 * Inherits from [PlaneDiagram]
 */
class LineDiagram(data: Data, cropBottom: Boolean = true) : PlaneDiagram(data, cropBottom) {

    companion object {
        const val GRAPH_COLOR_CODE = 0xFF4F86C6.toInt()

        const val LINE_STROKE_WIDTH_COEFFICIENT = 0.005f
        const val POINT_STROKE_WIDTH_COEFFICIENT = 0.012f
        const val MIN_X_MARGIN_COEFFICIENT = 0.05f
        const val MIN_X_STEP_COEFFICIENT = 0.15f
        const val X_STEP_INDENT_COEFFICIENT = 0.035f
    }

    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float) {
        // Bold labels
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true
        }
        val linePaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = LINE_STROKE_WIDTH_COEFFICIENT * size
        }
        val pointPaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = POINT_STROKE_WIDTH_COEFFICIENT * size
        }

        // Prepare geometric values
        val y1 = y0 + size
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val xMargin = max(maxLabelWidth / 2, size * MIN_X_MARGIN_COEFFICIENT)
        val x1 = x0 + xMargin
        val xStep = max(maxLabelWidth + size * X_STEP_INDENT_COEFFICIENT, size * MIN_X_STEP_COEFFICIENT)
        val points = data.mapIndexed { i, _ ->
            Pair(x1 + xStep * i, yCoords[i])
        }
        val pointsFlatten = points
            .flatMap { (x, y) -> listOf(x, y) }
            .toFloatArray()

        drawHorizontalLabels(
            canvas,
            x1,
            xStep,
            font,
            y1,
            true,
        )

        // Draw lines and points
        canvas.drawPolygon(pointsFlatten, linePaint)
        points.forEach { (x, y) ->
            canvas.drawPoint(x, y, pointPaint)
        }

        drawRuler(
            canvas,
            y0,
            y1,
            x0,
            x1 + xStep * (data.size - 1) + xMargin,
            font,
            true,
        )
    }

    override fun bounds(size: Float): Rect {
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true
        }

        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val xStep = max(maxLabelWidth + size * X_STEP_INDENT_COEFFICIENT, size * MIN_X_STEP_COEFFICIENT)

        val rulerBound = rulerBound(size, font)
        val horizontalLabelsBound = horizontalLabelsBound(xStep, size, font)
        return unionRects(rulerBound, horizontalLabelsBound)
    }
}