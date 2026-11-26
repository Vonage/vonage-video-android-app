package com.vonage.gradle

import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.Copy

/**
 * Workaround for https://github.com/Kotlin/binary-compatibility-validator/issues/312
 *
 * BCV hooks into the `kotlin-android` plugin to register its tasks, but AGP 9.x uses built-in
 * Kotlin and blocks that plugin, so `apiCheck`/`apiDump` are never created. The Kotlin Gradle
 * plugin's own `abiValidation` has the same gap (as of Kotlin 2.4.21 its tasks register but dump
 * nothing). This plugin registers equivalent tasks using BCV's task classes:
 *
 * - `androidApiCheck`: compares the release classes against `api/<project>.api`.
 * - `androidApiDump`: rewrites `api/<project>.api` from the release classes.
 *
 * The BCV worker classpath (BCV itself plus kotlin-metadata-jvm) is normally resolved by BCV's
 * internal configuration; it is recreated here from the version catalog.
 *
 * Remove this plugin once BCV or the Kotlin Gradle plugin supports AGP 9 built-in Kotlin.
 */
class AndroidApiValidationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.withId("com.android.library") {
            configure(project)
        }
    }

    private fun configure(project: Project) {
        val libs = project.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
        val workerClasspath = project.configurations.create("bcvRuntimeClasspath") {
            isCanBeConsumed = false
            isCanBeResolved = true
            dependencies.add(project.dependencies.create(libs.findLibrary("binary-compatibility-validator").get().get()))
            dependencies.add(project.dependencies.create(libs.findLibrary("kotlin-metadata-jvm").get().get()))
        }
        val apiFileName = "${project.name}.api"

        val apiBuild = project.tasks.register("androidApiBuild", KotlinApiBuildTask::class.java) {
            outputApiFile.set(project.layout.buildDirectory.file("$name/$apiFileName"))
            inputClassesDirs.from(project.tasks.named("compileReleaseKotlin").map { it.outputs.files })
            runtimeClasspath.from(workerClasspath)
        }
        project.tasks.register("androidApiCheck", KotlinApiCompareTask::class.java) {
            group = "verification"
            description = "Checks the public API of the release variant against api/$apiFileName"
            projectApiFile.set(project.layout.projectDirectory.file("api/$apiFileName"))
            generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
        }
        project.tasks.register("androidApiDump", Copy::class.java) {
            group = "other"
            description = "Writes the public API of the release variant to api/$apiFileName"
            from(apiBuild.flatMap { it.outputApiFile })
            into(project.layout.projectDirectory.dir("api"))
        }
    }
}
