import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Font
import org.jetbrains.skija.Rect

class PlaneDiagramHorizontalLabels(diagram: PlaneDiagram) {

    companion object {
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }

    private val labels: List<String>

    init {
        labels = diagram.labels
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
    fun draw(
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

    fun bounds(dx: Float, y: Float, font: Font): Rect {
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