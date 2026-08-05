plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.binary.compatibility.validator)
}

android {
    namespace = "com.vonage.audioselector"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests { isReturnDefaultValues = true }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.opentok.android.sdk)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}

// Workaround for https://github.com/Kotlin/binary-compatibility-validator/issues/312
// BCV hooks into kotlin-android plugin to register its tasks, but AGP 9.x uses built-in
// Kotlin and blocks that plugin. We manually register equivalent tasks using BCV internals.
// The runtimeClasspath for the BCV worker must include kotlin-metadata-jvm, which BCV normally
// resolves via its internal jvmValidationClasspath configuration. We recreate it manually.
val bcvRuntimeClasspath = configurations.create("bcvRuntimeClasspath") {
    isCanBeConsumed = false
    isCanBeResolved = true
    dependencies.add(project.dependencies.create("org.jetbrains.kotlinx:binary-compatibility-validator:0.18.1"))
    dependencies.add(project.dependencies.create("org.jetbrains.kotlin:kotlin-metadata-jvm:${libs.versions.kotlin.get()}"))
}
val apiBuild = tasks.register<kotlinx.validation.KotlinApiBuildTask>("androidApiBuild") {
    outputApiFile = project.layout.buildDirectory.file("${this.name}/vonage-audio-selector.api")
    inputClassesDirs.from(tasks.named("compileReleaseKotlin").map { it.outputs.files })
    runtimeClasspath.from(bcvRuntimeClasspath)
}
val androidApiCheck = tasks.register<kotlinx.validation.KotlinApiCompareTask>("androidApiCheck") {
    group = "verification"
    projectApiFile = project.file("api/vonage-audio-selector.api")
    generatedApiFile = apiBuild.flatMap(kotlinx.validation.KotlinApiBuildTask::outputApiFile)
}
val androidApiDump = tasks.register<Copy>("androidApiDump") {
    from(apiBuild.flatMap(kotlinx.validation.KotlinApiBuildTask::outputApiFile))
    destinationDir = project.file("api")
}
afterEvaluate {
    tasks.findByName("apiDump")?.dependsOn(androidApiDump)
    tasks.findByName("apiCheck")?.dependsOn(androidApiCheck)
}