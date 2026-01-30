package com.vonage.confighelper.ui.renderer

import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Icon
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

@Suppress("MagicNumber")
internal class ColorPreviewTreeCellRenderer : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        val component =
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)

        if (value is DefaultMutableTreeNode) {
            val nodeText = value.userObject?.toString() ?: ""
            extractColorValue(nodeText)?.let { colorString ->
                parseColor(colorString)?.let { color ->
                    icon = ColorIcon(color)
                }
            }
        }

        return component
    }

    private fun extractColorValue(text: String): String? {
        val match = hexPattern.find(text)
        return match?.groupValues?.get(1)
    }

    private fun parseColor(colorString: String): Color? =
        try {
            when {
                colorString.startsWith("#") && colorString.length == 7 -> {
                    // #RRGGBB format
                    Color.decode(colorString)
                }

                colorString.startsWith("#") && colorString.length == 9 -> {
                    // #RRGGBBAA format
                    val r = colorString.substring(1, 3).toInt(16)
                    val g = colorString.substring(3, 5).toInt(16)
                    val b = colorString.substring(5, 7).toInt(16)
                    val a = colorString.substring(7, 9).toInt(16)
                    Color(r, g, b, a)
                }

                else -> null
            }
        } catch (_: Exception) {
            null
        }

    companion object {
        // Match patterns like "key: #RRGGBB" or "key: #RRGGBBAA"
        val hexPattern = ".*:\\s*(#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?)".toRegex()
    }
}

@Suppress("MagicNumber")
internal class ColorIcon(val color: Color) : Icon {
    private val size = 16

    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
        if (g == null) return

        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw the color square
        g2d.color = color
        g2d.fillRoundRect(x, y, size, size, 4, 4)

        // Draw border
        g2d.color = JBColor.DARK_GRAY
        g2d.drawRoundRect(x, y, size, size, 4, 4)
    }

    override fun getIconWidth(): Int = size
    override fun getIconHeight(): Int = size
}
