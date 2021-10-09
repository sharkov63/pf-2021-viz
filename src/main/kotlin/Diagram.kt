import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PaintMode

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

private fun paintByColorCode(colorCode: Int) = Paint().apply {
    color = colorCode
    mode = PaintMode.FILL
    strokeWidth = 1f
}

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
            else -> listOf(0xDB2B39, 0x29335C, 0xF3A712, 0xF0CEA0, 0xF0CEA0, 0xCC4BC2)
        }
        return colorCodesWithoutAlpha.map { paintByColorCode(it or 0xFF000000.toInt()) }
    }

    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        val paints = getPaints(data.size)
        val sumValues = data.sumOf { it.value }
        val arcLens = data.map { (it.value * 360f / sumValues).toFloat() }
        var arcStart = 0f
        for (i in data.indices) {
            val arcLen = arcLens[i]
            canvas.drawArc(x0, y0, x0 + sz, y0 + sz, arcStart, arcLen, true, paints[i % paints.size])
            arcStart += arcLen
        }
    }
}

class LineDiagram(data: Data) : Diagram(data) {
    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}