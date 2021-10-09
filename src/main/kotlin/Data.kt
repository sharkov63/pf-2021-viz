import java.io.File
import kotlin.text.*

data class DataElement(val label: String, val value: Double)

typealias Data = List<DataElement>


fun parseDataElementOrNull(line: String): DataElement? {
    val tokens = line
        .trimEnd()
        .reversed()
        .split(' ', limit = 2)
        .reversed()
        .map { it.reversed().trimEnd() }
    if (tokens.size == 1) return null
    val key = tokens.first()
    val value = tokens.last().toDoubleOrNull() ?: return null
    if (value.isInfinite() || value.isNaN() || value < 0)
        return null
    return DataElement(key, value)
}

fun parseDataFromLines(lines: List<String>): Data {
    return lines.mapNotNull { parseDataElementOrNull(it) }
}

fun readDataFromFile(file: File): Data {
    assert(file.exists())
    assert(file.canRead())
    val lines = file.readLines()
    return parseDataFromLines(lines)
}

fun readDataFromStdin(): Data {
    var line = readLine()
    val lines: MutableList<String> = mutableListOf()
    while (line != null) {
        lines.add(line)
        line = readLine()
    }
    return parseDataFromLines(lines)
}

fun readData(file: File?): Data {
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