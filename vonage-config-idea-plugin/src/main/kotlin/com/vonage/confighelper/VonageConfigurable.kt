package com.vonage.confighelper

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.vonage.confighelper.service.VonageConfigHelper
import javax.swing.JComponent
import javax.swing.JPanel

class VonageConfigurable(
    private val project: Project,
) : Configurable {

    private var configFilePathField: JBTextField? = null
    private var themeFilePathField: JBTextField? = null
    private var autoRunGradleTasksCheckBox: JBCheckBox? = null
    private var mainPanel: JPanel? = null

    override fun getDisplayName(): String = "Vonage Reference App Config"

    override fun createComponent(): JComponent {
        val settings = VonageConfigHelper.getInstance(project)

        configFilePathField = JBTextField(settings.state.configFilePath)
        themeFilePathField = JBTextField(settings.state.themeFilePath)
        autoRunGradleTasksCheckBox = JBCheckBox(
            "Automatically run Gradle tasks when config file changes",
            settings.state.autoRunGradleTasks,
        )

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(
                JBLabel("Config file path (relative to project root):"),
                configFilePathField!!,
                1,
                false,
            )
            .addLabeledComponent(
                JBLabel("Theme file path (relative to project root):"),
                themeFilePathField!!,
                1,
                false,
            )
            .addComponent(autoRunGradleTasksCheckBox!!, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        return mainPanel!!
    }

    override fun isModified(): Boolean {
        val settings = VonageConfigHelper.getInstance(project)
        return configFilePathField?.text != settings.state.configFilePath ||
                themeFilePathField?.text != settings.state.themeFilePath ||
                autoRunGradleTasksCheckBox?.isSelected != settings.state.autoRunGradleTasks
    }

    override fun apply() {
        val settings = VonageConfigHelper.getInstance(project)
        settings.state.configFilePath = configFilePathField?.text ?: "config/app-config.json"
        settings.state.themeFilePath = themeFilePathField?.text ?: "config/theme.json"
        settings.state.autoRunGradleTasks = autoRunGradleTasksCheckBox?.isSelected ?: true
    }

    override fun reset() {
        val settings = VonageConfigHelper.getInstance(project)
        configFilePathField?.text = settings.state.configFilePath
        themeFilePathField?.text = settings.state.themeFilePath
        autoRunGradleTasksCheckBox?.isSelected = settings.state.autoRunGradleTasks
    }
}
