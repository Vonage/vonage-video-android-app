@file:Suppress("LongMethod")

package com.vonage.gradle.tasks

import com.google.gson.Gson
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.vonage.gradle.model.ColorScheme
import com.vonage.gradle.model.Theme
import com.vonage.gradle.model.background
import com.vonage.gradle.model.border
import com.vonage.gradle.model.disabled
import com.vonage.gradle.model.error
import com.vonage.gradle.model.errorHover
import com.vonage.gradle.model.onBackground
import com.vonage.gradle.model.onError
import com.vonage.gradle.model.onPrimary
import com.vonage.gradle.model.onSecondary
import com.vonage.gradle.model.onSuccess
import com.vonage.gradle.model.onSurface
import com.vonage.gradle.model.onTertiary
import com.vonage.gradle.model.onWarning
import com.vonage.gradle.model.primary
import com.vonage.gradle.model.primaryHover
import com.vonage.gradle.model.secondary
import com.vonage.gradle.model.success
import com.vonage.gradle.model.successHover
import com.vonage.gradle.model.surface
import com.vonage.gradle.model.tertiary
import com.vonage.gradle.model.textDisabled
import com.vonage.gradle.model.warning
import com.vonage.gradle.model.warningHover
import com.vonage.gradle.VONAGE
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import com.vonage.gradle.model.TextStyle as ThemeTextStyle

private const val COLOR_LENGTH = 6
private const val COLOR_LENGTH_WITH_ALPHA = 8

private val COLOR = ClassName("androidx.compose.ui.graphics", "Color")
private val COLOR_SCHEME = ClassName("androidx.compose.material3", "ColorScheme")
private val LIGHT_COLOR_SCHEME_FN = MemberName("androidx.compose.material3", "lightColorScheme")
private val DARK_COLOR_SCHEME_FN = MemberName("androidx.compose.material3", "darkColorScheme")
private val IMMUTABLE = ClassName("androidx.compose.runtime", "Immutable")
private val STATIC_COMPOSITION_LOCAL_OF =
    MemberName("androidx.compose.runtime", "staticCompositionLocalOf")
private val PROVIDABLE_COMPOSITION_LOCAL =
    ClassName("androidx.compose.runtime", "ProvidableCompositionLocal")
private val ROUNDED_CORNER_SHAPE =
    ClassName("androidx.compose.foundation.shape", "RoundedCornerShape")
private val SHAPE = ClassName("androidx.compose.ui.graphics", "Shape")
private val DP = MemberName("androidx.compose.ui.unit", "dp")
private val TEXT_STYLE = ClassName("androidx.compose.ui.text", "TextStyle")
private val FONT_FAMILY = ClassName("androidx.compose.ui.text.font", "FontFamily")
private val FONT_WEIGHT = ClassName("androidx.compose.ui.text.font", "FontWeight")
private val SP = MemberName("androidx.compose.ui.unit", "sp")
private val IS_SYSTEM_IN_DARK_THEME =
    MemberName("androidx.compose.foundation", "isSystemInDarkTheme")
private val MATERIAL_THEME = ClassName("androidx.compose.material3", "MaterialTheme")
private val COMPOSABLE = ClassName("androidx.compose.runtime", "Composable")
private val COMPOSITION_LOCAL_PROVIDER =
    MemberName("androidx.compose.runtime", "CompositionLocalProvider")

abstract class GenerateThemeTask : DefaultTask() {

    @get:InputFile
    abstract val themeJsonFile: RegularFileProperty

    @get:Input
    abstract val outputPackage: Property<String>

    @get:InputDirectory
    abstract val themeDirectory: DirectoryProperty

    @get:Input
    abstract val generateColors: Property<Boolean>

    @get:Input
    abstract val generateTypography: Property<Boolean>

    @get:Input
    abstract val generateShapes: Property<Boolean>

