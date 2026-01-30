package com.vonage.confighelper.ui.tab

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.vonage.confighelper.service.JsonParserService
import com.vonage.confighelper.ui.UiConfig
import com.vonage.confighelper.ui.renderer.ColorPreviewTreeCellRenderer
import com.vonage.confighelper.ui.renderer.toTreeNode
import java.awt.BorderLayout
import java.io.File
import javax.swing.JPanel
import javax.swing.tree.DefaultTreeModel

class ThemeTab(
    private val project: Project
) {
    private val themeJsonTree = Tree()
    private val json = JsonParserService()

    fun createPanel(): JPanel {
        val tabPanel = JPanel(BorderLayout())
        themeJsonTree.font = UiConfig.jsonViewFont
        themeJsonTree.cellRenderer = ColorPreviewTreeCellRenderer()
        val themeJsonPanel = JPanel(BorderLayout())

        val themeScrollPane = JBScrollPane(themeJsonTree)
        themeJsonPanel.add(themeScrollPane, BorderLayout.CENTER)
        themeJsonPanel.border = UiConfig.defaultBorder

        loadThemeJson()

        tabPanel.add(themeJsonPanel, BorderLayout.CENTER)
        return tabPanel
    }

    private fun loadThemeJson() {
        val basePath = project.basePath ?: return
        val themeFile = File(basePath, "config/theme.json")

        val rootNode = json.parseFile(themeFile).toTreeNode()
        themeJsonTree.model = DefaultTreeModel(rootNode)

        // Expand first level by default
        for (i in 0 until themeJsonTree.rowCount) {
            themeJsonTree.expandRow(i)
        }
    }
}
