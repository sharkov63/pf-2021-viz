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

// TODO("remove this global paint")
val paints = listOf(
    Paint().apply {
        color = 0xff9BC730L.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    },
    Paint().apply {
        color = 0xffff0000.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    }
)
val paint = paints[0]

open class Diagram(val data: Data) {
    open fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}

class BarDiagram(data: Data) : Diagram(data) {
    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}

class PieDiagram(data: Data) : Diagram(data) {
    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {
        val sumValues = data.sumOf { it.value }
        val arcLens = data.map { (it.value * 360f / sumValues).toFloat() }
        var arcStart = 0f
        for (i in data.indices) {
            val arcLen = arcLens[i]
            canvas.drawArc(x0, y0, x0 + sz, y0 + sz, arcStart, arcLen, true, paints[i % 2])
            arcStart += arcLen
        }
    }
}

class LineDiagram(data: Data) : Diagram(data) {
    override fun draw(canvas: Canvas, x0: Float, y0: Float, sz: Float) {

    }
}