    @TaskAction
    fun generate() {
        val themeFile = themeJsonFile.get().asFile
        require(themeFile.exists())

        val theme = Gson().fromJson(themeFile.readText(), Theme::class.java)

        val outputDirectory = themeDirectory.get().asFile
        require(outputDirectory.exists())

        val packageName = outputPackage.get()

        if (generateColors.get()) {
            generateColorFile(theme, outputDirectory, packageName)
        }

        if (generateShapes.get()) {
            generateShapeFile(theme, outputDirectory, packageName)
        }

        if (generateTypography.get()) {
            generateTypographyFile(theme, outputDirectory, packageName)
        }

        generateThemeFile(outputDirectory, packageName)
    }

    private fun generateColorFile(theme: Theme, outputDir: File, packageName: String) {
        val lightColors = theme.colors.light
        val darkColors = theme.colors.dark
        val vonageColors = ClassName(packageName, "${VONAGE}Colors")

        val fileSpec = FileSpec.builder(packageName, "Color")
            .indent("    ")
            .addFileComment("Auto-generated from theme.json")
            .addAnnotation(
                AnnotationSpec.builder(ClassName("kotlin", "Suppress"))
                    .addMember("%S", "MagicNumber")
                    .build()
            )

        colorEntries(lightColors).forEach { (name, hex) ->
            fileSpec.addProperty(
                PropertySpec.builder("Light$name", COLOR)
                    .initializer("%T(0x${hex.hexToArgb()})", COLOR)
                    .build()
            )
        }

        colorEntries(darkColors).forEach { (name, hex) ->
            fileSpec.addProperty(
                PropertySpec.builder("Dark$name", COLOR)
                    .initializer("%T(0x${hex.hexToArgb()})", COLOR)
                    .build()
            )
        }

        fileSpec.addProperty(
            PropertySpec.builder("LightColorScheme", COLOR_SCHEME)
                .addModifiers(KModifier.INTERNAL)
                .initializer(buildMaterialColorSchemeBlock(LIGHT_COLOR_SCHEME_FN, "Light"))
                .build()
        )

        fileSpec.addProperty(
            PropertySpec.builder("DarkColorScheme", COLOR_SCHEME)
                .addModifiers(KModifier.INTERNAL)
                .initializer(buildMaterialColorSchemeBlock(DARK_COLOR_SCHEME_FN, "Dark"))
                .build()
        )

        val localInit = CodeBlock.builder()
            .beginControlFlow("%M", STATIC_COMPOSITION_LOCAL_OF)
            .add("%T(\n", vonageColors)
            .indent()
        VONAGE_COLOR_FIELDS.forEach { field ->
            localInit.add("$field = %T.Unspecified,\n", COLOR)
        }
        localInit.unindent()
            .add(")\n")
            .endControlFlow()

        fileSpec.addProperty(
            PropertySpec.builder(
                "Local${VONAGE}Colors",
                PROVIDABLE_COMPOSITION_LOCAL.parameterizedBy(vonageColors),
            )
                .addModifiers(KModifier.INTERNAL)
                .initializer(localInit.build())
                .build()
        )

        val constructor = FunSpec.constructorBuilder()
        VONAGE_COLOR_FIELDS.forEach { field ->
            constructor.addParameter(field, COLOR)
        }

        val classBuilder = TypeSpec.classBuilder("${VONAGE}Colors")
            .addModifiers(KModifier.DATA)
            .addAnnotation(IMMUTABLE)
            .primaryConstructor(constructor.build())

        VONAGE_COLOR_FIELDS.forEach { field ->
            classBuilder.addProperty(
                PropertySpec.builder(field, COLOR)
                    .initializer(field)
                    .build()
            )
        }

        fileSpec.addType(classBuilder.build())

        File(outputDir, "Color.kt").writeText(fileSpec.build().toString())
        logger.debug("Updated Color.kt")
    }

