package com.vonage.confighelper.service

import com.vonage.confighelper.model.JsonNode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File

@Suppress("TooGenericExceptionCaught")
class JsonParserService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseFile(file: File): JsonNode =
        if (file.exists() && file.isFile) {
            try {
                val content = file.readText()
                parseJson(content, file.name)
            } catch (e: Exception) {
                JsonNode.ErrorNode("Error", "Error reading file: ${e.message}")
            }
        } else {
            JsonNode.ErrorNode("Error", "File not found at: ${file.path}")
        }

    fun parseJson(jsonString: String, rootName: String): JsonNode =
        try {
            val element = json.parseToJsonElement(jsonString)
            mapJsonElementToNode(rootName, element)
        } catch (e: Exception) {
            JsonNode.ErrorNode(rootName, "Parse error: ${e.message}")
        }

    private fun mapJsonElementToNode(key: String, element: JsonElement): JsonNode =
        when (element) {
            is JsonObject -> {
                val children = element.entries.map { (childKey, childValue) ->
                    mapJsonElementToNode(childKey, childValue)
                }
                JsonNode.ObjectNode(key, children)
            }

            is JsonArray -> {
                val children = element.mapIndexed { index, childValue ->
                    mapJsonElementToNode("[$index]", childValue)
                }
                JsonNode.ArrayNode(key, children)
            }

            is JsonPrimitive -> {
                val value = when {
                    element.isString -> element.content
                    else -> element.toString()
                }
                JsonNode.ValueNode(key, value)
            }
        }
}
