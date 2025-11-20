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
    }
}

tasks.test {
    useJUnitPlatform()
}