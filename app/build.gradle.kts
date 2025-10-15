import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kover)
    kotlin("plugin.serialization") version "2.0.21"
    id("com.vonage.json-config")
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
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.vonage.android.HiltTestRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        // chat conditional configuration
        val chatProperty = configProps.getProperty("vonage.meetingRoom.allow_chat", "false")
        buildConfigField("boolean", "FEATURE_CHAT_ENABLED", "$chatProperty")
        missingDimensionStrategy("chat", chatProperty.toEnabledString())
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isProfileable = false
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isProfileable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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
        unitTests.all {
            it.useJUnitPlatform()
        }
        animationsDisabled = true
    }

    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }

    sourceSets.configureEach {
        kotlin.srcDir("build/generated/source/jsonConfig/com/vonage/android/config")
    }
}

fun String.toEnabledString(): String = if (toBoolean()) "enabled" else "disabled"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateVonageConfig")
}

tasks.matching { it.name.startsWith("ksp") }.configureEach {
    dependsOn("generateVonageConfig")
}

jsonConfig {
    configFile.set("config/app-config.json")
}

dependencies {
    implementation(project(":compose"))
    implementation(project(":kotlin"))
    implementation(project(":shared"))
    implementation(project(":vonage-feature-chat"))
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
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.hilt.android.compiler)

    // to be removed when extracting to module all the audio stuff
    implementation(libs.opentok.android.sdk)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testRuntimeOnly(libs.junit.jupiter.engine)

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
