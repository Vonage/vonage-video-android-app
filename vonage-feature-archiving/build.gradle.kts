plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.vonage.android.archiving"
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

    flavorDimensions += "archiving"
    productFlavors {
        create("enabled") {
            dimension = "archiving"
        }
        create("disabled") {
            dimension = "archiving"
        }
    }
}

val enabledImplementation by configurations

dependencies {
    implementation(project(":vonage-video-ui-compose"))
    implementation(project(":vonage-video-core"))
    implementation(project(":vonage-video-shared"))
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

    enabledImplementation(libs.retrofit)
    enabledImplementation(libs.okhttp)
    enabledImplementation(libs.moshi.kotlin)

    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))
}
