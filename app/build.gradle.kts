import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.play.publisher)
    alias(libs.plugins.stability.analyzer)
    id("com.vonage.json-config")
}

val isReleaseBuild = gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }
if (isReleaseBuild) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}

val configProps = Properties()
val configFile = rootProject.file("gradle/generated-config.properties")
if (configFile.exists()) {
    configFile.inputStream().use { configProps.load(it) }
}

android {
    namespace = "com.vonage.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vonage.android"
        minSdk = 24
        targetSdk = 36
        // NOTE: The following versionCode and versionName are placeholders.
        // Actual values are set dynamically by the GitHub Actions workflow during CI/CD.
        versionCode = 110
        versionName = "1.1.0"

        testInstrumentationRunner = "com.vonage.android.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        // OpenTok SDK version
        buildConfigField("String", "OPENTOK_SDK_VERSION", "\"${libs.versions.opentokAndroidSdk.get()}\"")

        // Set up base API URL
        val baseApiUrl = configProps.getProperty("vonage.baseApiUrl", "")
        buildConfigField("String", "BASE_API_URL", "\"$baseApiUrl\"")
        manifestPlaceholders["hostName"] = baseApiUrl

        // Chat feature
        val chatProperty = configProps.getProperty("vonage.meetingRoom.allow_chat", "true")
        buildConfigField("boolean", "FEATURE_CHAT_ENABLED", "$chatProperty")
        missingDimensionStrategy("chat", chatProperty.toEnabledString())

        // Reactions feature
        val reactionsProperty = configProps.getProperty("vonage.meetingRoom.allow_emojis", "true")
        buildConfigField("boolean", "FEATURE_REACTIONS_ENABLED", "$reactionsProperty")
        missingDimensionStrategy("reactions", reactionsProperty.toEnabledString())

        // Archiving/recording feature
        val archivingProperty = configProps.getProperty("vonage.meetingRoom.allow_archiving", "true")
        buildConfigField("boolean", "FEATURE_ARCHIVING_ENABLED", "$archivingProperty")
        missingDimensionStrategy("archiving", archivingProperty.toEnabledString(), archivingProperty.toModuleFlavorString("archiving"))

        // Captions feature
        val captionsProperty = configProps.getProperty("vonage.meetingRoom.allow_captions", "true")
        buildConfigField("boolean", "FEATURE_CAPTIONS_ENABLED", "$captionsProperty")
        missingDimensionStrategy("captions", captionsProperty.toEnabledString(), captionsProperty.toModuleFlavorString("captions"))

        // Screensharing feature
        val screenSharingProperty = configProps.getProperty("vonage.meetingRoom.allow_screen_share", "true")
        buildConfigField("boolean", "FEATURE_SCREENSHARING_ENABLED", "$screenSharingProperty")
        missingDimensionStrategy(
            "screensharing",
            screenSharingProperty.toEnabledString(),
            screenSharingProperty.toModuleFlavorString("screensharing"),
        )

        // Background (video) effects feature
        val videoFxProperty = configProps.getProperty("vonage.video.allow_background_effects", "true")
        buildConfigField("boolean", "FEATURE_VIDEO_EFFECTS_ENABLED", "$videoFxProperty")
        missingDimensionStrategy("videofx", videoFxProperty.toEnabledString())

        // Audio effects feature
        val audioFxProperty = configProps.getProperty("vonage.audio.allow_advanced_noise_suppression", "true")
        buildConfigField("boolean", "FEATURE_AUDIO_EFFECTS_ENABLED", "$audioFxProperty")
        missingDimensionStrategy("audiofx", audioFxProperty.toEnabledString())

        // Settings feature
        val settingsProperty = configProps.getProperty("vonage.meetingRoom.allow_settings", "true")
        buildConfigField("boolean", "FEATURE_SETTINGS_ENABLED", "$settingsProperty")
        missingDimensionStrategy("settings", settingsProperty.toEnabledString())

        // Reporting feature — always enabled
        missingDimensionStrategy("reporting", "reportingEnabled")
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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        animationsDisabled = true
        managedDevices {
            localDevices {
                create("pixel") {
                    device = "Pixel 2"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }

    val signFile: File = rootProject.file(".sign/keystore.properties")
    if (signFile.exists()) {
        val properties = Properties()
        properties.load(FileInputStream(signFile))

        signingConfigs {
            create("release") {
                keyAlias = properties["keyAlias"] as? String
                keyPassword = properties["keyPassword"] as? String
                storeFile = rootProject.file(properties["keystore"] as String)
                storePassword = properties["storePassword"] as? String
            }
        }
    } else {
        signingConfigs {
            create("release") {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = rootProject.file(".sign/debug.keystore.jks")
                storePassword = "android"
            }
        }
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storeFile = rootProject.file(".sign/debug.keystore.jks")
            storePassword = "android"
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-DEBUG"
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    sourceSets.configureEach {
        kotlin.srcDir(layout.buildDirectory.dir("generated/source/jsonConfig"))
    }
}

play {
    serviceAccountCredentials.set(rootProject.file(".sign/service_account.json"))
    track.set("alpha")
    releaseStatus.set(com.github.triplet.gradle.androidpublisher.ReleaseStatus.COMPLETED)
    artifactDir.set(file("build/outputs/bundle/release"))
    defaultToAppBundles.set(true)
}

dependencies {
    implementation(project(":vonage-meeting-room"))
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
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.material.icons.extended)
    ksp(libs.hilt.android.compiler)
    // InApp Updates
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // Firebase Crashlytics - Only in release builds
    releaseImplementation(platform(libs.firebase.bom))
    releaseImplementation(libs.firebase.crashlytics.ndk)
    releaseImplementation(libs.firebase.analytics)

    // Vonage Video Android SDK, needed to customize Audio Device
    implementation(libs.opentok.android.sdk)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)

    debugImplementation(libs.leakcanary.android)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

fun String.toEnabledString(): String = if (toBoolean()) "enabled" else "disabled"

/**
 * Returns the prefixed flavor name used by the vonage-meeting-room module,
 * e.g. "chat" + true → "chatEnabled".
 */
fun String.toModuleFlavorString(dimension: String): String =
    if (toBoolean()) "${dimension}Enabled" else "${dimension}Disabled"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateVonageConfig")
}

tasks.matching { it.name.startsWith("ksp") }.configureEach {
    dependsOn("generateVonageConfig")
}

jsonConfig {
    configFile.set("config/app-config.json")
}
