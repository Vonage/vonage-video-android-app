plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.0.21"
    id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = "com.vonage"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
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
        name = "Vonage Reference App Config Plugin"
        version = "1.0.0-SNAPSHOT"
        ideaVersion {
            sinceBuild = "242"
            untilBuild = "253.*"
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}
