import kotlin.test.*
import java.io.File

internal class AllSamples {
    val samplesFolderPath = "samples"
    val samplesFolder = File(samplesFolderPath)
    val txtFiles = samplesFolder
        .walk()
        .toList()
        .filter { it.extension == "txt" }
    val diagramCodes = listOf(
        "bar",
        "line",
        "pie",
    )

    private fun getAnswerFileName(inputFile: File, diagramCode: String): String {
        return "$samplesFolderPath/${inputFile.nameWithoutExtension}-$diagramCode.a.png"
    }

    @Test
    fun createAllAnswerImages() {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        txtFiles.forEach { inputFile ->
            diagramCodes.forEach { diagramCode ->
                val answerFileName = getAnswerFileName(inputFile, diagramCode)
                try {
                    main(arrayOf("-i", inputFile.path, "-d", diagramCode, "-o", answerFileName))
                } catch (ex: Exception) {

                }
            }
        }
    }
}