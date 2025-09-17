import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    kotlin("plugin.serialization") version "2.0.21"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("org.sonarqube") version "6.3.1.5724"
}

android {
    namespace = "com.vonage.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vonage.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 5
        versionName = "0.0.5"

        testInstrumentationRunner = "com.vonage.android.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
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
        unitTests.all {
            it.useJUnitPlatform()
        }
        animationsDisabled = true
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
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":kotlin"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.androidx.material3)
    ksp(libs.hilt.android.compiler)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)

    // to be removed when extracting to module all the audio stuff
    implementation(libs.opentok.android.sdk)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

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
