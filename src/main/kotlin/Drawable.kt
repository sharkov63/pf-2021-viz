import org.jetbrains.skija.*

/**
 * [Drawable] is anything that can be drawn on skija [Canvas].
 */
abstract class Drawable {

    companion object {
        const val PNG_PADDING = 5
    }

    /**
     * Draws the object on [canvas] with pivot point at ([x0], [y0]).
     */
    abstract fun draw(canvas: Canvas, x0: Float, y0: Float)

    /**
     * Predicts bounding rectangle,
     * if the pivot point is at (0, 0)
     */
    abstract fun bounds(): Rect

    /**
     * Returns the PNG data of the object.
     */
    fun getPNGData(): ByteArray? {
        val bounds = bounds()

        val bitmap = Bitmap()
        val bitmapWidth = bounds.width.toInt() + 2 * PNG_PADDING
        val bitmapHeight = bounds.height.toInt() + 2 * PNG_PADDING
        bitmap.imageInfo = ImageInfo(
            bitmapWidth,
            bitmapHeight,
            ColorType.BGRA_8888,
            ColorAlphaType.PREMUL
        )
        bitmap.allocPixels()
        val canvas = Canvas(bitmap)
        val whitePaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
        }
        canvas.drawRect(Rect(0f, 0f, bitmapWidth.toFloat(), bitmapHeight.toFloat()), whitePaint)
        draw(canvas, -bounds.left + PNG_PADDING, -bounds.top + PNG_PADDING)
        canvas.readPixels(bitmap, 0, 0)

        val image = Image.makeFromBitmap(bitmap)
        val pngData = image.encodeToData(EncodedImageFormat.PNG) ?: return null
        return pngData.bytes
    }
}