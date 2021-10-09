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

open class Diagram(val data: Data) {
    open fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}

class BarDiagram(data: Data) : Diagram(data) {
    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}

class PieDiagram(data: Data) : Diagram(data) {

    private fun getPaints(sz: Int): List<Paint> {
        assert(sz > 0)
        val colorCodesWithoutAlpha = when {
            sz % 7 != 1 -> listOf(0x2D7DD2, 0x62A56B, 0x97CC04, 0xC69503, 0xF45D01, 0xF45D01, 0x64403E)
            else -> listOf(0x0B3954, 0x087E8B, 0xBFD7EA, 0xFF5A5F, 0xC81D25, 0x9368B7)
        }
        return colorCodesWithoutAlpha.map { fillPaintByColorCode(it or 0xFF000000.toInt()) }
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
            canvas.drawArc(x1 , y1, x1 + sz, y1 + sz, arcStart, arcLen, true, paints[i % paints.size])
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
            canvas.drawRect(colorBox, BLACK_STROKE_PAINT)
            canvas.drawRect(colorBox, colorBoxFill)

            // Draw lines
            if (i >= paints.size || i + paints.size < data.size){
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

    }
}