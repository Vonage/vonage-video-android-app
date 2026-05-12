import org.gradle.kotlin.dsl.detektPlugins

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.sonarqube) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.detekt)
    id("com.vonage.json-config") apply false
}

dependencies {
    detektPlugins(libs.detekt.compose.rules)
}

tasks.register("installGitHooks") {
    description = "Installs the pre-push Git hook from scripts/git-hooks"
    group = "setup"
    val hookSource = file("scripts/git-hooks/pre-push")
    val hookDest = file(".git/hooks/pre-push")
    inputs.file(hookSource)
    outputs.file(hookDest)
    doLast {
        hookSource.copyTo(hookDest, overwrite = true)
        hookDest.setExecutable(true)
        logger.lifecycle("Installed pre-push Git hook.")
    }
}

kover {
    merge {
        allProjects {
            it.name !in listOf(
                "vonage-video-ui-compose",
                "vonage-video-sdk",
                "vonage-config-idea-plugin"
            )
        }
    }

    reports {
        filters {
            excludes {
                androidGeneratedClasses()
                classes(
                    "*_Factory*",
                    "*Factory_Impl",
                    "*_MembersInjector",
                    "*Module",
                    "*Module_*",
                    "*_GeneratedInjector",
                    "*ComposableSingletons*",
                    "*MainApplication"
                )
                annotatedBy(
                    "androidx.compose.runtime.Stable",
                    "androidx.compose.runtime.Composable",
                    "androidx.compose.ui.tooling.preview.Preview",
                    "androidx.compose.ui.tooling.preview.PreviewLightDark",
                    "androidx.compose.ui.tooling.preview.PreviewScreenSizes",
                    "javax.annotation.processing.Generated",
                    "dagger.hilt.processor.internal.aggregateddeps.AggregatedDeps"
                )
            }
        }
    }
}

apply(from = "${rootDir}/build-tools/sonar.gradle")

val koverExcludedModules = listOf("vonage-video-ui-compose", "vonage-video-sdk", "vonage-config-idea-plugin")

subprojects {
    if (name !in koverExcludedModules) {
        apply(from = "${rootDir}/build-tools/kover.gradle")
    }
}

apply(from = "build-tools/detekt.gradle")
