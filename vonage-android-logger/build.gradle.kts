plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.binary.compatibility.validator)
}

android {
    namespace = "com.vonage.logger"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            all { it.useJUnitPlatform() }
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
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
    outputApiFile = project.layout.buildDirectory.file("${this.name}/vonage-android-logger.api")
    inputClassesDirs.from(tasks.named("compileReleaseKotlin").map { it.outputs.files })
    runtimeClasspath.from(bcvRuntimeClasspath)
}
val androidApiCheck = tasks.register<kotlinx.validation.KotlinApiCompareTask>("androidApiCheck") {
    group = "verification"
    projectApiFile = project.file("api/vonage-android-logger.api")
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
