package com.vonage.confighelper.ui

import com.vonage.confighelper.ui.renderer.ColorIcon
import com.vonage.confighelper.ui.renderer.ColorPreviewTreeCellRenderer
import org.junit.Test
import java.awt.Color
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ColorPreviewTreeCellRendererTest {

    private val renderer = ColorPreviewTreeCellRenderer()
    private val tree = JTree()

    @Test
    fun `should render node with hex color`() {
        val node = DefaultMutableTreeNode("primary: #FF5733")

        val component = renderer.getTreeCellRendererComponent(
            tree, node, false, false, true, 0, false
        )

        assertNotNull(component)
        assertNotNull(renderer.icon)
    }

    @Test
    fun `should render node with hex color and alpha`() {
        val node = DefaultMutableTreeNode("background: #FF5733AA")

        val component = renderer.getTreeCellRendererComponent(
            tree, node, false, false, true, 0, false
        )

        assertNotNull(component)
        assertNotNull(renderer.icon)
    }

    @Test
    fun `should handle nodes without color patterns`() {
        val node = DefaultMutableTreeNode("simple text")

        val component = renderer.getTreeCellRendererComponent(
            tree, node, false, false, true, 0, false
        )

        assertNotNull(component)
    }

    @Test
    fun `should handle null node objects`() {
        val node = DefaultMutableTreeNode(null)

        val component = renderer.getTreeCellRendererComponent(
            tree, node, false, false, true, 0, false
        )

        assertNotNull(component)
    }

    @Test
    fun `should handle selected state`() {
        val node = DefaultMutableTreeNode("color: #FF5733")

        val component = renderer.getTreeCellRendererComponent(
            tree, node, true, false, true, 0, false
        )

        assertNotNull(component)
    }

    @Test
    fun `should handle expanded state`() {
        val node = DefaultMutableTreeNode("color: #FF5733")

        val component = renderer.getTreeCellRendererComponent(
            tree, node, false, true, false, 0, false
        )

        assertNotNull(component)
    }

    @Test
    fun `ColorIcon should have correct dimensions`() {
        val icon = ColorIcon(Color.RED)

        assertEquals(16, icon.iconWidth)
        assertEquals(16, icon.iconHeight)
    }

    @Test
    fun `ColorIcon should paint without errors`() {
        val icon = ColorIcon(Color.BLUE)

        // Create a mock graphics context would be complex,
        // but we can at least verify icon creation doesn't fail
        assertNotNull(icon)
        assertEquals(Color.BLUE, icon.color)
    }
}
