class FloatValuedOptionParser(rawName: String, private val defaultValue: Float, private val minValue: Float, private val maxValue: Float) {
    private val name: String
    private val nameFromUpperCase: String

    init {
        name = rawName.lowercase()
        nameFromUpperCase = name[0].uppercase() + name.drop(1)
    }

    fun parse(argument: String?): Float {
        if (argument == null) {
            return defaultValue
        }
        val value = argument.toFloatOrNull()
        return when {
            value == null || value.isNaN() -> {
                println("Invalid $name ($argument). Falling back to default ($defaultValue).")
                defaultValue
            }
            value.isInfinite() || value > maxValue -> {
                println("$nameFromUpperCase ($argument) is too large. Falling back to maximum possible ($maxValue).")
                maxValue
            }
            value < minValue -> {
                println("$nameFromUpperCase ($argument) is too small. Falling back to minimum possible ($minValue).")
                minValue
            }
            else -> value
        }
    }
}