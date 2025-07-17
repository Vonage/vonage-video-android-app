plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.vonage.android.kotlin"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    //implementation(libs.opentok.android.sdk)
    implementation(files("libs/opentok-android-sdk-debug-2.32.0.aar"))
    implementation("com.google.guava:guava:31.1-android")
    implementation("com.vonage:webrtc:121.1.97")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.runtime.android)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit)
}