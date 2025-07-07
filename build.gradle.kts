import org.gradle.kotlin.dsl.detektPlugins

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt)
}

dependencies {
    detektPlugins(libs.detekt.compose.rules)
}

afterEvaluate {
    println("Running Add Pre Push Git Hook")
    exec {
        commandLine("cp", "./scripts/git-hooks/pre-push", "./.git/hooks")
    }
    println("Added pre-push Git Hook Script.")
}

apply(from = "build-tools/detekt.gradle")
