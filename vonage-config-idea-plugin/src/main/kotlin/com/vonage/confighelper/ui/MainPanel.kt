package com.vonage.confighelper.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import com.vonage.confighelper.VonageConfigurable
import com.vonage.confighelper.ui.tab.ConfigTab
import com.vonage.confighelper.ui.tab.ThemeTab
import java.awt.BorderLayout
import javax.swing.JPanel

class MainPanel(
    private val project: Project,
) {
    private val panel = JPanel(BorderLayout())
    private val presenter = MainPanelPresenter(project)

    val content: JPanel
        get() = panel

    init {
        val toolbar = createToolbar()
        val tabbedPane = JBTabbedPane()

        val configTab = ConfigTab(presenter)
        tabbedPane.addTab("Config", configTab.createPanel())

        val themeTab = ThemeTab(project)
        tabbedPane.addTab("Theme", themeTab.createPanel())

        panel.add(toolbar.component, BorderLayout.NORTH)
        panel.add(tabbedPane, BorderLayout.CENTER)

        presenter.loadConfigFile()
    }

    private fun createToolbar(): ActionToolbar {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(object : AnAction("Refresh", "Reload config file", AllIcons.Actions.Refresh) {
            override fun actionPerformed(e: AnActionEvent) {
                presenter.refresh()
            }
        })
        actionGroup.add(object : AnAction("Settings", "Plugin settings", AllIcons.General.Settings) {
            override fun actionPerformed(e: AnActionEvent) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, VonageConfigurable::class.java)
            }
        })
        actionGroup.add(object : AnAction("GitHub Repository", "Open GitHub repository", AllIcons.Vcs.Vendors.Github) {
            override fun actionPerformed(e: AnActionEvent) {
                BrowserUtil.browse("https://github.com/Vonage/vonage-video-android-app")
            }
        })
        val toolbar = ActionManager.getInstance()
            .createActionToolbar("MainPanelToolbar", actionGroup, true)
        toolbar.targetComponent = panel
        return toolbar
    }
}

class MainPanelToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainPanel = MainPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel.content, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
