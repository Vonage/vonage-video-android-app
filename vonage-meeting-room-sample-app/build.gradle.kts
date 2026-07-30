plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.vonage.sample.meetingroom"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vonage.sample.meetingroom"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Flavor dimension strategies: first value matches feature modules (enabled/disabled),
        // second value matches vonage-meeting-room prefixed flavors (archivingEnabled, etc.)
        missingDimensionStrategy("archiving", "enabled", "archivingEnabled")
        missingDimensionStrategy("captions", "enabled", "captionsEnabled")
        missingDimensionStrategy("screensharing", "enabled", "screensharingEnabled")
        missingDimensionStrategy("chat", "enabled")
        missingDimensionStrategy("reactions", "enabled")
        missingDimensionStrategy("videofx", "enabled")
        missingDimensionStrategy("audiofx", "enabled")
        missingDimensionStrategy("settings", "enabled")
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        optIn.add("com.vonage.android.meetingroom.api.ExperimentalMeetingRoomApi")
    }
}

dependencies {
    implementation(project(":vonage-meeting-room"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}
