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
    "round" to DiagramType.PIE,
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



open class Diagram(val data: Data) {
    // TODO("Diagram title")
    // TODO("Measure units")
    // TODO("Add selectable options")

    val minValue = data.minOf { it.value }
    val maxValue = data.maxOf { it.value }
    val sumValues = data
        .sumOf { it.value.toDouble() }
        .toFloat()

    val labels = data.map { it.label }

    open fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        throw Exception("Unable to draw Diagram")
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

        val arcLens = data.map { (it.value * 360f / sumValues) }
        val arcStarts = arcLens.scan(0f) { s, arcLen -> s + arcLen }

        // Draw arcs
        for (i in data.indices) {
            val arcLen = arcLens[i]
            val arcStart = arcStarts[i]
            canvas.drawArc(
                x1,
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


open class PlaneDiagram(data: Data, cropBottom: Boolean): Diagram(data) {

    private fun calcRulerStep(range: Float): Float {
        if (range == 0f) {
            return 0f
        }
        val k = floor(log10(range)).toInt()
        val d = 10f.pow(k)
        return when {
            2 * d > range -> d / 10f
            3 * d > range -> d / 5f
            4 * d > range -> d / 2f
            else -> d
        }
    }

    val rangeValues = if (cropBottom) {
        maxValue - minValue
    } else {
        maxValue
    }
    val rulerStep = calcRulerStep(rangeValues)
    val rulerBeginRel = if (cropBottom) {
        floor(minValue / rulerStep).toInt()
    } else {
        0
    }
    val rulerBegin = rulerBeginRel * rulerStep
    val rulerEndRel = ceil(maxValue / rulerStep).toInt()
    val rulerEnd = rulerEndRel * rulerStep
    val rulerRangeRel = rulerEndRel - rulerBeginRel
    val rulerRange = rulerEnd - rulerBegin

    fun getYCoords(y0: Float, y1: Float): List<Float> {
        return data.map { y1 - (it.value - rulerBegin) / rulerRange * (y1 - y0) }
    }

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
        val rulerStepIsInteger = floor(rulerStep) == rulerStep
        val decimals = max(0, -floor(log10(rulerStep)).toInt())
        val leak = 5f
        if (drawVerticalLine) {
            canvas.drawLine(x0 - leak, y1, x0 - leak, y0 - leak, LIGHT_GREY_STROKE_PAINT)
        }
        for (i in rulerBeginRel..rulerEndRel) {
            val y = y1 - yStep * (i - rulerBeginRel)
            canvas.drawLine(x0 - leak, y, x1 + leak, y, LIGHT_GREY_STROKE_PAINT)
            val label = if (rulerStepIsInteger)
                (rulerStep * i).toInt().toString()
            else
                "%.${decimals}f".format(rulerStep * i)
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height
            canvas.drawString(
                label,
                x0 - labelWidth - 2 * leak,
                y + labelHeight / 2,
                font,
                BLACK_FILL_PAINT
            )
        }
    }

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
        for ((i, label) in labels.withIndex()) {
            val xMid = x0 + dx * i
            if (drawMarks) {
                canvas.drawLine(xMid, y + 3f, xMid, y - 3f, BLACK_FILL_PAINT)
            }
            canvas.drawString(
                label,
                xMid - labelWidths[i] / 2,
                y + maxLabelHeight + 5f,
                font,
                BLACK_FILL_PAINT
            )
        }
    }

}

class BarDiagram(data: Data, cropBottom: Boolean = false) : PlaneDiagram(data, cropBottom) {
    private val barPaint = fillPaintByColorCode(0xFF4F86C6.toInt())

    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        val font = FONT.makeWithSize(sz * 0.03f).apply {
            isEmboldened = true
        }
        val y1 = y0 + sz

        // Draw bars
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val barWidth = max(maxLabelWidth + 2f, sz * 0.12f)
        val xGap = sz * 0.05f
        val xStep = barWidth + xGap
        for (i in data.indices) {
            val x = x0 + xStep * i
            val y = yCoords[i]
            canvas.drawRect(Rect(x, y, x + barWidth, y1), barPaint)
        }

        val x2 = x0 + xStep * (data.size - 1) + barWidth

        drawHorizontalLabels(
            canvas,
            x0 + barWidth / 2,
            xStep,
            font,
            y1,
            false,
        )

        drawRuler(
            canvas,
            y0,
            y1,
            x0,
            x2,
            font,
        )
    }
}

class LineDiagram(data: Data, cropBottom: Boolean = true) : PlaneDiagram(data, cropBottom) {

    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        val font = FONT.makeWithSize(sz * 0.03f).apply {
            isEmboldened = true
        }
        val linePaint = fillPaintByColorCode(0xFF4F86C6.toInt()).apply() {
            strokeWidth = 0.005f * sz
        }
        val pointPaint = fillPaintByColorCode(0xFF4F86C6.toInt()).apply() {
            strokeWidth = 0.012f * sz
        }


        // Draw lines and points
        val y1 = y0 + sz
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }

        val xMargin = max(maxLabelWidth / 2, sz * 0.05f)
        val x1 = x0 + xMargin
        val xStep = max(maxLabelWidth, sz * 0.15f)
        val points = data.mapIndexed { i, _ ->
            Pair(x1 + xStep * i, yCoords[i])
        }
        val pointsFlatten = points
            .flatMap { (x, y) -> listOf(x, y) }
            .toFloatArray()
        canvas.drawPolygon(pointsFlatten, linePaint)
        points.forEach { (x, y) ->
            canvas.drawPoint(x, y, pointPaint)
        }

        drawRuler(
            canvas,
            y0,
            y1,
            x0,
            x1 + xStep * (data.size - 1) + xMargin,
            font,
            true,
        )

        drawHorizontalLabels(
            canvas,
            x1,
            xStep,
            font,
            y1,
            true,
        )
    }
}