package com.vonage.gradle.tasks

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

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

        val jsonContent = configFile.readText()
        val gson = Gson()
        val jsonObject = gson.fromJson(jsonContent, JsonObject::class.java)

        val packageName = outputPackage.get()
        val className = className.get()

        // Generate the BuildConfig class
        val buildConfigContent = generateBuildConfigClass(
            packageName,
            className,
            jsonObject,
        )

        // Write Kotlin config class to output directory
        val outputFile = outputDir.get().asFile.resolve("${packageName.replace(".", "/")}/${className}.kt")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(buildConfigContent)

        // Generate Gradle properties file for build configuration
        val gradlePropsContent = generateGradleProperties(jsonObject)
        val gradlePropsFile = project.rootDir.resolve("gradle/generated-config.properties")
        gradlePropsFile.parentFile.mkdirs()
        gradlePropsFile.writeText(gradlePropsContent)

        logger.info("Generated config class: ${outputFile.absolutePath}")
        logger.info("Generated gradle properties: ${gradlePropsFile.absolutePath}")
    }

    private fun resolveConfigFile(): String {
        val configPath = configFile.get()

        // Check if it's a system property or command line argument
        val systemProperty = System.getProperty("config.file")
        if (systemProperty != null) {
            return systemProperty
        }

        // Check if it's an absolute path
        if (File(configPath).isAbsolute) {
            return configPath
        }

        // Relative to project root
        return project.rootDir.resolve(configPath).absolutePath
    }

    private fun generateBuildConfigClass(
        packageName: String,
        className: String,
        jsonObject: JsonObject,
    ): String {
        val sb = StringBuilder()

        sb.appendLine("package $packageName")
        sb.appendLine()
        sb.appendLine("/**")
        sb.appendLine(" * Generated configuration for Vonage Video SDK")
        sb.appendLine(" * Do not modify this file manually.")
        sb.appendLine(" */")
        sb.appendLine("object $className {")
        sb.appendLine()

        // Generate video settings
        generateVideoSettings(sb, jsonObject)

        // Generate audio settings
        generateAudioSettings(sb, jsonObject)

        // Generate waiting room settings
        generateWaitingRoomSettings(sb, jsonObject)

        // Generate meeting room settings
        generateMeetingRoomSettings(sb, jsonObject)

        sb.appendLine("}")

        return sb.toString()
    }

    private fun generateVideoSettings(sb: StringBuilder, jsonObject: JsonObject) {
        val videoSettings = jsonObject.getAsJsonObject("videoSettings")
        if (videoSettings != null) {
            sb.appendLine("    /**")
            sb.appendLine("     * Video Settings Configuration")
            sb.appendLine("     */")
            sb.appendLine("    object VideoSettings {")
            appendObject(videoSettings, sb)
            sb.appendLine("    }")
            sb.appendLine()
        }
    }

    private fun generateAudioSettings(sb: StringBuilder, jsonObject: JsonObject) {
        val audioSettings = jsonObject.getAsJsonObject("audioSettings")
        if (audioSettings != null) {
            sb.appendLine("    /**")
            sb.appendLine("     * Audio Settings Configuration")
            sb.appendLine("     */")
            sb.appendLine("    object AudioSettings {")
            appendObject(audioSettings, sb)
            sb.appendLine("    }")
            sb.appendLine()
        }
    }

    private fun generateWaitingRoomSettings(sb: StringBuilder, jsonObject: JsonObject) {
        val waitingRoomSettings = jsonObject.getAsJsonObject("waitingRoomSettings")
        if (waitingRoomSettings != null) {
            sb.appendLine("    /**")
            sb.appendLine("     * Waiting Room Settings Configuration")
            sb.appendLine("     */")
            sb.appendLine("    object WaitingRoomSettings {")
            appendObject(waitingRoomSettings, sb)
            sb.appendLine("    }")
            sb.appendLine()
        }
    }

    private fun generateMeetingRoomSettings(sb: StringBuilder, jsonObject: JsonObject) {
        val meetingRoomSettings = jsonObject.getAsJsonObject("meetingRoomSettings")
        if (meetingRoomSettings != null) {
            sb.appendLine("    /**")
            sb.appendLine("     * Meeting Room Settings Configuration")
            sb.appendLine("     */")
            sb.appendLine("    object MeetingRoomSettings {")
            appendObject(meetingRoomSettings, sb)
            sb.appendLine("    }")
            sb.appendLine()
        }
    }

    private fun appendObject(jsonObject: JsonObject, sb: StringBuilder) {
        jsonObject.entrySet().forEach { (key, value) ->
            val constantName = key.toCamelCase().uppercase()
            when {
                value.isJsonPrimitive -> {
                    val primitive = value.asJsonPrimitive
                    when {
                        primitive.isString -> {
                            sb.appendLine("        const val $constantName: String = \"${primitive.asString}\"")
                        }

                        primitive.isNumber -> {
                            val number = primitive.asNumber
                            if (number.toString().contains('.')) {
                                sb.appendLine("        const val $constantName: Double = ${number.toDouble()}")
                            } else {
                                sb.appendLine("        const val $constantName: Int = ${number.toInt()}")
                            }
                        }

                        primitive.isBoolean -> {
                            sb.appendLine("        const val $constantName: Boolean = ${primitive.asBoolean}")
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate Gradle properties file for build configuration
     */
    private fun generateGradleProperties(jsonObject: JsonObject): String {
        val sb = StringBuilder()
        sb.appendLine("# Generated Gradle properties from JSON config")
        sb.appendLine("# Do not modify this file manually")
        sb.appendLine()

        // Video Settings
        val videoSettings = jsonObject.getAsJsonObject("videoSettings")
        if (videoSettings != null) {
            sb.appendLine("# Video Settings")
            videoSettings.entrySet().forEach { (key, value) ->
                val propName = "vonage.video.${key.toSnakeCase()}"
                sb.appendLine("$propName=${value.asString}")
            }
            sb.appendLine()
        }

        // Audio Settings
        val audioSettings = jsonObject.getAsJsonObject("audioSettings")
        if (audioSettings != null) {
            sb.appendLine("# Audio Settings")
            audioSettings.entrySet().forEach { (key, value) ->
                val propName = "vonage.audio.${key.toSnakeCase()}"
                sb.appendLine("$propName=${value.asString}")
            }
            sb.appendLine()
        }

        // Waiting Room Settings
        val waitingRoomSettings = jsonObject.getAsJsonObject("waitingRoomSettings")
        if (waitingRoomSettings != null) {
            sb.appendLine("# Waiting Room Settings")
            waitingRoomSettings.entrySet().forEach { (key, value) ->
                val propName = "vonage.waitingRoom.${key.toSnakeCase()}"
                sb.appendLine("$propName=${value.asString}")
            }
            sb.appendLine()
        }

        // Meeting Room Settings
        val meetingRoomSettings = jsonObject.getAsJsonObject("meetingRoomSettings")
        if (meetingRoomSettings != null) {
            sb.appendLine("# Meeting Room Settings")
            meetingRoomSettings.entrySet().forEach { (key, value) ->
                val propName = "vonage.meetingRoom.${key.toSnakeCase()}"
                sb.appendLine("$propName=${value.asString}")
            }
            sb.appendLine()
        }

        return sb.toString()
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