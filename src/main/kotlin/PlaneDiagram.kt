import org.jetbrains.skija.*


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
abstract class PlaneDiagram(data: Data, val cropBottom: Boolean) : Diagram(data) {

    companion object {
        const val FONT_SIZE_COEFFICIENT = 0.03f
    }

    val ruler = PlaneDiagramRuler(this)
    val horizontalLabels = PlaneDiagramHorizontalLabels(this)


    fun getValueRange() = if (cropBottom || minValue < 0) {
        maxValue - minValue
    } else {
        maxValue
    }

    fun getBeginValue() = if (cropBottom || minValue < 0) {
        minValue
    } else {
        0f
    }


    /**
     * Get coordinates for value (y) axis on range [y0]..[y1]
     */
    fun getYCoords(y0: Float, y1: Float): List<Float> {
        return data.map { y1 - (it.value - ruler.begin) / (ruler.step * ruler.rangeRel) * (y1 - y0) }
    }
}