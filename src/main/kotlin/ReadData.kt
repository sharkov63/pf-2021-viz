import java.io.File

/**
 * This component provides reading data for diagrams.
 * TODO("Allow reading data from multiple files")
 */


/**
 * Diagram [data] with the number of [skipped] elements.
 */
data class DataWithSkipStats(val data: Data, val skipped: Int)



/**
 * Get diagram data from a file.
 * Invalid data records are skipped.
 * The number of skipped records is counted in [DataWithSkipStats] class.
 */
fun readDataFromFile(file: File): DataWithSkipStats {
    assert(file.exists())
    assert(file.canRead())
    val lines = file.readLines()
    return parseDataFromLines(lines)
}



/**
 * Get diagram data from standard input.
 * Invalid data records are skipped.
 * The number of skipped records is counted in [DataWithSkipStats] class.
 */
fun readDataFromStdin(): DataWithSkipStats {
    var line = readLine()
    val lines: MutableList<String> = mutableListOf()
    while (line != null) { // Read next line while can
        lines.add(line)
        line = readLine()
    }
    return parseDataFromLines(lines)
}



/**
 * Get diagram data either from a specified file,
 * or standard input (in case file is not specified, or cannot be reached or read).
 *
 * Invalid data records are skipped.
 * The number of skipped records is counted in [DataWithSkipStats] class.
 */
fun readDataWithSkipStats(file: File?): DataWithSkipStats {
    return if (file != null && file.exists() && file.canRead())
        readDataFromFile(file)
    else {
        println(
            if (file == null)
                "Input file is not specified."
            else
                "File \"${file.path}\" does not exist or cannot be read."
        )
        println("Please write the data in console:")
        readDataFromStdin()
    }
}