import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import javax.swing.WindowConstants

/**
 * This component contains functions for working with [SkiaWindow] and [SkiaRenderer].
 */


/**
 * Global [window].
 *
 * Currently, I found no way to make it not global:
 * I couldn't extract it from Swing CoroutineScope
 */
val window = SkiaWindow()

/**
 * Create window and draw diagram on it.
 */
fun createDiagramWindow(title: String, diagram: Diagram) = runBlocking(Dispatchers.Swing) {
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer, diagram)

    window.preferredSize = Dimension(1200, 600)
    window.minimumSize = Dimension(100, 100)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}

/**
 * A custom [SkiaRenderer] which would draw a diagram.
 */
class Renderer(val layer: SkiaLayer, val diagram: Diagram): SkiaRenderer {

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)

        // Draw diagram only once.
        // No need for layer.needRedraw()
        diagram.draw(canvas, 100f, 100f, 400f)
    }
}