import org.jetbrains.skija.Font

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
abstract class PlaneDiagram(
    data: Data,
    scale: Float,
    val cropBottom: Boolean,
    drawRulerLine: Boolean,
    drawHorizontalMarks: Boolean,
) : Diagram(data, scale) {

    companion object {
        const val FONT_SIZE_COEFFICIENT = 0.03f
    }

    val ruler: PlaneDiagramRuler
    val horizontalLabels: PlaneDiagramHorizontalLabels

    val font: Font


    init {
        ruler = PlaneDiagramRuler(this, drawRulerLine)
        horizontalLabels = PlaneDiagramHorizontalLabels(this, drawHorizontalMarks)
        font = FONT.makeWithSize(scale * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true // bold labels
        }
    }


    /**
     * Get coordinates for value (y) axis on range [y0]..[y1]
     */
    fun getYCoords(y0: Float, y1: Float): List<Float> {
        return data.map { y1 - (it.value - ruler.begin) / (ruler.step * ruler.rangeRel) * (y1 - y0) }
    }
}