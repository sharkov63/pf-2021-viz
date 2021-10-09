import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.WindowConstants



fun main(args: Array<String>) {
    if (args.isNotEmpty() && (args.first() == "-h" || args.first() == "--help")) {
        return exitHelp()
    }

    val options = parseOptions(args.toList()) ?: return exitInvalidArgs()

    val (data, skippedRecords) = readDataWithSkipStats(options.inputFile)
    println("Successfully read ${data.size + skippedRecords} records.")
    if (skippedRecords > 0) {
        println("$skippedRecords of those records are invalid and thus, omitted.")
    }

    if (data.isEmpty()) {
        return exitEmptyData()
    }

    val diagram = when (options.diagramType) {
        DiagramType.BAR -> BarDiagram(data)
        DiagramType.PIE -> PieDiagram(data)
        DiagramType.LINE -> LineDiagram(data)
    }
    createDiagramWindow("pf-2021-viz", diagram)
}