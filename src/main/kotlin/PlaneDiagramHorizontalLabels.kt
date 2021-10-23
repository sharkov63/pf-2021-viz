import org.jetbrains.skija.*

class PlaneDiagramHorizontalLabels(val diagram: PlaneDiagram, private val drawMarks: Boolean) {

    companion object {
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }

    /**
     * Draw labels on x-axis on [canvas]
     * starting from x=[x0],
     * with scale=[scale],
     * with x-step [dx],
     * on y-level [y].
     */
    fun draw(canvas: Canvas, x0: Float, scale: Float, dx: Float, y: Float) {
        val labelRects = diagram.labels.map { label -> diagram.font.measureText(label) }
        val labelWidths = labelRects.map { rect -> rect.width }
        val labelHeights = labelRects.map { rect -> rect.height }
        val maxLabelHeight = labelHeights.maxOf { it }

        // Draw each of the labels
        for ((i, label) in diagram.labels.withIndex()) {
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
                diagram.font,
                BLACK_FILL_PAINT
            )
        }
    }

    /**
     * Get bounding [Rect] of horizontal labels,
     * if it's drawn with scale=[scale], x-step=[dx] on level [y].
     */
    fun bounds(scale: Float, dx: Float, y: Float): Rect {
        val labelRects = diagram.labels.map { label -> diagram.font.measureText(label) }
        val labelWidths = labelRects.map { rect -> rect.width }
        val labelHeights = labelRects.map { rect -> rect.height }
        val maxLabelHeight = labelHeights.maxOf { it }

        return Rect(
            0f,
            0f,
            dx * diagram.labels.size + labelWidths.last(),
            y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
        )
    }
}