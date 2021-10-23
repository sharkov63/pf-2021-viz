import org.jetbrains.skija.*
import kotlin.math.*


/**
 * Pie diagram.
 *
 * Data with negative values or with zero-sum is not allowed.
 */
class PieDiagram(data: Data, scale: Float) : Diagram(data, scale) {

    companion object {
        const val FONT_SIZE_COEFFICIENT = 0.05f
        const val BLANK_WIDTH_PROPORTION = 0.1f
        const val COLOR_BOX_HEIGHT_PROPORTION = 0.7f
        const val LABEL_Y_STEP_PROPORTION = 1.3f
        const val LABEL_LINE_INDENT = 10f

        val COLOR_PALLET = listOf(
            0xca3f3f,
            0xe0607e,
            0xab92bf,
            0x655a7c,
            0x696d7d,
            0x68b0ab,
            0x8fc0a9,
            0xc8d5b9,
            0xeace71,
            0xa75a39,
        )
    }

    val paints: List<Paint>

    val font: Font
    val radius: Float
    val maxLabelWidth: Float
    val maxLabelHeight: Float
    val blankWidth: Float
    val yStep: Float

    init {
        checkDataCorrectness()

        paints = getPaints(data.size)

        // Prepare geometry
        font = FONT.makeWithSize(scale * FONT_SIZE_COEFFICIENT)
        radius = scale / 2
        maxLabelWidth = labels.maxOf { font.measureTextWidth(it, BLACK_FILL_PAINT) }
        maxLabelHeight = labels.maxOf { font.measureText(it, BLACK_FILL_PAINT).height }
        blankWidth = scale * BLANK_WIDTH_PROPORTION
        yStep = maxLabelHeight * LABEL_Y_STEP_PROPORTION
    }



    private fun checkDataCorrectness() {
        val negativeElement = data.find { it.value < 0 }
        if (negativeElement != null) {
            exitNegativeValues(DiagramType.PIE, negativeElement)
        }

        if (sumValues <= 0f) {
            exitPieZeroSum()
        }
    }


    /**
     * Forms a list of colors for diagram.
     */
    private fun getPaints(records: Int): List<Paint> {
        assert(records > 0)

        var rgbCodes = COLOR_PALLET
        // Ensure that first and last color are not equal
        if (records > rgbCodes.size && records % rgbCodes.size == 1) {
            rgbCodes = rgbCodes.dropLast(1)
        }
        val k = rgbCodes.size / records
        if (k >= 2) { // make colors more distinct
            rgbCodes = rgbCodes.filterIndexed { idx, _ -> idx % k == 0 }
        }
        return rgbCodes.map { fillPaintByColorCode(it or 0xFF000000.toInt()) }
    }



    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float) {
        val x1 = x0 + maxLabelWidth + blankWidth
        val xc = x1 + radius
        val yc = y0 + radius

        // Sectors (arcs) geometry
        val arcLens = values.map { (it * 360f / sumValues) }
        val arcStarts = arcLens.scan(0f) { s, arcLen -> s + arcLen }

        // Draw arcs
        for (i in data.indices) {
            val arcLen = arcLens[i]
            val arcStart = arcStarts[i]
            canvas.drawArc(
                x1,
                y0,
                x1 + scale,
                y0 + scale,
                arcStart,
                arcLen,
                true,
                paints[i % paints.size],
            )
        }

        // Draw labels
        val colorBoxSz = maxLabelHeight * COLOR_BOX_HEIGHT_PROPORTION
        val colorBoxMarginX = (maxLabelHeight - colorBoxSz) / 2
        labels.forEachIndexed { i, label ->
            val yCur = y0 + yStep * (i + 1)
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height

            // Draw color box
            val colorBoxMarginY = (labelHeight - colorBoxSz) / 2
            val colorBoxFill = paints[i % paints.size]
            val colorBox = Rect(
                x0 + colorBoxMarginX,
                yCur - labelHeight + colorBoxMarginY,
                x0 + colorBoxMarginX + colorBoxSz,
                yCur - labelHeight + colorBoxMarginY + colorBoxSz,
            )
            canvas.drawRect(colorBox, colorBoxFill)
            canvas.drawRect(colorBox, BLACK_STROKE_PAINT)

            // Draw lines, if colors coincide
            if (i >= paints.size || i + paints.size < data.size) {
                val arcMid = (arcStarts[i] + arcStarts[i + 1]) / 2
                val arcMidRad = arcMid / 360 * 2 * PI.toFloat()
                val x = xc + radius / 2 * cos(arcMidRad)
                val y = yc + radius / 2 * sin(arcMidRad)
                canvas.drawPolygon(
                    floatArrayOf(
                        x0 + maxLabelHeight + labelWidth + LABEL_LINE_INDENT, yCur - labelHeight / 2,
                        x0 + maxLabelHeight + maxLabelWidth + LABEL_LINE_INDENT, yCur - labelHeight / 2,
                        x, y,
                    ),
                    LIGHT_GREY_STROKE_PAINT,
                )
            }

            // Draw label
            canvas.drawString(label, x0 + maxLabelHeight, yCur, font, BLACK_FILL_PAINT)
        }
    }

    /**
     * Get bounding [Rect] of diagram.
     */
    override fun bounds() = Rect(
        0f,
        0f,
        maxLabelWidth + blankWidth + scale,
        max(scale, yStep * data.size),
    )
}
