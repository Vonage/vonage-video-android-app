package com.vonage.confighelper.model

sealed class JsonNode {
    abstract val key: String

    data class ObjectNode(
        override val key: String,
        val children: List<JsonNode>
    ) : JsonNode()

    data class ArrayNode(
        override val key: String,
        val children: List<JsonNode>
    ) : JsonNode()

    data class ValueNode(
        override val key: String,
        val value: String
    ) : JsonNode()

    data class ErrorNode(
        override val key: String,
        val error: String
    ) : JsonNode()
}
