import org.gradle.kotlin.dsl.detektPlugins

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.detekt)
    id("org.sonarqube") version "6.3.1.5724"
}

sonar {
    properties {
        property("sonar.projectKey", "Vonage_vonage-video-android-app")
        property("sonar.organization", "vonage")
    }
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
