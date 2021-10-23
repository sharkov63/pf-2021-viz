import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Diagram on a 2D plane:
 * y-axis is for values;
 * x-axis is for labels.
 *
 * cropBottom is a flag, which determines
 * whenever the value axis should start from zero (cropBottom = false),
 * or from the smallest value in data (cropBottom = true).
 *
 * Incorporates [BarDiagram] and [LineDiagram].
 */
abstract class PlaneDiagram(data: Data, cropBottom: Boolean) : Diagram(data) {

    companion object {
        const val FONT_SIZE_COEFFICIENT = 0.03f
        const val RULER_LEAK = 5f
        const val MARK_LEAK = 3f
        const val HORIZONTAL_LABELS_INDENT = 5f
    }

    private val rulerStep: Float
    private val rulerBegin: Float
    private val rulerRangeRel: Int
    private val rulerLabels: List<String>

    init {
        /* Value range depending on cropBottom */
        val rangeValues = if (cropBottom || minValue < 0) {
            maxValue - minValue
        } else {
            maxValue
        }

        /* Ruler parameters */
        rulerStep = calcRulerStep(rangeValues)
        val rulerBeginRel = if (cropBottom || minValue < 0) {
            floor(minValue / rulerStep).toInt()
        } else {
            0
        }
        rulerBegin = rulerBeginRel * rulerStep
        val rulerEndRel = floor(maxValue / rulerStep).toInt() + 1
        rulerRangeRel = rulerEndRel - rulerBeginRel

        /* Calc ruler labels */
        val rulerStepIsInteger = floor(rulerStep) == rulerStep
        val decimals = max(0, -floor(log10(rulerStep)).toInt())
        rulerLabels = List(rulerRangeRel + 1) {
            val rulerValue = rulerBegin + it * rulerStep
            if (rulerStepIsInteger)
                rulerValue.toInt().toString()
            else
                "%.${decimals}f".format(rulerValue)
        }
    }



    /**
     * Calculate a somewhat pretty delta value for ruler measuring [range].
     */
    private fun calcRulerStep(range: Float): Float {
        if (range == 0f) {
            // degenerate case
            return 1f
        }
        val k = floor(log10(range)).toInt()
        val d = 10f.pow(k) // the largest power of ten <= range
        return when { // divide if there are too few segments
            2 * d > range -> d / 10f
            3 * d > range -> d / 5f
            4 * d > range -> d / 2f
            else -> d
        }
    }


    /**
     * Get coordinates for value (y) axis on range [y0]..[y1]
     */
    fun getYCoords(y0: Float, y1: Float): List<Float> {
        return data.map { y1 - (it.value - rulerBegin) / (rulerStep * rulerRangeRel) * (y1 - y0) }
    }


    /**
     * Draw ruler on [canvas]
     * with y-range [y0]..[y1],
     * x-range [x0]..[x1],
     * and number labels with [font].
     *
     * One can choose to [drawVerticalLine] or not.
     */
    fun drawRuler(
        canvas: Canvas,
        y0: Float,
        y1: Float,
        x0: Float,
        x1: Float,
        font: Font,
        drawVerticalLine: Boolean = false,
    ) {
        val yStep = (y1 - y0) / rulerRangeRel
        if (drawVerticalLine) {
            canvas.drawLine(x0 - RULER_LEAK, y1, x0 - RULER_LEAK, y0 - RULER_LEAK, LIGHT_GREY_STROKE_PAINT)
        }

        // Draw each of level lines
        for (i in 0..rulerRangeRel) {
            val y = y1 - yStep * i

            // Draw line
            canvas.drawLine(x0 - RULER_LEAK, y, x1 + RULER_LEAK, y, LIGHT_GREY_STROKE_PAINT)

            // Draw label
            val label = rulerLabels[i]
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height
            canvas.drawString(
                label,
                x0 - labelWidth - 2 * RULER_LEAK,
                y + labelHeight / 2,
                font,
                BLACK_FILL_PAINT
            )
        }
    }

    fun rulerBound(size: Float, font: Font): Rect {
        val maxLabelWidth = rulerLabels.maxOf { font.measureTextWidth(it) }
        return Rect(
            -maxLabelWidth - 2 * RULER_LEAK,
            0f,
            0f,
            size + font.measureText(rulerLabels.first()).height
        )
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