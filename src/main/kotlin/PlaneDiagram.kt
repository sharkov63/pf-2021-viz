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
abstract class PlaneDiagram(data: Data, val cropBottom: Boolean) : Diagram(data) {

    companion object {
        const val FONT_SIZE_COEFFICIENT = 0.03f
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }

    val ruler = PlaneDiagramRuler(this)



    fun getValueRange() = if (cropBottom || minValue < 0) {
        maxValue - minValue
    } else {
        maxValue
    }

    fun getBeginValue() = if (cropBottom || minValue < 0) {
        minValue
    } else {
        0f
    }


    /**
     * Get coordinates for value (y) axis on range [y0]..[y1]
     */
    fun getYCoords(y0: Float, y1: Float): List<Float> {
        return data.map { y1 - (it.value - ruler.begin) / (ruler.step * ruler.rangeRel) * (y1 - y0) }
    }


    /**
     * Draw labels on x-axis on [canvas]
     * starting from x=[x0],
     * with x-step [dx],
     * with font [font],
     * on y-level [y].
     *
     * One can choose to [drawMarks] or not (useful only for line diagram so far).
     */
    fun drawHorizontalLabels(
        canvas: Canvas,
        x0: Float,
        dx: Float,
        font: Font,
        y: Float,
        drawMarks: Boolean,
    ) {
        val labelRects = labels.map { label -> font.measureText(label) }
        val labelWidths = labelRects.map { rect -> rect.width }
        val labelHeights = labelRects.map { rect -> rect.height }
        val maxLabelHeight = labelHeights.maxOf { it }

        // Draw each of the labels
        for ((i, label) in labels.withIndex()) {
            val xMid = x0 + dx * i

            // Draw mark
            if (drawMarks) {
                canvas.drawLine(xMid, y + MARK_LEAK, xMid, y - MARK_LEAK, BLACK_FILL_PAINT)
            }

            // Draw label
            canvas.drawString(
                label,
                xMid - labelWidths[i] / 2,
                y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
                font,
                BLACK_FILL_PAINT
            )
        }
    }

    fun horizontalLabelsBound(dx: Float, y: Float, font: Font): Rect {
        val labelRects = labels.map { label -> font.measureText(label) }
        val labelWidths = labelRects.map { rect -> rect.width }
        val labelHeights = labelRects.map { rect -> rect.height }
        val maxLabelHeight = labelHeights.maxOf { it }

        return Rect(
            0f,
            0f,
            dx * labels.size + labelWidths.last(),
            y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
        )
    }
}