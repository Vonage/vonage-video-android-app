package com.vonage.confighelper.service

import com.vonage.confighelper.model.JsonNode
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JsonParserServiceTest {

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    private val service = JsonParserService()

    @Test
    fun `parseJson should parse simple JSON object`() {
        val json = """{"name": "John", "age": "30"}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals("root", result.key)
        assertEquals(2, result.children.size)

        val nameNode = result.children[0] as JsonNode.ValueNode
        assertEquals("name", nameNode.key)
        assertEquals("John", nameNode.value)

        val ageNode = result.children[1] as JsonNode.ValueNode
        assertEquals("age", ageNode.key)
        assertEquals("30", ageNode.value)
    }

    @Test
    fun `parseJson should parse nested JSON object`() {
        val json = """{"user": {"name": "John", "age": "30"}}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        val userNode = result.children[0] as JsonNode.ObjectNode
        assertEquals("user", userNode.key)
        assertEquals(2, userNode.children.size)
    }

    @Test
    fun `parseJson should parse JSON array`() {
        val json = """["item1", "item2", "item3"]"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ArrayNode>(result)
        assertEquals("root", result.key)
        assertEquals(3, result.children.size)

        val firstItem = result.children[0] as JsonNode.ValueNode
        assertEquals("[0]", firstItem.key)
        assertEquals("item1", firstItem.value)
    }

    @Test
    fun `parseJson should parse JSON with mixed types`() {
        val json = """{"name": "John", "age": 30, "active": true, "score": 95.5}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals(4, result.children.size)
    }

    @Test
    fun `parseJson should handle empty JSON object`() {
        val json = """{}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals("root", result.key)
        assertTrue(result.children.isEmpty())
    }

    @Test
    fun `parseJson should handle empty JSON array`() {
        val json = """[]"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ArrayNode>(result)
        assertEquals("root", result.key)
        assertTrue(result.children.isEmpty())
    }

    @Test
    fun `parseJson should return ErrorNode for invalid JSON`() {
        val json = """{invalid json}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ErrorNode>(result)
        assertEquals("root", result.key)
        assertTrue(result.error.contains("Parse error"))
    }

    @Test
    fun `parseJson should handle complex nested structure`() {
        val json = """
            {
                "users": [
                    {"name": "John", "age": "30"},
                    {"name": "Jane", "age": "25"}
                ],
                "settings": {
                    "theme": "dark",
                    "notifications": true
                }
            }
        """.trimIndent()

        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals(2, result.children.size)

        val usersNode = result.children[0] as JsonNode.ArrayNode
        assertEquals("users", usersNode.key)
        assertEquals(2, usersNode.children.size)

        val settingsNode = result.children[1] as JsonNode.ObjectNode
        assertEquals("settings", settingsNode.key)
    }

    @Test
    fun `parseJsonFile should parse existing file`() {
        val testFile = File(tempFolder.root, "test.json")
        testFile.writeText("""{"name": "Test", "value": "123"}""")

        val result = service.parseFile(testFile)

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals("test.json", result.key)
        assertEquals(2, result.children.size)
    }

    @Test
    fun `parseJsonFile should return ErrorNode for non-existent file`() {
        val nonExistentFile = File("/path/to/nonexistent/file.json")
        val result = service.parseFile(nonExistentFile)

        assertIs<JsonNode.ErrorNode>(result)
        assertEquals("Error", result.key)
        assertTrue(result.error.contains("File not found"))
    }

    @Test
    fun `parseJsonFile should return ErrorNode for invalid JSON`() {
        val testFile = File(tempFolder.root, "invalid.json")
        testFile.writeText("""not valid json""")

        val result = service.parseFile(testFile)

        assertIs<JsonNode.ErrorNode>(result)
        assertTrue(result.error.contains("Parse error"))
    }

    @Test
    fun `parseJson should handle null values`() {
        val json = """{"name": "John", "address": null}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals(2, result.children.size)
    }

    @Test
    fun `parseJson should handle special characters in strings`() {
        val json = """{"message": "Hello \"World\"", "path": "C:\\Users\\Test"}"""
        val result = service.parseJson(json, "root")

        assertIs<JsonNode.ObjectNode>(result)
        assertEquals(2, result.children.size)
    }
}
