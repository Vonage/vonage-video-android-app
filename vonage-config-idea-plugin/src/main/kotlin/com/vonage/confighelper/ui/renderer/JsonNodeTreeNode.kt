package com.vonage.confighelper.ui.renderer

import com.vonage.confighelper.model.JsonNode
import javax.swing.tree.DefaultMutableTreeNode

internal fun JsonNode.toTreeNode(): DefaultMutableTreeNode =
    when (this) {
        is JsonNode.ObjectNode -> {
            val node = DefaultMutableTreeNode(key)
            children.forEach { child ->
                node.add(child.toTreeNode())
            }
            node
        }

        is JsonNode.ArrayNode -> {
            val node = DefaultMutableTreeNode(key)
            children.forEach { child ->
                node.add(child.toTreeNode())
            }
            node
        }

        is JsonNode.ValueNode -> DefaultMutableTreeNode("${key}: $value")
        is JsonNode.ErrorNode -> DefaultMutableTreeNode("${key}: $error")
    }
