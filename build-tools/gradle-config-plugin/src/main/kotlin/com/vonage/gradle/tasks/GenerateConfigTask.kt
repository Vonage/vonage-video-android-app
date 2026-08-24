package com.vonage.gradle.tasks

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Properties

@Suppress("NestedBlockDepth")
abstract class GenerateConfigTask : DefaultTask() {

    @get:Input
    abstract val configFile: Property<String>

    @get:Input
    abstract val outputPackage: Property<String>

    @get:Input
    abstract val className: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generateConfig() {
        val configFilePath = resolveConfigFile()
        val configFile = File(configFilePath)

        require(configFile.exists())

        val props = loadProps()
        val jsonContent = resolvePlaceholders(configFile.readText(), props)

        val gson = Gson()
        val jsonObject = gson.fromJson(jsonContent, JsonObject::class.java)

        val packageName = outputPackage.get()
        val className = className.get()

        // Generate the BuildConfig class using KotlinPoet
        val fileSpec = generateBuildConfigClass(packageName, className, jsonObject)
        fileSpec.writeTo(outputDir.get().asFile)

        // Generate Gradle properties file for build configuration
        val gradlePropsContent = generateGradleProperties(jsonObject)
        val gradlePropsFile = project.rootDir.resolve("gradle/generated-config.properties")
        gradlePropsFile.parentFile.mkdirs()
        gradlePropsFile.writeText(gradlePropsContent)

        logger.info("Generated config class: ${outputDir.get().asFile.absolutePath}")
        logger.info("Generated gradle properties: ${gradlePropsFile.absolutePath}")
    }

    /**
     * Load properties from environment variables and local.properties
     */
    private fun loadProps(): Map<String, String> {
        val properties = mutableMapOf<String, String>()

        // Load from local.properties (for local development)
        val localPropertiesFile = File(project.rootDir, "local.properties")
        if (localPropertiesFile.exists()) {
            val localProperties = Properties()
            localPropertiesFile.inputStream().use { localProperties.load(it) }
            localProperties.forEach { (key, value) ->
                properties[key.toString()] = value.toString()
            }
        }

        // Override with environment variables (for CI/CD)
        System.getenv("BASE_API_URL")?.let {
            properties["BASE_API_URL"] = it
        }

        // Validate required secrets
        if (!properties.containsKey("BASE_API_URL")) {
            throw IllegalStateException(
                """
                BASE_API_URL is not configured!
                
                For local development, add to local.properties:
                    BASE_API_URL=https://your-backend-url.com
                
                For CI/CD, set environment variable:
                    export BASE_API_URL=https://your-backend-url.com
                """.trimIndent()
            )
        }

        return properties
    }

    /**
     * Replace ${VARIABLE} placeholders with actual values
     */
    private fun resolvePlaceholders(text: String, properties: Map<String, String>): String {
        var result = text
        properties.forEach { (key, value) ->
            result = result.replace("\${$key}", value)
        }
        return result
    }

    private fun resolveConfigFile(): String {
        val configPath = configFile.get()
        return if (File(configPath).isAbsolute) configPath
        else project.rootDir.resolve(configPath).absolutePath
    }

    private fun generateBuildConfigClass(
        packageName: String,
        className: String,
        jsonObject: JsonObject,
    ): FileSpec {
        val configObject = TypeSpec.objectBuilder(className)
            .addKdoc("Generated configuration for Vonage Video SDK\nDo not modify this file manually")

        listOf(
            Triple("videoSettings", "VideoSettings", "Video Settings Configuration"),
            Triple("audioSettings", "AudioSettings", "Audio Settings Configuration"),
            Triple(
                "waitingRoomSettings",
                "WaitingRoomSettings",
                "Waiting Room Settings Configuration"
            ),
            Triple(
                "meetingRoomSettings",
                "MeetingRoomSettings",
                "Meeting Room Settings Configuration"
            ),
        ).forEach { (jsonKey, objectName, kdoc) ->
            jsonObject.getAsJsonObject(jsonKey)?.let { settings ->
                configObject.addType(buildSettingsObject(objectName, kdoc, settings))
            }
        }

        return FileSpec.builder(packageName, className)
            .addType(configObject.build())
            .build()
    }

