import org.gradle.kotlin.dsl.implementation

plugins {
    `kotlin-dsl`
}

group = "com.vonage.gradle"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(gradleApi())
    implementation(libs.gradle)
    implementation(libs.kotlin.stdlib)
    implementation(libs.gson)
    implementation(libs.kotlinpoet)
    implementation(libs.binary.compatibility.validator)
}

gradlePlugin {
    plugins {
        create("jsonConfig") {
            id = "com.vonage.json-config"
            implementationClass = "com.vonage.gradle.JsonConfigPlugin"
            displayName = "JSON Config Generator"
            description = "Generates BuildConfig fields from JSON configuration files"
        }
        create("themeGenerator") {
            id = "com.vonage.theme-generator"
            implementationClass = "com.vonage.gradle.ThemeGeneratorPlugin"
            displayName = "Theme Generator"
            description = "Generates Compose theme code from theme.json configuration"
        }
        create("androidApiValidation") {
            id = "com.vonage.android-api-validation"
            implementationClass = "com.vonage.gradle.AndroidApiValidationPlugin"
            displayName = "Android API Validation"
            description = "Registers binary-compatibility-validator tasks for AGP 9 built-in Kotlin modules"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}