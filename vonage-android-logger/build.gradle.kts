plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
}

android {
    namespace = "com.vonage.logger"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests { isReturnDefaultValues = true }
    }
}

dependencies {
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
}