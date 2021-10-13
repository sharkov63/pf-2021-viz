import org.jetbrains.skija.EncodedImageFormat
import org.jetbrains.skija.Image
import java.io.File
import java.nio.file.Files

private fun ensureAncestorDirectories(file: File) {
    val parentPath = file.absoluteFile.parentFile.toPath()
    Files.createDirectories(parentPath)
}

private fun ensureFile(file: File) {
    ensureAncestorDirectories(file)
    file.createNewFile()
}

fun getScreenshotPNGData(): ByteArray? {
    val bitMap = window.layer.screenshot() ?: return null
    val image = Image.makeFromBitmap(bitMap)
    val pngData = image.encodeToData(EncodedImageFormat.PNG) ?: return null
    return pngData.bytes
}

fun writeScreenshotToFile(file: File) {
    val bytes = getScreenshotPNGData()
    if (bytes == null) {
        println("Couldn't extract PNG byte data from window layer.")
        return
    }
    ensureFile(file)
    if (!file.canWrite()) {
        println("Cannot write to file \"${file.path}\"")
        return
    }
    file.writeBytes(bytes)
    println("Successfully written PNG data to \"${file.path}\"")
}