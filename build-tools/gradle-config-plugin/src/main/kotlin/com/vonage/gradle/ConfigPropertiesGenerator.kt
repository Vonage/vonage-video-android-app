package com.vonage.gradle

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.File
import java.util.Properties

/**
 * Parses `app-config.json`, validates its top-level keys, and writes
 * `gradle/generated-config.properties`.
 *
 * Shared by [JsonConfigPlugin] and [com.vonage.gradle.tasks.GenerateConfigTask] so both callers
 * produce byte-identical output from the same logic:
 *
 * - [JsonConfigPlugin] calls this **eagerly**, during the configuration phase, right after the
 *   plugin is applied — before the rest of the build script (in particular `android { ... }`,
 *   which reads the generated properties file to pick product flavors and `BuildConfig` fields)
 *   has a chance to run. This is what makes config edits actually take effect in the *same*
 *   Gradle invocation that regenerates them.
 * - [com.vonage.gradle.tasks.GenerateConfigTask] calls this from its `@TaskAction` so the
 *   documented standalone `./gradlew generateVonageConfig` invocation, Gradle's incremental
 *   up-to-date checks, and CI still work exactly as before.
 */
internal object ConfigPropertiesGenerator {

    /** A nested settings object in the config, generated as a block of Gradle properties. */
    data class SettingsGroup(
        val jsonKey: String,
        val objectName: String,
        val propertyPrefix: String,
        val header: String,
    )

    /** A top-level scalar value in the config, generated as a single Gradle property. */
    data class ScalarKey(
        val jsonKey: String,
        val propertyName: String,
        val header: String,
    )

    /**
     * Every settings group the generator understands, in output order.
     *
     * This is the single source of truth for both generated outputs. Adding a group to the
     * config without adding it here fails the build — see [validateTopLevelKeys].
     */
    val SETTINGS_GROUPS = listOf(
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
    val SCALAR_KEYS = listOf(
        ScalarKey("baseApiUrl", "vonage.baseApiUrl", "Base API URL"),
    )

    /**
     * Top-level keys that are intentionally not generated.
     *
     * `metadata` is descriptive only — it documents the config document itself (name, version,
     * created date, description) and drives no app behavior.
     */
    val IGNORED_KEYS = setOf("metadata")

    /**
     * Parses [configFile], validates it, writes [gradlePropertiesFile], and returns the parsed
     * JSON so callers that also need to generate Kotlin source (like
     * [com.vonage.gradle.tasks.GenerateConfigTask]) don't have to re-parse it.
     */
    fun generate(
        configFile: File,
        localPropertiesFile: File?,
        baseApiUrlFromEnv: String?,
        gradlePropertiesFile: File,
    ): JsonObject {
        require(configFile.exists()) { "Config file not found: ${configFile.absolutePath}" }

        val props = loadProps(localPropertiesFile, baseApiUrlFromEnv)
        val jsonContent = resolvePlaceholders(configFile.readText(), props)

        val jsonObject = Gson().fromJson(jsonContent, JsonObject::class.java)
        validateTopLevelKeys(jsonObject)

        val gradlePropsContent = generateGradleProperties(jsonObject)
        gradlePropertiesFile.parentFile?.mkdirs()
        gradlePropertiesFile.writeText(gradlePropsContent)

        return jsonObject
    }

    /** Load properties from environment variables and local.properties. */
    private fun loadProps(localPropertiesFile: File?, baseApiUrlFromEnv: String?): Map<String, String> {
        val properties = mutableMapOf<String, String>()

        if (localPropertiesFile != null && localPropertiesFile.exists()) {
            val loaded = Properties()
            localPropertiesFile.inputStream().use { loaded.load(it) }
            loaded.forEach { (key, value) ->
                properties[key.toString()] = value.toString()
            }
        }

        baseApiUrlFromEnv?.let { properties["BASE_API_URL"] = it }

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

    /** Replace ${VARIABLE} placeholders with actual values. */
    private fun resolvePlaceholders(text: String, properties: Map<String, String>): String {
        var result = text
        properties.forEach { (key, value) ->
            result = result.replace("\${$key}", value)
        }
        return result
    }

    /** Generate Gradle properties file content for build configuration. */
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
     * Fail the build when the config contains a top-level key this generator does not
     * understand.
     *
     * Both generated outputs work from explicit key lists ([SETTINGS_GROUPS], [SCALAR_KEYS]), so
     * an unrecognised key would otherwise be dropped silently: the build succeeds, no constant is
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

                To add a settings group, extend SETTINGS_GROUPS in ConfigPropertiesGenerator.
                To add a single value, extend SCALAR_KEYS.
                For a documentation-only block, add it to IGNORED_KEYS.
                """.trimIndent()
            )
        }
    }

    private fun StringBuilder.appendProp(propName: String, value: JsonElement) {
        appendLine("$propName=${value.asString}")
    }

    /** Convert camelCase to snake_case for property names. */
    private fun String.toSnakeCase(): String =
        replace(Regex("([a-z])([A-Z])"), "$1_$2").lowercase()
}
