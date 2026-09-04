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
        project.tasks.register("generateTheme", GenerateThemeTask::class.java) {
            themeJsonFile.set(extension.themeJsonFile)
            outputPackage.set(extension.outputPackage)
            themeDirectory.set(extension.themeDirectory)
        }
    }
}
