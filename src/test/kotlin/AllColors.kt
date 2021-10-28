import java.io.File
import kotlin.test.*

internal class AllColors {
    @Test
    fun createAllColorImages() {
        val inputFile = File("samples/planet mass.txt")
        val diagramCodes = listOf("bar", "area")
        val colors = listOf(
            "red",
            "orange",
            "brown",
            "yellow",
            "green",
            "cyan",
            "blue",
            "purple",
            "pink",
            "gray",
            "black",
        )
        diagramCodes.forEach { diagramCode ->
            colors.forEach { colorWord ->
                val answerFileName = "samples/colors/${inputFile.nameWithoutExtension}-$diagramCode-$colorWord-color.png"
                try {
                    main(arrayOf("-i", inputFile.path, "-d", diagramCode, "-c", colorWord, "-o", answerFileName, "--no-window"))
                } catch (ex: Exception) {
                    assertEquals("Exit block", ex.message)
                }
            }
        }
    }
}