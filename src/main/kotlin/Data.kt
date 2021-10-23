import kotlin.text.*

/**
 * This component contains Data and DataElement classes for diagram,
 * as well as parse data functions.
 */



/**
 * A single diagram record (component), consisting of
 * it's name [label] and [value].
 */
data class DataElement(val label: String, val value: Float)

/**
 * Diagram data.
 */
typealias Data = List<DataElement>



/**
 * Get a [DataElement] from a string.
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
        // at least two tokens are required: for the label and for the value
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