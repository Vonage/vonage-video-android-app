package com.vonage.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.vonage.gradle.tasks.GenerateConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project

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
        val taskProvider = project.tasks.register(
            "generateVonageConfig",
            GenerateConfigTask::class.java,
        ) {
            configFile.set(extension.configFile)
            outputPackage.set(extension.outputPackage)
            className.set(extension.className)
            outputDir.set(project.layout.buildDirectory.dir("generated/source/jsonConfig"))
        }

        project.extensions.getByType(AndroidComponentsExtension::class.java).onVariants { variant ->
            variant.sources.kotlin?.addGeneratedSourceDirectory(
                taskProvider,
                GenerateConfigTask::outputDir,
            )
        }
    }
}
