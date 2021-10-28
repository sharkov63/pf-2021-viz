import org.jetbrains.skija.*
import kotlin.math.*

/**
 * A drawable component of PlaneDiagram:
 * vertical ruler on value (Y) axis with labels and horizontal lines.
 */
class PlaneDiagramRuler(
    val diagram: PlaneDiagram,
    private val linesLength: Float,
    private val drawVerticalLine: Boolean,
) : Drawable() {

    companion object {
        const val RULER_LEAK = 5f
    }

    val step: Float
    val begin: Float
    val range: Float
    val rangeRel: Int

    val labels: List<String>

    init {
        /* Diagram value range */
        val needCrop = diagram.cropBottom || diagram.minValue < 0
        val rangeValue = if (needCrop) {
            diagram.maxValue - diagram.minValue
        } else {
            diagram.maxValue
        }
        val beginValue = if (needCrop) {
            diagram.minValue
        } else {
            0f
        }

        /* Ruler params */
        step = calcRulerStep(rangeValue)
        val rulerBeginRel = floor(beginValue / step).toInt()
        begin = rulerBeginRel * step
        val rulerEndRel = floor(diagram.maxValue / step).toInt() + 1
        rangeRel = rulerEndRel - rulerBeginRel
        range = rangeRel * step

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
     * with starting from pivot ([pivotX], [pivotY]).
     */
    override fun draw(canvas: Canvas, pivotX: Float, pivotY: Float) {
        val x1 = pivotX + linesLength
        val y1 = pivotY + diagram.scale
        val yStep = diagram.scale / rangeRel

        if (drawVerticalLine) {
            canvas.drawLine(pivotX, y1, pivotX, pivotY - RULER_LEAK, LIGHT_GREY_STROKE_PAINT)
        }

        // Draw each of level lines
        for (i in 0..rangeRel) {
            val y = y1 - yStep * i

            // Draw line
            canvas.drawLine(pivotX - RULER_LEAK, y, x1, y, LIGHT_GREY_STROKE_PAINT)

            // Draw label
            val label = labels[i]
            val labelWidth = diagram.font.measureTextWidth(label)
            val labelHeight = diagram.font.measureText(label).height
            canvas.drawString(
                label,
                pivotX - labelWidth - 2 * RULER_LEAK,
                y + labelHeight / 2,
                diagram.font,
                BLACK_FILL_PAINT
            )
        }
    }

    /**
     * Get bounding [Rect] of ruler.
     */
    override fun bounds(): Rect {
        val maxLabelWidth = labels.maxOf { diagram.font.measureTextWidth(it) }
        return Rect(
            -maxLabelWidth - 2 * RULER_LEAK,
            -RULER_LEAK,
            linesLength,
            diagram.scale + diagram.font.measureText(labels.first()).height
        )
    }
}