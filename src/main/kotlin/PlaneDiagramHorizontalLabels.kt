import org.jetbrains.skija.*

/**
 * A drawable component of PlaneDiagram:
 * a set of labels on horizontal (X) axis.
 */
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
     * starting from x=[pivotX],
     * on y-level [pivotY].
     */
    override fun draw(canvas: Canvas, pivotX: Float, pivotY: Float) {
        diagram.labels.forEachIndexed { i, label ->
            val xMid = pivotX + xStep * i

            // Draw mark
            if (drawMarks) {
                canvas.drawLine(xMid, pivotY + MARK_LEAK, xMid, pivotY - MARK_LEAK, BLACK_FILL_PAINT)
            }

            // Draw label
            canvas.drawString(
                label,
                xMid - diagram.labelWidths[i] / 2,
                pivotY + diagram.maxLabelHeight + HORIZONTAL_LABELS_INDENT,
                diagram.font,
                BLACK_FILL_PAINT
            )
        }
    }

    /**
     * Get bounding [Rect] of horizontal labels.
     */
    override fun bounds() = Rect(
        -diagram.labelWidths.first() / 2,
        0f,
        xStep * (diagram.labels.size - 1) + diagram.labelWidths.last() / 2,
        diagram.maxLabelHeight + HORIZONTAL_LABELS_INDENT,
    )
}