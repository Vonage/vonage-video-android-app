package com.vonage.confighelper.service

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import org.jetbrains.plugins.gradle.util.GradleConstants
import java.io.File

class GradleExecutionService(
    private val project: Project
) {

    interface ExecutionCallback {
        fun onStart()
        fun onOutput(message: String)
        fun onSuccess()
        fun onFailure(exitCode: Int)
    }

    @Suppress("TooGenericExceptionCaught")
    fun executeGradleTasks(
        vararg tasks: String,
        callback: ExecutionCallback
    ) {
        val basePath = project.basePath ?: run {
            callback.onFailure(-1)
            return
        }

        val gradlew = getGradleWrapper(basePath)
        if (gradlew == null || !gradlew.exists()) {
            callback.onOutput("ERROR: Gradle wrapper not found")
            callback.onFailure(-1)
            return
        }

        try {
            val commandLine = GeneralCommandLine()
                .withExePath(gradlew.absolutePath)
                .withParameters(*tasks)
                .withWorkDirectory(basePath)

            val processHandler = OSProcessHandler(commandLine)
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun processTerminated(event: ProcessEvent) {
                    if (event.exitCode == 0) {
                        callback.onSuccess()
                        callback.onOutput("Refreshing Gradle project...")
                        refreshGradleProject(basePath)
                        callback.onOutput("Gradle resync triggered")
                    } else {
                        callback.onFailure(event.exitCode)
                    }
                }

                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    val text = event.text?.trim()
                    if (!text.isNullOrEmpty() && text.contains("BUILD")) {
                        callback.onOutput(text)
                    }
                }
            })

            callback.onStart()
            processHandler.startNotify()
            callback.onOutput("Started: ./gradlew ${tasks.joinToString(" ")}")
        } catch (e: Exception) {
            callback.onOutput("ERROR: ${e.message}")
            callback.onFailure(-1)
        }
    }

    private fun getGradleWrapper(basePath: String): File? {
        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        val gradlewName = if (isWindows) "gradlew.bat" else "gradlew"
        return File(basePath, gradlewName)
    }

    private fun refreshGradleProject(basePath: String) {
        ExternalSystemUtil.refreshProject(
            project,
            GradleConstants.SYSTEM_ID,
            basePath,
            false,
            ProgressExecutionMode.IN_BACKGROUND_ASYNC
        )
    }
}
