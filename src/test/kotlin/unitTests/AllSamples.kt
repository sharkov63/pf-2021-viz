package unitTests

import main
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

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
        "area",
    )

    private fun getAnswerFileName(inputFile: File, diagramCode: String): String {
        return "$samplesFolderPath/${inputFile.nameWithoutExtension}-$diagramCode.a.png"
    }

    private fun getOutputFileName(inputFile: File, diagramCode: String): String {
        return "$samplesFolderPath/${inputFile.nameWithoutExtension}-$diagramCode.png"
    }

    //@Test
    fun createAllAnswerImages() {
        System.setSecurityManager(NoExitSecurityManager())
        txtFiles.forEach { inputFile ->
            diagramCodes.forEach { diagramCode ->
                val answerFileName = getAnswerFileName(inputFile, diagramCode)
                try {
                    main(arrayOf("-i", inputFile.path, "-d", diagramCode, "-o", answerFileName, "--no-window"))
                } catch (ex: Exception) {
                    assertEquals("Exit block", ex.message)
                }
            }
        }
    }

    private fun checkSample(inputFile: File, diagramCode: String) {
        val answerFileName = getAnswerFileName(inputFile, diagramCode)
        val outputFileName = getOutputFileName(inputFile, diagramCode)
        val answerFile = File(answerFileName)
        val outputFile = File(outputFileName)
        try {
            main(arrayOf("-i", inputFile.path, "-d", diagramCode, "-o", outputFileName, "--no-window"))
            assertEquals(answerFile.readText(), outputFile.readText())
        } catch (ex: Exception) {
            assertEquals("Exit block", ex.message)
        } finally {
            if (outputFile.exists())
                outputFile.delete()
        }
        println("Checked $inputFile $diagramCode")
    }

    @Test
    fun checkAllSamples() {
        val noExitSecurityManager = NoExitSecurityManager()
        System.setSecurityManager(noExitSecurityManager)
        txtFiles.forEach { inputFile ->
            diagramCodes.forEach { diagramCode ->
                checkSample(inputFile, diagramCode)
            }
        }
    }
}