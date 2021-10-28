package allSamples

import main
import unitTests.NoExitSecurityManager
import kotlin.test.*

internal class CreateAllSamples {
    @Test
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
}