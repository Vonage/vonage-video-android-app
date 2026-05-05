plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vonage.android.meetingroom"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
        // These feature modules are always included in their enabled variant from meeting-room's
        // perspective; the app layer controls which variant is used via its own
        // missingDimensionStrategy for direct feature-module dependencies.
        missingDimensionStrategy("chat", "enabled")
        missingDimensionStrategy("reactions", "enabled")
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
        buildConfig = true
    }

    // Only dimensions with actual source-set differences in this module need explicit flavors.
    // Dimensions without custom source sets (chat, reactions, videofx, audiofx, settings) are
    // handled via missingDimensionStrategy in defaultConfig, reducing variants from 512 to 16.
    flavorDimensions += listOf("archiving", "captions", "screensharing", "reporting")

    productFlavors {
        create("archivingEnabled") { dimension = "archiving"; matchingFallbacks += listOf("enabled") }
        create("archivingDisabled") { dimension = "archiving"; matchingFallbacks += listOf("disabled") }
        create("captionsEnabled") { dimension = "captions"; matchingFallbacks += listOf("enabled") }
        create("captionsDisabled") { dimension = "captions"; matchingFallbacks += listOf("disabled") }
        create("screensharingEnabled") { dimension = "screensharing"; matchingFallbacks += listOf("enabled") }
        create("screensharingDisabled") { dimension = "screensharing"; matchingFallbacks += listOf("disabled") }
        create("reportingEnabled") { dimension = "reporting"; matchingFallbacks += listOf("enabled") }
        create("reportingDisabled") { dimension = "reporting"; matchingFallbacks += listOf("disabled") }
    }
}

dependencies {
    // Core SDK and UI
    implementation(project(":vonage-video-core"))
    implementation(project(":vonage-video-ui-compose"))
    implementation(project(":vonage-video-shared"))
    implementation(project(":vonage-android-logger"))
    implementation(project(":vonage-audio-selector"))

    // Feature modules (enabled/disabled variants matched via flavor dimensions)
    implementation(project(":vonage-feature-chat"))
    implementation(project(":vonage-feature-reactions"))
    implementation(project(":vonage-feature-archiving"))
    implementation(project(":vonage-feature-captions"))
    implementation(project(":vonage-feature-screensharing"))
    implementation(project(":vonage-feature-video-effects"))
    implementation(project(":vonage-feature-audio-effects"))
    implementation(project(":vonage-feature-settings"))

    // Networking
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)

    // AndroidX / Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.opentok.android.sdk)
    implementation(libs.androidx.material.icons.extended)

    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}
