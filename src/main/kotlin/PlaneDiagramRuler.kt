import org.jetbrains.skija.*
import kotlin.math.*

class PlaneDiagramRuler(diagram: PlaneDiagram) {

    companion object {
        const val RULER_LEAK = 5f
    }

    val step: Float
    val begin: Float
    val rangeRel: Int

    private val labels: List<String>

    init {
        step = calcRulerStep(diagram.getValueRange())
        val rulerBeginRel = floor(diagram.getBeginValue() / step).toInt()
        begin = rulerBeginRel * step
        val rulerEndRel = floor(diagram.maxValue / step).toInt() + 1
        rangeRel = rulerEndRel - rulerBeginRel

        labels = calcLabels()
    }



    /**
     * Calculate a somewhat pretty delta value for ruler measuring [range].
     */
    private fun calcRulerStep(range: Float): Float {
        if (range <= 0f) {
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
     * Prepare pretty value labels.
     */
    private fun calcLabels(): List<String> {
        val rulerStepIsInteger = floor(step) == step
        val decimals = max(0, -floor(log10(step)).toInt())
        return List(rangeRel + 1) {
            val rulerValue = begin + it * step
            if (rulerStepIsInteger)
                rulerValue.toInt().toString()
            else
                "%.${decimals}f".format(rulerValue)
        }
    }


    /**
     * Draw ruler on [canvas]
     * with y-range [y0]..[y1],
     * x-range [x0]..[x1],
     * and number labels with [font].
     *
     * One can choose to [drawVerticalLine] or not.
     */
    fun draw(
        canvas: Canvas,
        y0: Float,
        y1: Float,
        x0: Float,
        x1: Float,
        font: Font,
        drawVerticalLine: Boolean = false,
    ) {
        val yStep = (y1 - y0) / rangeRel
        if (drawVerticalLine) {
            canvas.drawLine(x0 - RULER_LEAK, y1, x0 - RULER_LEAK, y0 - RULER_LEAK, LIGHT_GREY_STROKE_PAINT)
        }

        // Draw each of level lines
        for (i in 0..rangeRel) {
            val y = y1 - yStep * i

            // Draw line
            canvas.drawLine(x0 - RULER_LEAK, y, x1 + RULER_LEAK, y, LIGHT_GREY_STROKE_PAINT)

            // Draw label
            val label = labels[i]
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

    fun bounds(size: Float, font: Font): Rect {
        val maxLabelWidth = labels.maxOf { font.measureTextWidth(it) }
        return Rect(
            -maxLabelWidth - 2 * RULER_LEAK,
            0f,
            0f,
            size + font.measureText(labels.first()).height
        )
    }
}