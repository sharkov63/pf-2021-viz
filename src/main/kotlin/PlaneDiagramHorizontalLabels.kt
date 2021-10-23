import org.jetbrains.skija.*

class PlaneDiagramHorizontalLabels(
    val diagram: PlaneDiagram,
    private val xStep: Float,
    private val drawMarks: Boolean,
) : Drawable() {

    companion object {
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }


    /**
     * Draw labels on x-axis on [canvas]
     * starting from x=[x0],
     * on y-level [y0].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float) {
        diagram.labels.forEachIndexed { i, label ->
            val xMid = x0 + xStep * i

            // Draw mark
            if (drawMarks) {
                canvas.drawLine(xMid, y0 + MARK_LEAK, xMid, y0 - MARK_LEAK, BLACK_FILL_PAINT)
            }

            // Draw label
            canvas.drawString(
                label,
                xMid - diagram.labelWidths[i] / 2,
                y0 + diagram.maxLabelHeight + HORIZONTAL_LABELS_INDENT,
                diagram.font,
                BLACK_FILL_PAINT
            )
        }
    }

    /**
     * Get bounding [Rect] of horizontal labels.
     */
    override fun bounds() = Rect(
        0f,
        0f,
        xStep * diagram.labels.size + diagram.labelWidths.last(),
        diagram.maxLabelHeight + HORIZONTAL_LABELS_INDENT,
    )
}