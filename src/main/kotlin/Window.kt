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


const val MIN_WINDOW_WIDTH = 100
const val MIN_WINDOW_HEIGHT = 100
const val WINDOW_PADDING = 15
const val CANVAS_UNIT = 1.1f



/**
 * Create a window and draw [diagram] on it.
 */
fun createDiagramWindow(title: String, diagram: Diagram) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer, diagram)

    // Predict diagram size in window
    val bounds = diagram.bounds()
    window.preferredSize = Dimension(
        (bounds.width * CANVAS_UNIT + 2 * WINDOW_PADDING).toInt(),
        (bounds.height * CANVAS_UNIT + 2 * WINDOW_PADDING).toInt()
    )

    window.minimumSize = Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}


/**
 * A custom [SkiaRenderer] which would draw [diagram].
 */
class Renderer(val layer: SkiaLayer, val diagram: Diagram): SkiaRenderer {

    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)

        val bounds = diagram.bounds()

        // Draw diagram only once.
        // No need for layer.needRedraw()
        diagram.draw(
            canvas,
            WINDOW_PADDING - bounds.left,
            WINDOW_PADDING - bounds.top,
        )
    }
}