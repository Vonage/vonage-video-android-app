package com.vonage.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

abstract class ThemeGeneratorExtension {
    abstract val themeJsonFile: RegularFileProperty
    abstract val outputPackage: Property<String>
    abstract val themeDirectory: DirectoryProperty
    abstract val generateColors: Property<Boolean>
    abstract val generateTypography: Property<Boolean>
    abstract val generateShapes: Property<Boolean>
    abstract val generateDimens: Property<Boolean>

    init {
        generateColors.convention(true)
        generateTypography.convention(true)
        generateShapes.convention(true)
        generateDimens.convention(false) // Don't regenerate dimens by default
    }
}
