package com.vonage.gradle.tasks

import com.google.gson.JsonObject
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.vonage.gradle.ConfigPropertiesGenerator
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

    /**
     * Parses the config, validates its top-level keys, and (re)writes
     * `gradle/generated-config.properties` — via [ConfigPropertiesGenerator], shared with the
     * eager pass in [com.vonage.gradle.JsonConfigPlugin] so both produce identical output — then
     * generates the [AppConfig]-equivalent Kotlin source with KotlinPoet.
     */
    @TaskAction
    fun generateConfig() {
        val configFile = configSource.get().asFile

        val jsonObject = ConfigPropertiesGenerator.generate(
            configFile = configFile,
            localPropertiesFile = localPropertiesFile.asFile.orNull,
            baseApiUrlFromEnv = baseApiUrlFromEnv.orNull,
            gradlePropertiesFile = gradlePropertiesFile.get().asFile,
        )

        val packageName = outputPackage.get()
        val className = className.get()

        val fileSpec = generateBuildConfigClass(packageName, className, jsonObject)
        fileSpec.writeTo(outputDir.get().asFile)

        logger.info("Generated config class: ${outputDir.get().asFile.absolutePath}")
        logger.info("Generated gradle properties: ${gradlePropertiesFile.get().asFile.absolutePath}")
    }

    private fun generateBuildConfigClass(
        packageName: String,
        className: String,
        jsonObject: JsonObject,
    ): FileSpec {
        val configObject = TypeSpec.objectBuilder(className)
            .addKdoc("Generated configuration for Vonage Video SDK\nDo not modify this file manually")

        ConfigPropertiesGenerator.SETTINGS_GROUPS.forEach { group ->
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

    @Suppress("NestedBlockDepth")
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
     * Converts camelCase to UPPER_CASE format for constants
     */
    private fun String.toCamelCase(): String =
        replace(Regex("([a-z])([A-Z])"), "$1_$2")
}
