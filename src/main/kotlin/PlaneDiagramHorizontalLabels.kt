import org.jetbrains.skija.*

class PlaneDiagramHorizontalLabels(diagram: PlaneDiagram, private val xStep: Float, private val drawMarks: Boolean) {

    companion object {
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }

    private val font: Font
    private val labels: List<String>
    private val labelRects: List<Rect>
    private val labelWidths: List<Float>
    private val maxLabelWidth: Float
    private val maxLabelHeight: Float

    init {
        font = diagram.font
        labels = diagram.labels
        labelRects = diagram.labelRects
        labelWidths = diagram.labelWidths
        maxLabelWidth = diagram.maxLabelWidth
        maxLabelHeight = diagram.maxLabelHeight
    }

    /**
     * Draw labels on x-axis on [canvas]
     * starting from x=[x0],
     * on y-level [y].
     */
    fun draw(canvas: Canvas, x0: Float, y: Float) {
        // Draw each of the labels
        for ((i, label) in labels.withIndex()) {
            val xMid = x0 + xStep * i

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

    /**
     * Get bounding [Rect] of horizontal labels,
     * if it's drawn on level [y].
     */
    fun bounds(y: Float) = Rect(
        0f,
        0f,
        xStep * labels.size + labelWidths.last(),
        y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
    )
}