    private fun generateShapeFile(theme: Theme, outputDir: File, packageName: String) {
        val borderRadius = theme.borderRadius
        val vonageShapes = ClassName(packageName, "${VONAGE}Shapes")

        val shapeEntries = listOf(
            "shapeNone" to borderRadius.none,
            "shapeExtraSmall" to borderRadius.extraSmall,
            "shapeSmall" to borderRadius.small,
            "shapeMedium" to borderRadius.medium,
            "shapeLarge" to borderRadius.large,
            "shapeExtraLarge" to borderRadius.extraLarge,
        )

        val shapeFields = listOf(
            "none" to "shapeNone",
            "extraSmall" to "shapeExtraSmall",
            "small" to "shapeSmall",
            "medium" to "shapeMedium",
            "large" to "shapeLarge",
            "extraLarge" to "shapeExtraLarge",
        )

        val fileSpec = FileSpec.builder(packageName, "Shape")
            .indent("    ")
            .addFileComment("Auto-generated from theme.json")

        shapeEntries.forEach { (name, value) ->
            fileSpec.addProperty(
                PropertySpec.builder(name, ROUNDED_CORNER_SHAPE)
                    .addModifiers(KModifier.INTERNAL)
                    .initializer("%T(%L.%M)", ROUNDED_CORNER_SHAPE, value, DP)
                    .build()
            )
        }

        val localInit = CodeBlock.builder()
            .beginControlFlow("%M", STATIC_COMPOSITION_LOCAL_OF)
            .addStatement("%T()", vonageShapes)
            .endControlFlow()
            .build()

        fileSpec.addProperty(
            PropertySpec.builder(
                "Local${VONAGE}Shapes",
                PROVIDABLE_COMPOSITION_LOCAL.parameterizedBy(vonageShapes),
            )
                .addModifiers(KModifier.INTERNAL)
                .initializer(localInit)
                .build()
        )

        val constructor = FunSpec.constructorBuilder()
        shapeFields.forEach { (fieldName, defaultValue) ->
            constructor.addParameter(
                ParameterSpec.builder(fieldName, SHAPE)
                    .defaultValue(defaultValue)
                    .build()
            )
        }

        val classBuilder = TypeSpec.classBuilder("${VONAGE}Shapes")
            .addModifiers(KModifier.DATA)
            .addAnnotation(IMMUTABLE)
            .primaryConstructor(constructor.build())

        shapeFields.forEach { (fieldName, _) ->
            classBuilder.addProperty(
                PropertySpec.builder(fieldName, SHAPE)
                    .initializer(fieldName)
                    .build()
            )
        }

        fileSpec.addType(classBuilder.build())

        File(outputDir, "Shape.kt").writeText(fileSpec.build().toString())
        logger.debug("Updated Shape.kt")
    }

