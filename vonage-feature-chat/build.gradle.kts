plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.vonage.android.chat"
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "chat"
    productFlavors {
        create("enabled") {
            dimension = "chat"
        }
        create("disabled") {
            dimension = "chat"
        }
    }
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":kotlin"))
    implementation(project(":shared"))
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.opentok.android.sdk)

    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(kotlin("test"))
}