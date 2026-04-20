plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.vonage.android.meetingroom"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Default flavor strategies for feature module dimensions
        missingDimensionStrategy("chat", "enabled")
        missingDimensionStrategy("reactions", "enabled")
        missingDimensionStrategy("archiving", "enabled")
        missingDimensionStrategy("captions", "enabled")
        missingDimensionStrategy("screensharing", "enabled")
        missingDimensionStrategy("videofx", "enabled")
        missingDimensionStrategy("audiofx", "enabled")
        missingDimensionStrategy("settings", "enabled")
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
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(project(":vonage-video-ui-compose"))
    implementation(project(":vonage-video-core"))
    implementation(project(":vonage-video-shared"))
    implementation(project(":vonage-feature-chat"))
    implementation(project(":vonage-feature-archiving"))
    implementation(project(":vonage-feature-screensharing"))
    implementation(project(":vonage-feature-reactions"))
    implementation(project(":vonage-feature-video-effects"))
    implementation(project(":vonage-feature-audio-effects"))
    implementation(project(":vonage-feature-captions"))
    implementation(project(":vonage-feature-settings"))
    implementation(project(":vonage-audio-selector"))
    implementation(project(":vonage-android-logger"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.material.icons.extended)
    implementation("javax.inject:javax.inject:1")

    testImplementation(libs.junit.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}



