plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vonage.android.okta"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }

    flavorDimensions += "okta"
    productFlavors {
        create("enabled") {
            dimension = "okta"
            // okta-mobile-kotlin requires API 26+, so authentication-enabled builds
            // raise the minimum OS to Android 8.0 (app/build.gradle.kts mirrors this).
            minSdk = 26
        }
        create("disabled") {
            dimension = "okta"
        }
    }
}

val enabledImplementation by configurations

dependencies {
    implementation(project(":vonage-video-ui-compose"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui.tooling.preview)

    // Okta mobile SDK is only pulled in when the enabled flavor is compiled, so
    // a disabled build ships without any Okta code.
    enabledImplementation(platform(libs.okta.bom))
    enabledImplementation(libs.okta.auth.foundation)
    enabledImplementation(libs.okta.oauth2)
    enabledImplementation(libs.okta.web.authentication.ui)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(kotlin("test"))
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}
