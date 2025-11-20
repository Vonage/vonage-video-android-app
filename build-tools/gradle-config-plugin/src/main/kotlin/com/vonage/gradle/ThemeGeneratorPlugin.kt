package com.vonage.gradle

import com.vonage.gradle.tasks.GenerateThemeTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ThemeGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create("themeGenerator", ThemeGeneratorExtension::class.java)

        project.plugins.withId("com.android.library") {
            configureAndroidProject(project, extension)
        }

        project.plugins.withId("com.android.application") {
            configureAndroidProject(project, extension)
        }
    }

    private fun configureAndroidProject(project: Project, extension: ThemeGeneratorExtension) {
        val taskName = "generateTheme"

        val generateTask = project.tasks.register(taskName, GenerateThemeTask::class.java) {
            themeJsonFile.set(extension.themeJsonFile)
            outputPackage.set(extension.outputPackage)
            themeDirectory.set(extension.themeDirectory)
            generateColors.set(extension.generateColors)
            generateTypography.set(extension.generateTypography)
            generateShapes.set(extension.generateShapes)
            generateDimens.set(extension.generateDimens)
        }

        // Don't auto-run on preBuild, let it be manually triggered
        project.tasks.register("updateTheme") {
            group = "vonage"
            description = "Updates theme files from theme.json"
            dependsOn(generateTask)
        }
    }
}
