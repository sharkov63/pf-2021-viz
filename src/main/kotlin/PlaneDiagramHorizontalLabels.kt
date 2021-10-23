import org.jetbrains.skija.*

class PlaneDiagramHorizontalLabels(diagram: PlaneDiagram, private val drawMarks: Boolean) {

    companion object {
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }

    val labels: List<String>
    val font: Font
    val labelRects: List<Rect>
    val labelWidths: List<Float>
    val labelHeights: List<Float>
    val maxLabelWidth: Float
    val maxLabelHeight: Float

    init {
        labels = diagram.labels
        font = diagram.font
        labelRects = labels.map { label -> font.measureText(label) }
        labelWidths = labelRects.map { rect -> rect.width }
        labelHeights = labelRects.map { rect -> rect.height }
        maxLabelWidth = labelWidths.maxOf { it }
        maxLabelHeight = labelHeights.maxOf { it }
    }


    /**
     * Draw labels on x-axis on [canvas]
     * starting from x=[x0],
     * with x-step [dx],
     * on y-level [y].
     */
    fun draw(canvas: Canvas, x0: Float, dx: Float, y: Float) {
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

    /**
     * Get bounding [Rect] of horizontal labels,
     * if it's drawn with x-step=[dx] on level [y].
     */
    fun bounds(dx: Float, y: Float) = Rect(
        0f,
        0f,
        dx * labels.size + labelWidths.last(),
        y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
    )
}