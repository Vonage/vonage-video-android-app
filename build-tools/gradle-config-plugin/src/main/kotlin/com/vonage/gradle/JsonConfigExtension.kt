package com.vonage.gradle

import org.gradle.api.provider.Property

abstract class JsonConfigExtension {
    abstract val configFile: Property<String>
    abstract val outputPackage: Property<String>
    abstract val className: Property<String>

    init {
        // Every current caller sets this explicitly to "app-config.json" anyway; the convention
        // lets JsonConfigPlugin resolve and eagerly generate gradle/generated-config.properties
        // during `apply()` — before the script's own `jsonConfig { ... }` block or `android { }`
        // block run — without needing to wait for the script to set it. `-Dconfig.file` still
        // takes precedence over both, per resolveConfigFile().
        configFile.convention("app-config.json")
        outputPackage.convention("com.vonage.android.config")
        className.convention("AppConfig")
    }
}
