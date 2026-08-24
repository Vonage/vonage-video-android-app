plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = "com.vonage"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    // NOTE: The IntelliJ Platform bundles Kotlin stdlib; verifyPluginProjectConfiguration
    // will report "Kotlin stdlib dependency conflict" because the Kotlin Gradle Plugin
    // auto-adds it. The recommended fix (`kotlin.stdlib.default.dependency=false` in
    // gradle.properties) is a global toggle and breaks every other Kotlin module in this
    // multi-module repo. Excluding stdlib via `configurations.all { exclude(...) }`
    // breaks the IntelliJ Platform's own transformed classpath at build time.
    // The warning is accepted as advisory: at packaging the `composedJar` task keeps
    // only this module's classes, and at runtime the plugin ClassLoader delegates to
    // the platform's stdlib. There is no observed runtime conflict.
    implementation(libs.kotlinx.serialization.json)

    intellijPlatform {
        intellijIdeaCommunity("2025.2")
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugins("org.jetbrains.plugins.gradle")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        pluginVerifier()
        zipSigner()
    }

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

intellijPlatform {
    buildSearchableOptions = false
    pluginConfiguration {
        id = "com.vonage.confighelper"
        name = "Vonage Reference App Config"
        version = "1.0.0-SNAPSHOT"
        ideaVersion {
            sinceBuild = "252"
            // No untilBuild: allow forward compatibility with future IDE builds.
            // The plugin verifier CLI still runs against `recommended()` builds below.
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}
