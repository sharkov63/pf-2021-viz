import org.jetbrains.skija.*

/**
 * [Drawable] is anything that can be drawn on skija [Canvas].
 */
abstract class Drawable() {

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
        bitmap.imageInfo = ImageInfo(
            bounds.width.toInt() + 2 * PNG_PADDING,
            bounds.height.toInt() + 2 * PNG_PADDING,
            ColorType.BGRA_8888,
            ColorAlphaType.PREMUL
        )
        bitmap.allocPixels()
        val canvas = Canvas(bitmap)
        draw(canvas, -bounds.left + PNG_PADDING, -bounds.top + PNG_PADDING)
        canvas.readPixels(bitmap, 0, 0)

        val image = Image.makeFromBitmap(bitmap)
        val pngData = image.encodeToData(EncodedImageFormat.PNG) ?: return null
        return pngData.bytes
    }
}