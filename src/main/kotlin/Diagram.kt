import org.jetbrains.skija.*
import kotlin.math.*

enum class DiagramType {
    BAR,
    PIE,
    LINE,
}

val DEFAULT_DIAGRAM_TYPE = DiagramType.BAR

val diagramTypeByDescription = mapOf(
    "default" to DEFAULT_DIAGRAM_TYPE,
    "bar" to DiagramType.BAR,
    "histogram" to DiagramType.BAR,
    "column" to DiagramType.BAR,
    "pie" to DiagramType.PIE,
    "circle" to DiagramType.PIE,
    "line" to DiagramType.LINE,
    "graph" to DiagramType.LINE,
)



private fun fillPaintByColorCode(colorCode: Int) = Paint().apply {
    color = colorCode
    mode = PaintMode.FILL
    strokeWidth = 1f
}

val BLACK_FILL_PAINT = fillPaintByColorCode(0xFF000000.toInt())
val BLACK_STROKE_PAINT = fillPaintByColorCode(0xFF000000.toInt()).apply {
    mode = PaintMode.STROKE
}
val LIGHT_GREY_STROKE_PAINT = fillPaintByColorCode(0xFFAAAAAA.toInt()).apply {
    mode = PaintMode.STROKE
}

val TYPEFACE = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
val FONT = Font(TYPEFACE, 20f)



fun getRulerStep(maxValue: Float): Float {
    val k = log10(maxValue).toInt()
    val d = 10f.pow(k)
    return when {
        2 * d > maxValue -> d / 10f
        3 * d > maxValue -> d / 5f
        4 * d > maxValue -> d / 2f
        else -> d
    }
}


open class Diagram(val data: Data) {
    // TODO("Diagram title")
    // TODO("Measure units")
    // TODO("Add selectable options")

    open fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}

class BarDiagram(data: Data) : Diagram(data) {
    private val barPaint = fillPaintByColorCode(0xFF4F86C6.toInt())

    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        // TODO("Shift min value")

        val font = FONT.makeWithSize(sz * 0.03f).apply {
            isEmboldened = true
        }
        val y1 = y0 + sz

        // Draw bars
        val maxValue = data.maxOf { it.value }
        val barHeights = data.map { (it.value / maxValue * sz).toFloat() }
        val barWidth = sz * 0.2f
        val xStep = sz * 0.3f
        for (i in data.indices) {
            val x = x0 + xStep * i
            val y = y1 - barHeights[i]
            canvas.drawRect(Rect(x, y, x + barWidth, y1), barPaint)
        }

        val x2 = x0 + xStep * (data.size - 1) + barWidth

        // Draw labels
        for (i in data.indices) {
            val label = data[i].label
            val xMid = x0 + xStep * i + barWidth / 2
            val labelWidth = font.measureTextWidth(label, BLACK_FILL_PAINT)
            val labelHeight = font.measureText(label, BLACK_FILL_PAINT).height
            canvas.drawString(
                label,
                xMid - labelWidth / 2,
                y1 + labelHeight * 1.5f,
                font,
                BLACK_FILL_PAINT
            )
        }

        // Draw ruler
        val d = getRulerStep(maxValue.toFloat())
        val dIsInteger = floor(d) == d
        val decimals = max(0, -log10(d).toInt())
        val yStep = (d / maxValue * sz).toFloat()
        for (i in 0..ceil(maxValue / d).toInt()) {
            val y = y1 - yStep * i
            val xLeak = 5f
            canvas.drawLine(x0 - xLeak, y, x2 + xLeak, y, LIGHT_GREY_STROKE_PAINT)
            val label = if (dIsInteger)
                (d * i).toInt().toString()
            else
                "%.${decimals}f".format(d * i)
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height
            canvas.drawString(
                label,
                x0 - labelWidth - 2 * xLeak,
                y + labelHeight / 2,
                font,
                BLACK_FILL_PAINT
            )
        }
    }
}

class PieDiagram(data: Data) : Diagram(data) {

    private fun getPaints(sz: Int): List<Paint> {
        assert(sz > 0)
        var rgbCodes = listOf(
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
        if (sz > rgbCodes.size && sz % rgbCodes.size == 1)
            rgbCodes = rgbCodes.dropLast(1)
        val k = rgbCodes.size / sz
        if (k >= 2) // make colors more distinct
            rgbCodes = rgbCodes.filterIndexed{ idx, _ -> idx % k == 0 }
        return rgbCodes.map { fillPaintByColorCode(it or 0xFF000000.toInt()) }
    }

    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        val radius = sz / 2
        val font = FONT.makeWithSize(sz * 0.05f)
        val maxLabelWidth = data.maxOf { font.measureTextWidth(it.label, BLACK_FILL_PAINT) }
        val maxLabelHeight = data.maxOf { font.measureText(it.label, BLACK_FILL_PAINT).height }
        val blankWidth = sz * 0.1f
        val x1 = x0 + maxLabelWidth + blankWidth
        val y1 = y0
        val xc = x1 + sz / 2
        val yc = y1 + sz / 2

        val paints = getPaints(data.size)

        val sumValues = data.sumOf { it.value }
        val arcLens = data.map { (it.value * 360f / sumValues).toFloat() }
        val arcStarts = arcLens.scan(0f) { s, arcLen -> s + arcLen }

        // Draw arcs
        for (i in data.indices) {
            val arcLen = arcLens[i]
            val arcStart = arcStarts[i]
            canvas.drawArc(x1 ,
                y1,
                x1 + sz,
                y1 + sz,
                arcStart,
                arcLen,
                true,
                paints[i % paints.size]
            )
        }

        val colorBoxSz = maxLabelHeight * 0.7f
        val colorBoxMarginX = (maxLabelHeight - colorBoxSz) / 2
        val yStep = maxLabelHeight * 1.3f
        var yCur = y0 + yStep
        for (i in data.indices) {
            val label = data[i].label
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height

            // Draw color box
            val colorBoxMarginY = (labelHeight - colorBoxSz) / 2
            val colorBoxFill = paints[i % paints.size]
            val colorBox = Rect(
                x0 + colorBoxMarginX,
                yCur - labelHeight + colorBoxMarginY,
                x0 + colorBoxMarginX + colorBoxSz,
                yCur - labelHeight + colorBoxMarginY + colorBoxSz
            )
            canvas.drawRect(colorBox, colorBoxFill)
            canvas.drawRect(colorBox, BLACK_STROKE_PAINT)

            // Draw lines
            if (i >= paints.size || i + paints.size < data.size) {
                val arcMid = (arcStarts[i] + arcStarts[i + 1]) / 2
                val arcMidRad = arcMid / 360 * 2 * PI.toFloat()
                val x = xc + radius / 2 * cos(arcMidRad)
                val y = yc + radius / 2 * sin(arcMidRad)
                canvas.drawPolygon(
                    floatArrayOf(
                        x0 + maxLabelHeight + labelWidth + 10f,
                        yCur - labelHeight / 2,
                        x0 + maxLabelHeight + maxLabelWidth + 10f,
                        yCur - labelHeight / 2,
                        x,
                        y,
                    ),
                    LIGHT_GREY_STROKE_PAINT
                )
            }

            // Draw label
            canvas.drawString(label, x0 + maxLabelHeight, yCur, font, BLACK_FILL_PAINT)

            yCur += yStep
        }
    }
}

class LineDiagram(data: Data) : Diagram(data) {
    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        TODO("Draw line diagram")
    }
}