    private fun generateTypographyFile(theme: Theme, outputDir: File, packageName: String) {
        val mobile = theme.typography.mobile
        val vonageTypography = ClassName(packageName, "${VONAGE}Typography")

        val textStyleEntries = listOf(
            "headlineTextStyle" to mobile.headline,
            "subtitleTextStyle" to mobile.subtitle,
            "heading1TextStyle" to mobile.heading1,
            "heading2TextStyle" to mobile.heading2,
            "heading3TextStyle" to mobile.heading3,
            "heading4TextStyle" to mobile.heading4,
            "bodyExtendedTextStyle" to mobile.bodyExtended,
            "bodyExtendedSemiboldTextStyle" to mobile.bodyExtendedSemibold,
            "bodyBaseTextStyle" to mobile.bodyBase,
            "bodyBaseSemiboldTextStyle" to mobile.bodyBaseSemibold,
            "captionTextStyle" to mobile.caption,
            "captionSemiboldTextStyle" to mobile.captionSemibold,
        )

        val typographyFields = listOf(
            "headline" to "headlineTextStyle",
            "subtitle" to "subtitleTextStyle",
            "heading1" to "heading1TextStyle",
            "heading2" to "heading2TextStyle",
            "heading3" to "heading3TextStyle",
            "heading4" to "heading4TextStyle",
            "bodyExtended" to "bodyExtendedTextStyle",
            "bodyExtendedSemibold" to "bodyExtendedSemiboldTextStyle",
            "bodyBase" to "bodyBaseTextStyle",
            "bodyBaseSemibold" to "bodyBaseSemiboldTextStyle",
            "caption" to "captionTextStyle",
            "captionSemibold" to "captionSemiboldTextStyle",
        )

        val fileSpec = FileSpec.builder(packageName, "Typography")
            .indent("    ")
            .addFileComment("Auto-generated from theme.json")

        textStyleEntries.forEach { (name, style) ->
            fileSpec.addProperty(
                PropertySpec.builder(name, TEXT_STYLE)
                    .addModifiers(KModifier.INTERNAL)
                    .initializer(buildTextStyleBlock(style))
                    .build()
            )
        }

        val localInit = CodeBlock.builder()
            .beginControlFlow("%M", STATIC_COMPOSITION_LOCAL_OF)
            .addStatement("%T()", vonageTypography)
            .endControlFlow()
            .build()

        fileSpec.addProperty(
            PropertySpec.builder(
                "Local${VONAGE}Typography",
                PROVIDABLE_COMPOSITION_LOCAL.parameterizedBy(vonageTypography),
            )
                .addModifiers(KModifier.INTERNAL)
                .initializer(localInit)
                .build()
        )

        val constructor = FunSpec.constructorBuilder()
        typographyFields.forEach { (fieldName, defaultValue) ->
            constructor.addParameter(
                ParameterSpec.builder(fieldName, TEXT_STYLE)
                    .defaultValue(defaultValue)
                    .build()
            )
        }

        val classBuilder = TypeSpec.classBuilder("${VONAGE}Typography")
            .addModifiers(KModifier.DATA)
            .addAnnotation(IMMUTABLE)
            .primaryConstructor(constructor.build())

        typographyFields.forEach { (fieldName, _) ->
            classBuilder.addProperty(
                PropertySpec.builder(fieldName, TEXT_STYLE)
                    .initializer(fieldName)
                    .build()
            )
        }

        fileSpec.addType(classBuilder.build())

        File(outputDir, "Typography.kt").writeText(fileSpec.build().toString())
        logger.debug("Updated Typography.kt")
    }

    private fun buildTextStyleBlock(style: ThemeTextStyle): CodeBlock {
        val fontSize = style.fontSize.replace("px", "")
        val lineHeight = style.lineHeight.replace("px", "")
        return CodeBlock.builder()
            .add("%T(\n", TEXT_STYLE)
            .indent()
            .add("fontSize = %L.%M,\n", fontSize, SP)
            .add("fontWeight = %T(%L),\n", FONT_WEIGHT, style.fontWeight)
            .add("lineHeight = %L.%M,\n", lineHeight, SP)
            .add("fontFamily = %T.Default,\n", FONT_FAMILY)
            .unindent()
            .add(")")
            .build()
    }

