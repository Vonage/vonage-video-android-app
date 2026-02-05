plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
}

android {
    namespace = "com.vonage.audioselector"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.opentok.android.sdk)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}