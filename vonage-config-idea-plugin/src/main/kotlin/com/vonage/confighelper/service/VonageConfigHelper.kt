package com.vonage.confighelper.service

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "VonageConfigHelper",
    storages = [Storage("VonageConfigHelper.xml")]
)
class VonageConfigHelper : PersistentStateComponent<VonageConfigHelper.State> {

    private var state = State()

    data class State(
        var configFilePath: String = "config/app-config.json",
        var themeFilePath: String = "config/theme.json",
        var autoRunGradleTasks: Boolean = true
    )

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(project: Project): VonageConfigHelper =
            project.service<VonageConfigHelper>()
    }
}