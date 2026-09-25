package com.vonage.gradle

import com.vonage.gradle.tasks.GenerateConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class JsonConfigPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("jsonConfig", JsonConfigExtension::class.java)

        project.plugins.withId("com.android.application") {
            configureAndroidProject(project, extension)
        }

        project.plugins.withId("com.android.library") {
            configureAndroidProject(project, extension)
        }
    }

    private fun configureAndroidProject(project: Project, extension: JsonConfigExtension) {
        val taskName = "generateVonageConfig"

        val generatedPropertiesFile = project.rootProject.layout.projectDirectory
            .file("gradle/generated-config.properties")
            .asFile

        // Eagerly regenerate `gradle/generated-config.properties` right now, during this
        // module's `apply()` — before the rest of the build script runs.
        //
        // `app/build.gradle.kts` reads that properties file synchronously (outside any task) to
        // pick `missingDimensionStrategy` flavors, `BuildConfig` fields, and `minSdk`. Those are
        // all resolved once, during configuration, and can't be changed afterwards by a task
        // that only runs during execution. Previously `generateVonageConfig` only ran as a task
        // (wired via `preBuild.finalizedBy(...)` below), so its output was written *after* the
        // properties file had already been read for this invocation: the documented
        // `./gradlew generateVonageConfig installDebug` could still install a build compiled
        // against the *previous* config, especially across a flavor or base-URL change.
        // Regenerating synchronously here closes that gap. The task registered below is kept
        // so `./gradlew generateVonageConfig` on its own, Gradle's incremental up-to-date
        // checks, and any CI step invoking it explicitly keep working unchanged.
        //
        // Limitation: this eager pass resolves the config file via `-Dconfig.file` or the
        // `configFile` convention default (`"app-config.json"`) — it runs before this module's
        // own `jsonConfig { configFile.set(...) }` block has executed, so a non-default
        // `configFile` set there is honoured by the task but not by this eager pass. Every
        // current caller uses the default, so this only matters for a future caller that needs
        // a different config file name without `-Dconfig.file`.
        runCatching {
            ConfigPropertiesGenerator.generate(
                configFile = resolveConfigFile(project, extension),
                localPropertiesFile = project.rootProject.file("local.properties").takeIf { it.exists() },
                baseApiUrlFromEnv = System.getenv("BASE_API_URL"),
                gradlePropertiesFile = generatedPropertiesFile,
            )
        }.onFailure { error ->
            // Don't fail configuration here — e.g. BASE_API_URL may legitimately be unset for
            // a task unrelated to building the app (like `detekt`). The registered task below
            // still runs the same generation and reports the real error when it matters.
            project.logger.warn(
                "jsonConfig: could not eagerly regenerate gradle/generated-config.properties " +
                    "(${error.message}). Run './gradlew $taskName' to see the full error.",
            )
        }

        project.tasks.register(taskName, GenerateConfigTask::class.java, {
            configSource.set(
                project.layout.file(project.provider { resolveConfigFile(project, extension) })
            )
            localPropertiesFile.set(
                project.layout.file(
                    project.provider {
                        project.rootProject.file("local.properties").takeIf { it.exists() }
                    }
                )
            )
            baseApiUrlFromEnv.set(project.provider { System.getenv("BASE_API_URL") })
            outputPackage.set(extension.outputPackage)
            className.set(extension.className)
            outputDir.set(project.layout.buildDirectory.dir("generated/source/jsonConfig"))
            gradlePropertiesFile.set(
                project.rootProject.layout.projectDirectory.file("gradle/generated-config.properties")
            )
        })

        project.afterEvaluate {
            tasks.getByName("preBuild").finalizedBy(project.tasks.getByName(taskName))
        }
    }

    /**
     * Resolve the config file location, in precedence order: the `config.file` system property,
     * an absolute path from the extension, then a path relative to the root project.
     */
    private fun resolveConfigFile(project: Project, extension: JsonConfigExtension): File {
        System.getProperty("config.file")?.let { return File(it) }

        val configPath = extension.configFile.get()
        val configFile = File(configPath)

        return if (configFile.isAbsolute) configFile else project.rootDir.resolve(configPath)
    }
}
