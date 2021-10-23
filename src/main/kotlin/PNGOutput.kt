import java.io.File
import java.nio.file.Files

/**
 * This component allows writing diagrams to a PNG file.
 */


/* Aux functions for file creation */

private fun ensureAncestorDirectories(file: File) {
    val parentPath = file.absoluteFile.parentFile.toPath()
    Files.createDirectories(parentPath)
}

private fun ensureFile(file: File) {
    ensureAncestorDirectories(file)
    file.createNewFile()
}


/**
 * Writes [diagram] to [file] in PNG format
 */
fun writeDiagramToPNGFile(file: File, diagram: Diagram) {
    val bytes = diagram.getPNGData()
    requireNotNull(bytes)
    ensureFile(file)
    if (!file.canWrite()) {
        println("Cannot write to file \"${file.path}\"")
        return
    }
    file.writeBytes(bytes)
    println("Successfully written PNG data to \"${file.path}\"")
}