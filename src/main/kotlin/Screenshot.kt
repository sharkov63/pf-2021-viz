import org.jetbrains.skija.*
import java.io.File
import java.nio.file.Files


/**
 * This component allows to write PNG data to a file.
 */

const val PADDING = 5


/* Aux functions for file creation */
private fun ensureAncestorDirectories(file: File) {
    val parentPath = file.absoluteFile.parentFile.toPath()
    Files.createDirectories(parentPath)
}

private fun ensureFile(file: File) {
    ensureAncestorDirectories(file)
    file.createNewFile()
}

fun writeDiagramToFile(file: File, diagram: Diagram, size: Float) {
    val bytes = diagram.getPNGData(size)
    requireNotNull(bytes)
    ensureFile(file)
    if (!file.canWrite()) {
        println("Cannot write to file \"${file.path}\"")
        return
    }
    file.writeBytes(bytes)
    println("Successfully written PNG data to \"${file.path}\"")
}