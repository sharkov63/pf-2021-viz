/**
 * Color option.
 * Determines the color of some diagrams.
 */

const val MIN_COLOR_OPTION = 0x000000
const val MAX_COLOR_OPTION = 0xffffff
const val DEFAULT_COLOR_OPTION = 0x4f86c6

private fun correctColorCode(colorCode: Int?) = colorCode in MIN_COLOR_OPTION..MAX_COLOR_OPTION

val wordColorDescriptions = mapOf(
    "default" to DEFAULT_COLOR_OPTION,
    "red" to 0xba181b,
    "orange" to 0xe85d04,
    "brown" to 0x582f0e,
    "yellow" to 0xfaa307,
    "green" to 0x008000,
    "cyan" to 0x48cae4,
    "blue" to 0x4f86c6,
    "purple" to 0x5a189a,
    "violet" to 0x5a189a,
    "pink" to 0xff4d6d,
    "grey" to 0x6c757d,
    "gray" to 0x6c757d,
    "black" to 0x001219,
)

/**
 * Given a description of a color (either a word, or a HEX code),
 * returns a color code in RGB as an [Int].
 */
fun parseColorOption(argument: String?): Int {
    val argumentAsHexInt = if (argument?.length == 6) {
        argument.toIntOrNull(16)
    } else {
        null
    }
    val argumentAsHexIntWithSharp = if (argument?.length == 7 && argument.first() == '#') {
        argument.drop(1).toIntOrNull(16)
    } else {
        null
    }
    return when {
        argument == null -> DEFAULT_COLOR_OPTION
        wordColorDescriptions.containsKey(argument.lowercase()) -> wordColorDescriptions.getValue(argument.lowercase())
        argumentAsHexInt != null && correctColorCode(argumentAsHexInt) -> argumentAsHexInt
        argumentAsHexIntWithSharp != null && correctColorCode(argumentAsHexIntWithSharp) -> argumentAsHexIntWithSharp
        else -> {
            println("Invalid color code ($argument). Falling back to default (#${DEFAULT_COLOR_OPTION.toString(16)}).")
            DEFAULT_COLOR_OPTION
        }
    }
}