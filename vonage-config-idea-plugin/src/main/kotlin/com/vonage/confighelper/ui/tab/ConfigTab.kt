package com.vonage.confighelper.ui.tab

import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.vonage.confighelper.model.JsonNode
import com.vonage.confighelper.ui.MainPanelPresenter
import com.vonage.confighelper.ui.UiConfig
import com.vonage.confighelper.ui.renderer.toTreeNode
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.DefaultListModel
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

@Suppress("MagicNumber")
class ConfigTab(
    private val presenter: MainPanelPresenter
) {
    private val listModel = DefaultListModel<String>()
    private val list = JBList(listModel)
    private val jsonTree = Tree()
    private val jsonPanel = JPanel(BorderLayout())

    init {
        setupPresenter()
        setupJsonPanel()
        listModel.addElement("Waiting for config changes...")
    }

    fun createPanel(): JPanel {
        val tabPanel = JPanel(BorderLayout())
        val scrollPane = JBScrollPane(list)
        val splitPane = OnePixelSplitter(true, 0.6f)
        splitPane.firstComponent = jsonPanel
        splitPane.secondComponent = scrollPane

        tabPanel.add(splitPane, BorderLayout.CENTER)
        return tabPanel
    }

    private fun setupPresenter() {
        presenter.onJsonContentUpdated = { jsonNode ->
            updateJsonTree(jsonNode)
        }

        presenter.onLogAdded = { logMessage ->
            listModel.add(0, logMessage)
        }
    }

    private fun setupJsonPanel() {
        jsonTree.font = UiConfig.jsonViewFont
        val rootNode = DefaultMutableTreeNode("No config file found")
        jsonTree.model = DefaultTreeModel(rootNode)
        val jsonScrollPane = JBScrollPane(jsonTree)
        jsonScrollPane.preferredSize = Dimension(0, 200)
        jsonPanel.add(jsonScrollPane, BorderLayout.CENTER)
        jsonPanel.border = UiConfig.defaultBorder
    }

    private fun updateJsonTree(jsonNode: JsonNode) {
        jsonTree.model = DefaultTreeModel(jsonNode.toTreeNode())
        // Expand first level by default
        for (i in 0 until jsonTree.rowCount) {
            jsonTree.expandRow(i)
        }
    }
}
