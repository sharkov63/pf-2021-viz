import java.io.File
import kotlin.text.*

/**
 * This component provides reading data for diagrams.
 */

// TODO("Allow reading data from multiple files")



/**
 * A single diagram record (component), consisting of
 * its' name [label] and [value].
 */
data class DataElement(val label: String, val value: Float)

/**
 * Diagram data.
 */
typealias Data = List<DataElement>

/**
 * Diagram [data] with the number of [skipped] elements.
 */
data class DataWithSkipStats(val data: Data, val skipped: Int)


/**
 * Get a data record from a string.
 * Returns null, if the string is invalid.
 */
fun parseDataElementOrNull(line: String): DataElement? {
    val tokens = line
        .trimEnd()
        .reversed() // all reverses are because .split(limit = 2) limits from beginning, not from end
        .split(' ', limit = 2)
        .reversed()
        .map { it.reversed().trimEnd() }
    if (tokens.size == 1) {
        // at least two tokens are requires: for the label and for the value
        return null
    }
    val key = tokens.first()
    val value = tokens.last().toFloatOrNull() ?: return null
    if (value.isInfinite() || value.isNaN()) {
        // bad float
        return null
    }
    return DataElement(key, value)
}

/**
 * Get diagram data from a list of [lines].
 * Invalid data records are skipped.
 * The number of skipped records is counted in [DataWithSkipStats] class.
 */
fun parseDataFromLines(lines: List<String>): DataWithSkipStats {
    val dataWithNulls = lines.map { parseDataElementOrNull(it) }
    val data = dataWithNulls.filterNotNull()
    val nullCount = dataWithNulls.count { it == null }
    return DataWithSkipStats(data, nullCount)
}

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