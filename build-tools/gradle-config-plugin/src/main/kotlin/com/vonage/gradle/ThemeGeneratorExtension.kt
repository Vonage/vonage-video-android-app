package com.vonage.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

abstract class ThemeGeneratorExtension {
    abstract val themeJsonFile: RegularFileProperty
    abstract val outputPackage: Property<String>
    abstract val themeDirectory: DirectoryProperty
}
