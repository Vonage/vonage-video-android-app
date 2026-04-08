plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.metalava)
}

android {
    namespace = "com.vonage.logger"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests { isReturnDefaultValues = true }
    }
}

metalava {
    filename = "api/$name.api"
}

dependencies {
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
}