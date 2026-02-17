package com.vonage.confighelper.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.vonage.confighelper.model.JsonNode
import com.vonage.confighelper.model.LogEntry
import com.vonage.confighelper.service.FileChangeListener
import com.vonage.confighelper.service.GradleExecutionService
import com.vonage.confighelper.service.JsonParserService
import com.vonage.confighelper.service.VonageConfigHelper
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class MainPanelPresenter(
    private val project: Project,
) {

    private val jsonParser = JsonParserService()
    private val gradleExecutionService = GradleExecutionService(project)
    private val dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

    // Callbacks to update the view
    var onLogAdded: ((String) -> Unit)? = null
    var onJsonContentUpdated: ((JsonNode) -> Unit)? = null

    private val logs = mutableListOf<LogEntry>()
    private val maxLogEntries = UiConfig.maxLogEntries

    init {
        setupFileListener()
    }

    fun loadConfigFile() {
        val basePath = project.basePath ?: return
        val settings = VonageConfigHelper.getInstance(project)
        val configPath = settings.state.configFilePath
        val appConfigFile = File(basePath, configPath)
        val jsonNode = jsonParser.parseFile(appConfigFile)
        onJsonContentUpdated?.invoke(jsonNode)
    }

    fun refresh() {
        loadConfigFile()
        val timestamp = dateFormat.format(LocalDate.now())
        addLog("[$timestamp] Manually refreshed config file")
        handleConfigChange(timestamp)
    }

    private fun setupFileListener() {
        val fileChangeListener = project.service<FileChangeListener>()
        fileChangeListener.addListener { file, changeType ->
            if (file.name == "app-config.json" && changeType == FileChangeListener.ChangeType.MODIFIED) {
                val timestamp = dateFormat.format(LocalDate.now())
                loadConfigFile()
                val settings = VonageConfigHelper.getInstance(project)
                if (settings.state.autoRunGradleTasks) {
                    handleConfigChange(timestamp)
                }
            }
        }
    }

    private fun handleConfigChange(timestamp: String) {
        addLog("[$timestamp] Detected app config change, running Gradle tasks...")

        gradleExecutionService.executeGradleTasks(
            "clean", "generateVonageConfig",
            callback = object : GradleExecutionService.ExecutionCallback {
                override fun onStart() {
                    // Already logged above
                }

                override fun onOutput(message: String) {
                    val ts = dateFormat.format(LocalDate.now())
                    addLog("[$ts] $message")
                }

                override fun onSuccess() {
                    val ts = dateFormat.format(LocalDate.now())
                    addLog("[$ts] Gradle tasks completed successfully")
                }

                override fun onFailure(exitCode: Int) {
                    val ts = dateFormat.format(LocalDate.now())
                    if (exitCode >= 0) {
                        addLog("[$ts] Gradle tasks failed with exit code $exitCode")
                    }
                }
            }
        )
    }

    private fun addLog(message: String) {
        val entry = LogEntry(
            timestamp = Date(),
            message = message,
            level = when {
                message.contains("ERROR") -> LogEntry.LogLevel.ERROR
                message.contains("successfully")
                        || message.contains("completed") -> LogEntry.LogLevel.SUCCESS

                else -> LogEntry.LogLevel.INFO
            }
        )
        logs.add(0, entry)
        if (logs.size > maxLogEntries) {
            logs.removeAt(logs.lastIndex)
        }
        onLogAdded?.invoke(entry.message)
    }
}
