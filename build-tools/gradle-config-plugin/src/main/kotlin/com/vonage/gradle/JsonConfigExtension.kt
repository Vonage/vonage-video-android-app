package com.vonage.gradle

import org.gradle.api.provider.Property

abstract class JsonConfigExtension {
    abstract val configFile: Property<String>
    abstract val outputPackage: Property<String>
    abstract val className: Property<String>

    init {
        outputPackage.convention("com.vonage.android.config")
        className.convention("AppConfig")
    }
}
