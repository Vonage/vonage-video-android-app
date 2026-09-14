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
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.util.Properties

@Suppress("NestedBlockDepth")
abstract class GenerateConfigTask : DefaultTask() {

    /**
     * The config JSON itself, declared as a file input so edits to it invalidate the task.
     *
     * Declaring only the *path* (as a `Property<String>`) made the task up-to-date across config
     * edits, so changes silently failed to regenerate until a clean build.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val configSource: RegularFileProperty

    /**
     * `local.properties`, when present. Declared as an optional input because it supplies the
     * placeholder values (for example `BASE_API_URL`) substituted into the generated output.
     */
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val localPropertiesFile: RegularFileProperty

    /** `BASE_API_URL` from the environment (CI), which overrides `local.properties`. */
    @get:Input
    @get:Optional
    abstract val baseApiUrlFromEnv: Property<String>

    @get:Input
    abstract val outputPackage: Property<String>

    @get:Input
    abstract val className: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    /** The generated Gradle properties file, declared so deleting it forces a regeneration. */
    @get:OutputFile
    abstract val gradlePropertiesFile: RegularFileProperty

    @TaskAction
    fun generateConfig() {
        val configFile = configSource.get().asFile

        require(configFile.exists()) { "Config file not found: ${configFile.absolutePath}" }

        val props = loadProps()
        val jsonContent = resolvePlaceholders(configFile.readText(), props)

        val gson = Gson()
        val jsonObject = gson.fromJson(jsonContent, JsonObject::class.java)

        validateTopLevelKeys(jsonObject)

        val packageName = outputPackage.get()
        val className = className.get()

        // Generate the BuildConfig class using KotlinPoet
        val fileSpec = generateBuildConfigClass(packageName, className, jsonObject)
        fileSpec.writeTo(outputDir.get().asFile)

        // Generate Gradle properties file for build configuration
        val gradlePropsContent = generateGradleProperties(jsonObject)
        val gradlePropsFile = gradlePropertiesFile.get().asFile
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
        val localProperties = localPropertiesFile.asFile.orNull
        if (localProperties != null && localProperties.exists()) {
            val loaded = Properties()
            localProperties.inputStream().use { loaded.load(it) }
            loaded.forEach { (key, value) ->
                properties[key.toString()] = value.toString()
            }
        }

        // Override with environment variables (for CI/CD)
        baseApiUrlFromEnv.orNull?.let {
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

    private fun generateBuildConfigClass(
        packageName: String,
        className: String,
        jsonObject: JsonObject,
    ): FileSpec {
        val configObject = TypeSpec.objectBuilder(className)
            .addKdoc("Generated configuration for Vonage Video SDK\nDo not modify this file manually")

        SETTINGS_GROUPS.forEach { group ->
            jsonObject.getAsJsonObject(group.jsonKey)?.let { settings ->
                configObject.addType(
                    buildSettingsObject(group.objectName, "${group.header} Configuration", settings)
                )
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

        SCALAR_KEYS.forEach { scalar ->
            jsonObject.get(scalar.jsonKey)?.let { value ->
                sb.appendLine("# ${scalar.header}")
                sb.appendProp(scalar.propertyName, value)
                sb.appendLine()
            }
        }

        SETTINGS_GROUPS.forEach { group ->
            jsonObject.getAsJsonObject(group.jsonKey)?.let { settings ->
                sb.appendLine("# ${group.header}")
                settings.entrySet().forEach { (key, value) ->
                    sb.appendProp("${group.propertyPrefix}.${key.toSnakeCase()}", value)
                }
                sb.appendLine()
            }
        }

        return sb.toString()
    }

    /**
     * Fail the build when the config contains a top-level key this task does not understand.
     *
     * Both generators work from explicit key lists ([SETTINGS_GROUPS], [SCALAR_KEYS]), so an
     * unrecognised key would otherwise be dropped silently: the build succeeds, no constant is
     * generated, and the new setting does nothing. Erroring here turns that into a build failure
     * that names the offending key.
     */
    private fun validateTopLevelKeys(jsonObject: JsonObject) {
        val known = SETTINGS_GROUPS.map { it.jsonKey } +
            SCALAR_KEYS.map { it.jsonKey } +
            IGNORED_KEYS
        val unknown = jsonObject.keySet() - known.toSet()

        if (unknown.isNotEmpty()) {
            throw IllegalStateException(
                """
                Unrecognised top-level key(s) in the app config: ${unknown.joinToString()}

                Keys not handled by the generator produce no AppConfig constants and no Gradle
                properties, so the setting would silently do nothing.

                To add a settings group, extend SETTINGS_GROUPS in GenerateConfigTask.
                To add a single value, extend SCALAR_KEYS.
                For a documentation-only block, add it to IGNORED_KEYS.
                """.trimIndent()
            )
        }
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

    /**
     * A nested settings object in the config, generated as both an [AppConfig] sub-object and a
     * block of Gradle properties.
     *
     * @param jsonKey        Key of the object in the config JSON.
     * @param objectName     Name of the generated nested Kotlin object.
     * @param propertyPrefix Prefix for the generated Gradle property names.
     * @param header         Comment header, also used as the generated KDoc.
     */
    private data class SettingsGroup(
        val jsonKey: String,
        val objectName: String,
        val propertyPrefix: String,
        val header: String,
    )

    /**
     * A top-level scalar value in the config, generated as a single Gradle property.
     *
     * @param jsonKey      Key of the value in the config JSON.
     * @param propertyName Full name of the generated Gradle property.
     * @param header       Comment header for the generated block.
     */
    private data class ScalarKey(
        val jsonKey: String,
        val propertyName: String,
        val header: String,
    )

    companion object {
        /**
         * Every settings group the generator understands, in output order.
         *
         * This is the single source of truth for both generated outputs. Adding a group to the
         * config without adding it here fails the build — see [validateTopLevelKeys].
         */
        private val SETTINGS_GROUPS = listOf(
            SettingsGroup("videoSettings", "VideoSettings", "vonage.video", "Video Settings"),
            SettingsGroup("audioSettings", "AudioSettings", "vonage.audio", "Audio Settings"),
            SettingsGroup("authSettings", "AuthSettings", "vonage.auth", "Authentication Settings"),
            SettingsGroup(
                "waitingRoomSettings",
                "WaitingRoomSettings",
                "vonage.waitingRoom",
                "Waiting Room Settings",
            ),
            SettingsGroup(
                "meetingRoomSettings",
                "MeetingRoomSettings",
                "vonage.meetingRoom",
                "Meeting Room Settings",
            ),
        )

        /** Top-level scalar values, in output order. */
        private val SCALAR_KEYS = listOf(
            ScalarKey("baseApiUrl", "vonage.baseApiUrl", "Base API URL"),
        )

        /**
         * Top-level keys that are intentionally not generated.
         *
         * `metadata` is descriptive only — it documents the config document itself (name, version,
         * created date, description) and drives no app behavior.
         */
        private val IGNORED_KEYS = setOf("metadata")
    }
}