    private fun generateThemeFile(outputDir: File, packageName: String) {
        val vonageColors = ClassName(packageName, "${VONAGE}Colors")
        val vonageTypography = ClassName(packageName, "${VONAGE}Typography")
        val vonageShapes = ClassName(packageName, "${VONAGE}Shapes")
        val vonageDimens = ClassName(packageName, "${VONAGE}Dimens")

        val composableLambda = LambdaTypeName.get(returnType = UNIT)
            .copy(annotations = listOf(AnnotationSpec.builder(COMPOSABLE).build()))

        val body = CodeBlock.builder()
        body.beginControlFlow("val colorScheme = when")
        body.addStatement("darkTheme -> DarkColorScheme")
        body.addStatement("else -> LightColorScheme")
        body.endControlFlow()
        body.add("\n")

        body.beginControlFlow("val extendedColors = if (darkTheme)")
        body.add("%T(\n", vonageColors)
        body.indent()
        DARK_COLOR_MAPPING.forEach { (field, colorRef) ->
            body.add("$field = $colorRef,\n")
        }
        body.unindent()
        body.add(")\n")
        body.nextControlFlow("else")
        body.add("%T(\n", vonageColors)
        body.indent()
        LIGHT_COLOR_MAPPING.forEach { (field, colorRef) ->
            body.add("$field = $colorRef,\n")
        }
        body.unindent()
        body.add(")\n")
        body.endControlFlow()
        body.add("\n")

        body.addStatement("val extendedTypography = %T()", vonageTypography)
        body.addStatement("val extendedShapes = %T()", vonageShapes)
        body.addStatement("val extendedDimens = %T()", vonageDimens)
        body.add("\n")

        body.add("%M(\n", COMPOSITION_LOCAL_PROVIDER)
        body.indent()
        body.add("Local${VONAGE}Colors provides extendedColors,\n")
        body.add("Local${VONAGE}Typography provides extendedTypography,\n")
        body.add("Local${VONAGE}Shapes provides extendedShapes,\n")
        body.add("Local${VONAGE}Dimens provides extendedDimens,\n")
        body.unindent()
        body.beginControlFlow(")")
        body.add("%T(\n", MATERIAL_THEME)
        body.indent()
        body.add("colorScheme = colorScheme,\n")
        body.add("content = content,\n")
        body.unindent()
        body.add(")\n")
        body.endControlFlow()

        val themeFunction = FunSpec.builder("${VONAGE}VideoTheme")
            .addAnnotation(COMPOSABLE)
            .addParameter(
                ParameterSpec.builder("darkTheme", Boolean::class)
                    .defaultValue("%M()", IS_SYSTEM_IN_DARK_THEME)
                    .build()
            )
            .addParameter("content", composableLambda)
            .addCode(body.build())
            .build()

        val themeObject = TypeSpec.objectBuilder("${VONAGE}VideoTheme")
        listOf(
            Triple("colors", vonageColors, "Local${VONAGE}Colors"),
            Triple("typography", vonageTypography, "Local${VONAGE}Typography"),
            Triple("shapes", vonageShapes, "Local${VONAGE}Shapes"),
            Triple("dimens", vonageDimens, "Local${VONAGE}Dimens"),
        ).forEach { (name, type, localName) ->
            themeObject.addProperty(
                PropertySpec.builder(name, type)
                    .getter(
                        FunSpec.getterBuilder()
                            .addAnnotation(COMPOSABLE)
                            .addStatement("return $localName.current")
                            .build()
                    )
                    .build()
            )
        }

        val fileSpec = FileSpec.builder(packageName, "Theme")
            .indent("    ")
            .addFileComment("Auto-generated from theme.json")
            .addFunction(themeFunction)
            .addType(themeObject.build())
            .build()

        File(outputDir, "Theme.kt").writeText(fileSpec.toString())
        logger.debug("Updated Theme.kt")
    }

    /**
     * Converts a schema hex color to the ARGB hex string expected by [Color]'s Long constructor.
     * The schema uses `#RRGGBB` (opaque) or `#RRGGBBAA` (alpha channel last); Compose's `Color`
     * expects `AARRGGBB`, so an 8-digit input needs its alpha byte moved to the front.
     */
    private fun String.hexToArgb(): String {
        val cleanHex = removePrefix("#")
        return when (cleanHex.length) {
            COLOR_LENGTH -> "FF$cleanHex"
            COLOR_LENGTH_WITH_ALPHA -> {
                val rgb = cleanHex.substring(0, COLOR_LENGTH)
                val alpha = cleanHex.substring(COLOR_LENGTH)
                "$alpha$rgb"
            }
            else -> cleanHex
        }
    }

    private fun colorEntries(colors: ColorScheme): List<Pair<String, String>> = listOf(
        "Primary" to colors.primary,
        "PrimaryHover" to colors.primaryHover,
        "OnPrimary" to colors.onPrimary,
        "Secondary" to colors.secondary,
        "OnSecondary" to colors.onSecondary,
        "Tertiary" to colors.tertiary,
        "OnTertiary" to colors.onTertiary,
        "Background" to colors.background,
        "OnBackground" to colors.onBackground,
        "Surface" to colors.surface,
        "OnSurface" to colors.onSurface,
        "Error" to colors.error,
        "OnError" to colors.onError,
        "ErrorHover" to colors.errorHover,
        "Warning" to colors.warning,
        "OnWarning" to colors.onWarning,
        "WarningHover" to colors.warningHover,
        "Success" to colors.success,
        "OnSuccess" to colors.onSuccess,
        "SuccessHover" to colors.successHover,
        "Border" to colors.border,
        "Disabled" to colors.disabled,
        "TextDisabled" to colors.textDisabled,
    )

