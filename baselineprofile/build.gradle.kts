import java.util.Properties

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.androidx.baselineprofile)
}

val configProps = Properties().apply {
    val file = rootProject.file("gradle/generated-config.properties")
    if (file.exists()) file.inputStream().use(::load)
}

data class FeatureKey(val property: String, val default: Boolean = true, val hasModuleFlavor: Boolean = false)

// Same keys and defaults as app/build.gradle.kts.
val featureDimensions = mapOf(
    "chat" to FeatureKey("vonage.meetingRoom.allow_chat"),
    "reactions" to FeatureKey("vonage.meetingRoom.allow_emojis"),
    "captions" to FeatureKey("vonage.meetingRoom.allow_captions", hasModuleFlavor = true),
    "screensharing" to FeatureKey("vonage.meetingRoom.allow_screen_share", hasModuleFlavor = true),
    "videofx" to FeatureKey("vonage.video.allow_background_effects"),
    "audiofx" to FeatureKey("vonage.audio.allow_advanced_noise_suppression"),
    "settings" to FeatureKey("vonage.meetingRoom.allow_settings"),
    "okta" to FeatureKey("vonage.auth.allow_authentication", default = false),
)

android {
    namespace = "com.vonage.android.baselineprofile"
    compileSdk {
        version = release(libs.versions.compileSdk.get().toInt()) {
            minorApiLevel = libs.versions.compileSdkMinor.get().toInt()
        }
    }

    defaultConfig {
        // BaselineProfileRule requires API 28+.
        minSdk = 28
        targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // This module resolves :app's dependency graph, so it must pick the same feature flavors
        // as :app does (see the missingDimensionStrategy calls in app/build.gradle.kts).
        featureDimensions.forEach { (dimension, key) ->
            val enabled = configProps.getProperty(key.property, key.default.toString()).toBoolean()
            val flavor = if (enabled) "enabled" else "disabled"
            if (key.hasModuleFlavor) {
                missingDimensionStrategy(dimension, flavor, dimension + flavor.replaceFirstChar(Char::uppercase))
            } else {
                missingDimensionStrategy(dimension, flavor)
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    targetProjectPath = ":app"

    testOptions.managedDevices.localDevices {
        // Non-ATD image: profile collection needs the full ART runtime. API 33+ needs no root.
        create("pixel6Api34") {
            device = "Pixel 6"
            apiLevel = 34
            systemImageSource = "aosp"
        }
    }
}

baselineProfile {
    managedDevices += "pixel6Api34"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

// Pass the tested app's applicationId to the tests, so they don't hardcode it.
androidComponents {
    onVariants { variant ->
        val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
        variant.instrumentationRunnerArguments.put(
            "targetAppId",
            variant.testedApks.map { artifactsLoader.load(it)?.applicationId.orEmpty() },
        )
    }
}