    private fun buildSettingsObject(
        name: String,
        kdoc: String,
        jsonObject: JsonObject,
    ): TypeSpec {
        val builder = TypeSpec.objectBuilder(name)
            .addKdoc(kdoc)

        jsonObject.entrySet().forEach { (key, value) ->
            val constantName = key.toCamelCase().uppercase()
            if (value.isJsonPrimitive) {
                val primitive = value.asJsonPrimitive
                val property = when {
                    primitive.isString -> PropertySpec.builder(constantName, String::class)
                        .addModifiers(KModifier.CONST)
                        .initializer("%S", primitive.asString)
                        .build()

                    primitive.isNumber -> {
                        val number = primitive.asNumber
                        if (number.toString().contains('.')) {
                            PropertySpec.builder(constantName, Double::class)
                                .addModifiers(KModifier.CONST)
                                .initializer("%L", number.toDouble())
                                .build()
                        } else {
                            PropertySpec.builder(constantName, Int::class)
                                .addModifiers(KModifier.CONST)
                                .initializer("%L", number.toInt())
                                .build()
                        }
                    }

                    primitive.isBoolean -> PropertySpec.builder(constantName, Boolean::class)
                        .addModifiers(KModifier.CONST)
                        .initializer("%L", primitive.asBoolean)
                        .build()

                    else -> null
                }
                property?.let {
                    builder.addProperty(property)
                }
            }
        }

        return builder.build()
    }

    /**
     * Generate Gradle properties file for build configuration
     */
    private fun generateGradleProperties(jsonObject: JsonObject): String {
        val sb = StringBuilder()
        sb.appendLine("# Generated Gradle properties from JSON config")
        sb.appendLine("# Do not modify this file manually")
        sb.appendLine()

        // Base api URL
        jsonObject.get("baseApiUrl")?.let { value ->
            sb.appendLine("# Video Settings")
            sb.appendProp("vonage.baseApiUrl", value)
            sb.appendLine()
        }

        // Video Settings
        val videoSettings = jsonObject.getAsJsonObject("videoSettings")
        if (videoSettings != null) {
            sb.appendLine("# Video Settings")
            videoSettings.entrySet().forEach { (key, value) ->
                sb.appendProp("vonage.video.${key.toSnakeCase()}", value)
            }
            sb.appendLine()
        }

        // Audio Settings
        val audioSettings = jsonObject.getAsJsonObject("audioSettings")
        if (audioSettings != null) {
            sb.appendLine("# Audio Settings")
            audioSettings.entrySet().forEach { (key, value) ->
                sb.appendProp("vonage.audio.${key.toSnakeCase()}", value)
            }
            sb.appendLine()
        }

        // Waiting Room Settings
        val waitingRoomSettings = jsonObject.getAsJsonObject("waitingRoomSettings")
        if (waitingRoomSettings != null) {
            sb.appendLine("# Waiting Room Settings")
            waitingRoomSettings.entrySet().forEach { (key, value) ->
                sb.appendProp("vonage.waitingRoom.${key.toSnakeCase()}", value)
            }
            sb.appendLine()
        }

        // Meeting Room Settings
        val meetingRoomSettings = jsonObject.getAsJsonObject("meetingRoomSettings")
        if (meetingRoomSettings != null) {
            sb.appendLine("# Meeting Room Settings")
            meetingRoomSettings.entrySet().forEach { (key, value) ->
                sb.appendProp("vonage.meetingRoom.${key.toSnakeCase()}", value)
            }
            sb.appendLine()
        }

        return sb.toString()
    }

    private fun StringBuilder.appendProp(propName: String, value: JsonElement) {
        appendLine("$propName=${value.asString}")
    }

    /**
     * Converts camelCase to UPPER_CASE format for constants
     */
    private fun String.toCamelCase(): String =
        replace(Regex("([a-z])([A-Z])"), "$1_$2")

    /**
     * Convert camelCase to snake_case for property names
     */
    private fun String.toSnakeCase(): String =
        replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