    private fun buildMaterialColorSchemeBlock(schemeFn: MemberName, prefix: String): CodeBlock =
        CodeBlock.builder()
            .add("%M(\n", schemeFn)
            .indent()
            .add("primary = ${prefix}Primary,\n")
            .add("onPrimary = ${prefix}OnPrimary,\n")
            .add("secondary = ${prefix}Secondary,\n")
            .add("onSecondary = ${prefix}OnSecondary,\n")
            .add("tertiary = ${prefix}Tertiary,\n")
            .add("onTertiary = ${prefix}OnTertiary,\n")
            .add("background = ${prefix}Background,\n")
            .add("onBackground = ${prefix}OnBackground,\n")
            .add("surface = ${prefix}Surface,\n")
            .add("onSurface = ${prefix}OnSurface,\n")
            .add("error = ${prefix}Error,\n")
            .add("onError = ${prefix}OnError,\n")
            .add("outline = ${prefix}Border,\n")
            .unindent()
            .add(")")
            .build()

    companion object {
        private val VONAGE_COLOR_FIELDS = listOf(
            "primary",
            "onPrimary",
            "primaryHover",
            "secondary",
            "onSecondary",
            "tertiary",
            "onTertiary",
            "accent",
            "onAccent",
            "background",
            "onBackground",
            "surface",
            "onSurface",
            "error",
            "onError",
            "errorHover",
            "warning",
            "onWarning",
            "warningHover",
            "success",
            "onSuccess",
            "successHover",
            "border",
            "disabled",
            "textDisabled",
            "textPrimary",
            "textSecondary",
            "textTertiary",
        )

        private fun colorMapping(
            prefix: String,
            accentColor: String,
            onAccentColor: String,
        ): List<Pair<String, String>> = listOf(
            "primary" to "${prefix}Primary",
            "onPrimary" to "${prefix}OnPrimary",
            "primaryHover" to "${prefix}PrimaryHover",
            "secondary" to "${prefix}Secondary",
            "onSecondary" to "${prefix}OnSecondary",
            "tertiary" to "${prefix}Tertiary",
            "onTertiary" to "${prefix}OnTertiary",
            "accent" to "$prefix$accentColor",
            "onAccent" to "$prefix$onAccentColor",
            "background" to "${prefix}Background",
            "onBackground" to "${prefix}OnBackground",
            "surface" to "${prefix}Surface",
            "onSurface" to "${prefix}OnSurface",
            "error" to "${prefix}Error",
            "onError" to "${prefix}OnError",
            "errorHover" to "${prefix}ErrorHover",
            "warning" to "${prefix}Warning",
            "onWarning" to "${prefix}OnWarning",
            "warningHover" to "${prefix}WarningHover",
            "success" to "${prefix}Success",
            "onSuccess" to "${prefix}OnSuccess",
            "successHover" to "${prefix}SuccessHover",
            "border" to "${prefix}Border",
            "disabled" to "${prefix}Disabled",
            "textDisabled" to "${prefix}TextDisabled",
            "textPrimary" to "${prefix}Primary",
            "textSecondary" to "${prefix}Secondary",
            "textTertiary" to "${prefix}Tertiary",
        )

        private val DARK_COLOR_MAPPING = colorMapping(
            prefix = "Dark",
            accentColor = "Primary",
            onAccentColor = "OnPrimary",
        )

        private val LIGHT_COLOR_MAPPING = colorMapping(
            prefix = "Light",
            accentColor = "Secondary",
            onAccentColor = "OnSecondary",
        )
    }
}
