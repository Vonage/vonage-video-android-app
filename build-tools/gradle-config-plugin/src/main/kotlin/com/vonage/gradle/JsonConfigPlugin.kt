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
