@file:Suppress("LongMethod")

package com.vonage.gradle.tasks

import com.google.gson.Gson
import com.vonage.gradle.model.ThemeConfig
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

private const val COLOR_LENGTH = 6

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

        val themeConfig = Gson().fromJson(themeFile.readText(), ThemeConfig::class.java)
        val theme = themeConfig.themes["vonage"] ?: throw IllegalArgumentException("Vonage theme not found")

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

    private fun generateColorFile(theme: com.vonage.gradle.model.Theme, outputDir: File, packageName: String) {
        // Helper function to convert hex color to ARGB format with alpha channel
        fun hexToArgb(hex: String): String {
            val cleanHex = hex.removePrefix("#")
            return if (cleanHex.length == COLOR_LENGTH) {
                "FF$cleanHex" // Add full alpha channel
            } else {
                cleanHex // Already has alpha channel
            }
        }

        val content = buildString {
            appendLine("@file:Suppress(\"MagicNumber\")")
            appendLine()
            appendLine("package $packageName")
            appendLine()
            appendLine("import androidx.compose.material3.darkColorScheme")
            appendLine("import androidx.compose.material3.lightColorScheme")
            appendLine("import androidx.compose.runtime.Immutable")
            appendLine("import androidx.compose.runtime.staticCompositionLocalOf")
            appendLine("import androidx.compose.ui.graphics.Color")
            appendLine()
            appendLine("// Auto-generated from theme.json")
            appendLine()

            // Generate color constants for light theme
            appendLine("// Light theme colors")
            val lightColors = theme.colors.light
            appendLine("val LightPrimary = Color(0x${hexToArgb(lightColors.primary)})")
            appendLine("val LightPrimaryHover = Color(0x${hexToArgb(lightColors.primaryHover)})")
            appendLine("val LightOnPrimary = Color(0x${hexToArgb(lightColors.onPrimary)})")
            appendLine("val LightSecondary = Color(0x${hexToArgb(lightColors.secondary)})")
            appendLine("val LightOnSecondary = Color(0x${hexToArgb(lightColors.onSecondary)})")
            appendLine("val LightTertiary = Color(0x${hexToArgb(lightColors.tertiary)})")
            appendLine("val LightOnTertiary = Color(0x${hexToArgb(lightColors.onTertiary)})")
            appendLine("val LightBackground = Color(0x${hexToArgb(lightColors.background)})")
            appendLine("val LightOnBackground = Color(0x${hexToArgb(lightColors.onBackground)})")
            appendLine("val LightSurface = Color(0x${hexToArgb(lightColors.surface)})")
            appendLine("val LightOnSurface = Color(0x${hexToArgb(lightColors.onSurface)})")
            appendLine("val LightError = Color(0x${hexToArgb(lightColors.error)})")
            appendLine("val LightOnError = Color(0x${hexToArgb(lightColors.onError)})")
            appendLine("val LightErrorHover = Color(0x${hexToArgb(lightColors.errorHover)})")
            appendLine("val LightWarning = Color(0x${hexToArgb(lightColors.warning)})")
            appendLine("val LightOnWarning = Color(0x${hexToArgb(lightColors.onWarning)})")
            appendLine("val LightWarningHover = Color(0x${hexToArgb(lightColors.warningHover)})")
            appendLine("val LightSuccess = Color(0x${hexToArgb(lightColors.success)})")
            appendLine("val LightOnSuccess = Color(0x${hexToArgb(lightColors.onSuccess)})")
            appendLine("val LightSuccessHover = Color(0x${hexToArgb(lightColors.successHover)})")
            appendLine("val LightBorder = Color(0x${hexToArgb(lightColors.border)})")
            appendLine("val LightDisabled = Color(0x${hexToArgb(lightColors.disabled)})")
            appendLine("val LightTextDisabled = Color(0x${hexToArgb(lightColors.textDisabled)})")
            appendLine()

            // Generate color constants for dark theme
            appendLine("// Dark theme colors")
            val darkColors = theme.colors.dark
            appendLine("val DarkPrimary = Color(0x${hexToArgb(darkColors.primary)})")
            appendLine("val DarkPrimaryHover = Color(0x${hexToArgb(darkColors.primaryHover)})")
            appendLine("val DarkOnPrimary = Color(0x${hexToArgb(darkColors.onPrimary)})")
            appendLine("val DarkSecondary = Color(0x${hexToArgb(darkColors.secondary)})")
            appendLine("val DarkOnSecondary = Color(0x${hexToArgb(darkColors.onSecondary)})")
            appendLine("val DarkTertiary = Color(0x${hexToArgb(darkColors.tertiary)})")
            appendLine("val DarkOnTertiary = Color(0x${hexToArgb(darkColors.onTertiary)})")
            appendLine("val DarkBackground = Color(0x${hexToArgb(darkColors.background)})")
            appendLine("val DarkOnBackground = Color(0x${hexToArgb(darkColors.onBackground)})")
            appendLine("val DarkSurface = Color(0x${hexToArgb(darkColors.surface)})")
            appendLine("val DarkOnSurface = Color(0x${hexToArgb(darkColors.onSurface)})")
            appendLine("val DarkError = Color(0x${hexToArgb(darkColors.error)})")
            appendLine("val DarkOnError = Color(0x${hexToArgb(darkColors.onError)})")
            appendLine("val DarkErrorHover = Color(0x${hexToArgb(darkColors.errorHover)})")
            appendLine("val DarkWarning = Color(0x${hexToArgb(darkColors.warning)})")
            appendLine("val DarkOnWarning = Color(0x${hexToArgb(darkColors.onWarning)})")
            appendLine("val DarkWarningHover = Color(0x${hexToArgb(darkColors.warningHover)})")
            appendLine("val DarkSuccess = Color(0x${hexToArgb(darkColors.success)})")
            appendLine("val DarkOnSuccess = Color(0x${hexToArgb(darkColors.onSuccess)})")
            appendLine("val DarkSuccessHover = Color(0x${hexToArgb(darkColors.successHover)})")
            appendLine("val DarkBorder = Color(0x${hexToArgb(darkColors.border)})")
            appendLine("val DarkDisabled = Color(0x${hexToArgb(darkColors.disabled)})")
            appendLine("val DarkTextDisabled = Color(0x${hexToArgb(darkColors.textDisabled)})")
            appendLine()

            // Generate Material3 ColorSchemes
            appendLine("internal val LightColorScheme = lightColorScheme(")
            appendLine("    primary = LightPrimary,")
            appendLine("    onPrimary = LightOnPrimary,")
            appendLine("    secondary = LightSecondary,")
            appendLine("    onSecondary = LightOnSecondary,")
            appendLine("    tertiary = LightTertiary,")
            appendLine("    onTertiary = LightOnTertiary,")
            appendLine("    background = LightBackground,")
            appendLine("    onBackground = LightOnBackground,")
            appendLine("    surface = LightSurface,")
            appendLine("    onSurface = LightOnSurface,")
            appendLine("    error = LightError,")
            appendLine("    onError = LightOnError,")
            appendLine("    outline = LightBorder,")
            appendLine(")")
            appendLine()

            appendLine("internal val DarkColorScheme = darkColorScheme(")
            appendLine("    primary = DarkPrimary,")
            appendLine("    onPrimary = DarkOnPrimary,")
            appendLine("    secondary = DarkSecondary,")
            appendLine("    onSecondary = DarkOnSecondary,")
            appendLine("    tertiary = DarkTertiary,")
            appendLine("    onTertiary = DarkOnTertiary,")
            appendLine("    background = DarkBackground,")
            appendLine("    onBackground = DarkOnBackground,")
            appendLine("    surface = DarkSurface,")
            appendLine("    onSurface = DarkOnSurface,")
            appendLine("    error = DarkError,")
            appendLine("    onError = DarkOnError,")
            appendLine("    outline = DarkBorder,")
            appendLine(")")
            appendLine()

            // Generate VonageColors data class
            appendLine("internal val LocalVonageColors = staticCompositionLocalOf {")
            appendLine("    VonageColors(")
            appendLine("        primary = Color.Unspecified,")
            appendLine("        onPrimary = Color.Unspecified,")
            appendLine("        primaryHover = Color.Unspecified,")
            appendLine("        secondary = Color.Unspecified,")
            appendLine("        onSecondary = Color.Unspecified,")
            appendLine("        tertiary = Color.Unspecified,")
            appendLine("        onTertiary = Color.Unspecified,")
            appendLine("        accent = Color.Unspecified,")
            appendLine("        onAccent = Color.Unspecified,")
            appendLine("        background = Color.Unspecified,")
            appendLine("        onBackground = Color.Unspecified,")
            appendLine("        surface = Color.Unspecified,")
            appendLine("        onSurface = Color.Unspecified,")
            appendLine("        error = Color.Unspecified,")
            appendLine("        onError = Color.Unspecified,")
            appendLine("        errorHover = Color.Unspecified,")
            appendLine("        warning = Color.Unspecified,")
            appendLine("        onWarning = Color.Unspecified,")
            appendLine("        warningHover = Color.Unspecified,")
            appendLine("        success = Color.Unspecified,")
            appendLine("        onSuccess = Color.Unspecified,")
            appendLine("        successHover = Color.Unspecified,")
            appendLine("        border = Color.Unspecified,")
            appendLine("        disabled = Color.Unspecified,")
            appendLine("        textDisabled = Color.Unspecified,")
            appendLine("        textPrimary = Color.Unspecified,")
            appendLine("        textSecondary = Color.Unspecified,")
            appendLine("        textTertiary = Color.Unspecified,")
            appendLine("    )")
            appendLine("}")
            appendLine()

            appendLine("@Immutable")
            appendLine("data class VonageColors(")
            appendLine("    val primary: Color,")
            appendLine("    val onPrimary: Color,")
            appendLine("    val primaryHover: Color,")
            appendLine("    val secondary: Color,")
            appendLine("    val onSecondary: Color,")
            appendLine("    val tertiary: Color,")
            appendLine("    val onTertiary: Color,")
            appendLine("    val accent: Color,")
            appendLine("    val onAccent: Color,")
            appendLine("    val background: Color,")
            appendLine("    val onBackground: Color,")
            appendLine("    val surface: Color,")
            appendLine("    val onSurface: Color,")
            appendLine("    val error: Color,")
            appendLine("    val onError: Color,")
            appendLine("    val errorHover: Color,")
            appendLine("    val warning: Color,")
            appendLine("    val onWarning: Color,")
            appendLine("    val warningHover: Color,")
            appendLine("    val success: Color,")
            appendLine("    val onSuccess: Color,")
            appendLine("    val successHover: Color,")
            appendLine("    val border: Color,")
            appendLine("    val disabled: Color,")
            appendLine("    val textDisabled: Color,")
            appendLine("    val textPrimary: Color,")
            appendLine("    val textSecondary: Color,")
            appendLine("    val textTertiary: Color,")
            appendLine(")")
        }

        File(outputDir, "Color.kt").writeText(content)
        println("Updated Color.kt")
    }

    private fun generateShapeFile(theme: com.vonage.gradle.model.Theme, outputDir: File, packageName: String) {
        val content = buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("import androidx.compose.foundation.shape.RoundedCornerShape")
            appendLine("import androidx.compose.runtime.Immutable")
            appendLine("import androidx.compose.runtime.staticCompositionLocalOf")
            appendLine("import androidx.compose.ui.graphics.Shape")
            appendLine("import androidx.compose.ui.unit.dp")
            appendLine()
            appendLine("// Auto-generated from theme.json")
            appendLine()

            val borderRadius = theme.borderRadius
            appendLine("internal val shapeNone = RoundedCornerShape(${borderRadius.none}.dp)")
            appendLine("internal val shapeExtraSmall = RoundedCornerShape(${borderRadius.extraSmall}.dp)")
            appendLine("internal val shapeSmall = RoundedCornerShape(${borderRadius.small}.dp)")
            appendLine("internal val shapeMedium = RoundedCornerShape(${borderRadius.medium}.dp)")
            appendLine("internal val shapeLarge = RoundedCornerShape(${borderRadius.large}.dp)")
            appendLine("internal val shapeExtraLarge = RoundedCornerShape(${borderRadius.extraLarge}.dp)")
            appendLine()

            appendLine("internal val LocalVonageShapes = staticCompositionLocalOf {")
            appendLine("    VonageShapes()")
            appendLine("}")
            appendLine()

            appendLine("@Immutable")
            appendLine("data class VonageShapes(")
            appendLine("    val none: Shape = shapeNone,")
            appendLine("    val extraSmall: Shape = shapeExtraSmall,")
            appendLine("    val small: Shape = shapeSmall,")
            appendLine("    val medium: Shape = shapeMedium,")
            appendLine("    val large: Shape = shapeLarge,")
            appendLine("    val extraLarge: Shape = shapeExtraLarge,")
            appendLine(")")
        }

        File(outputDir, "Shape.kt").writeText(content)
        println("Updated Shape.kt")
    }

    private fun generateTypographyFile(theme: com.vonage.gradle.model.Theme, outputDir: File, packageName: String) {
        val mobile = theme.typography.mobile

        val content = buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("import androidx.compose.runtime.Immutable")
            appendLine("import androidx.compose.runtime.staticCompositionLocalOf")
            appendLine("import androidx.compose.ui.text.TextStyle")
            appendLine("import androidx.compose.ui.text.font.FontFamily")
            appendLine("import androidx.compose.ui.text.font.FontWeight")
            appendLine("import androidx.compose.ui.unit.sp")
            appendLine()
            appendLine("// Auto-generated from theme.json")
            appendLine()

            fun parseSize(size: String) = size.replace("px", "")

            appendLine("internal val headlineTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.headline.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.headline.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.headline.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val subtitleTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.subtitle.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.subtitle.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.subtitle.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val heading1TextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.heading1.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.heading1.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.heading1.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val heading2TextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.heading2.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.heading2.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.heading2.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val heading3TextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.heading3.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.heading3.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.heading3.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val heading4TextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.heading4.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.heading4.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.heading4.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val bodyExtendedTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.bodyExtended.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.bodyExtended.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.bodyExtended.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val bodyExtendedSemiboldTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.bodyExtendedSemibold.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.bodyExtendedSemibold.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.bodyExtendedSemibold.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val bodyBaseTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.bodyBase.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.bodyBase.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.bodyBase.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val bodyBaseSemiboldTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.bodyBaseSemibold.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.bodyBaseSemibold.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.bodyBaseSemibold.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val captionTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.caption.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.caption.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.caption.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val captionSemiboldTextStyle = TextStyle(")
            appendLine("    fontSize = ${parseSize(mobile.captionSemibold.fontSize)}.sp,")
            appendLine("    fontWeight = FontWeight(${mobile.captionSemibold.fontWeight}),")
            appendLine("    lineHeight = ${parseSize(mobile.captionSemibold.lineHeight)}.sp,")
            appendLine("    fontFamily = FontFamily.Default,")
            appendLine(")")
            appendLine()

            appendLine("internal val LocalVonageTypography = staticCompositionLocalOf {")
            appendLine("    VonageTypography()")
            appendLine("}")
            appendLine()

            appendLine("@Immutable")
            appendLine("data class VonageTypography(")
            appendLine("    val headline: TextStyle = headlineTextStyle,")
            appendLine("    val subtitle: TextStyle = subtitleTextStyle,")
            appendLine("    val heading1: TextStyle = heading1TextStyle,")
            appendLine("    val heading2: TextStyle = heading2TextStyle,")
            appendLine("    val heading3: TextStyle = heading3TextStyle,")
            appendLine("    val heading4: TextStyle = heading4TextStyle,")
            appendLine("    val bodyExtended: TextStyle = bodyExtendedTextStyle,")
            appendLine("    val bodyExtendedSemibold: TextStyle = bodyExtendedSemiboldTextStyle,")
            appendLine("    val bodyBase: TextStyle = bodyBaseTextStyle,")
            appendLine("    val bodyBaseSemibold: TextStyle = bodyBaseSemiboldTextStyle,")
            appendLine("    val caption: TextStyle = captionTextStyle,")
            appendLine("    val captionSemibold: TextStyle = captionSemiboldTextStyle,")
            appendLine(")")
        }

        File(outputDir, "Typography.kt").writeText(content)
        println("Updated Typography.kt")
    }

    private fun generateThemeFile(outputDir: File, packageName: String) {
        val content = buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("import androidx.compose.foundation.isSystemInDarkTheme")
            appendLine("import androidx.compose.material3.MaterialTheme")
            appendLine("import androidx.compose.runtime.Composable")
            appendLine("import androidx.compose.runtime.CompositionLocalProvider")
            appendLine()
            appendLine("// Auto-generated from theme.json")
            appendLine()

            appendLine("@Composable")
            appendLine("fun VonageVideoTheme(")
            appendLine("    darkTheme: Boolean = isSystemInDarkTheme(),")
            appendLine("    content: @Composable () -> Unit")
            appendLine(") {")
            appendLine("    val colorScheme = when {")
            appendLine("        darkTheme -> DarkColorScheme")
            appendLine("        else -> LightColorScheme")
            appendLine("    }")
            appendLine()
            appendLine("    val extendedColors = if (darkTheme) {")
            appendLine("        VonageColors(")
            appendLine("            primary = DarkPrimary,")
            appendLine("            onPrimary = DarkOnPrimary,")
            appendLine("            primaryHover = DarkPrimaryHover,")
            appendLine("            secondary = DarkSecondary,")
            appendLine("            onSecondary = DarkOnSecondary,")
            appendLine("            tertiary = DarkTertiary,")
            appendLine("            onTertiary = DarkOnTertiary,")
            appendLine("            accent = DarkPrimary,")
            appendLine("            onAccent = DarkOnPrimary,")
            appendLine("            background = DarkBackground,")
            appendLine("            onBackground = DarkOnBackground,")
            appendLine("            surface = DarkSurface,")
            appendLine("            onSurface = DarkOnSurface,")
            appendLine("            error = DarkError,")
            appendLine("            onError = DarkOnError,")
            appendLine("            errorHover = DarkErrorHover,")
            appendLine("            warning = DarkWarning,")
            appendLine("            onWarning = DarkOnWarning,")
            appendLine("            warningHover = DarkWarningHover,")
            appendLine("            success = DarkSuccess,")
            appendLine("            onSuccess = DarkOnSuccess,")
            appendLine("            successHover = DarkSuccessHover,")
            appendLine("            border = DarkBorder,")
            appendLine("            disabled = DarkDisabled,")
            appendLine("            textDisabled = DarkTextDisabled,")
            appendLine("            textPrimary = DarkPrimary,")
            appendLine("            textSecondary = DarkSecondary,")
            appendLine("            textTertiary = DarkTertiary,")
            appendLine("        )")
            appendLine("    } else {")
            appendLine("        VonageColors(")
            appendLine("            primary = LightPrimary,")
            appendLine("            onPrimary = LightOnPrimary,")
            appendLine("            primaryHover = LightPrimaryHover,")
            appendLine("            secondary = LightSecondary,")
            appendLine("            onSecondary = LightOnSecondary,")
            appendLine("            tertiary = LightTertiary,")
            appendLine("            onTertiary = LightOnTertiary,")
            appendLine("            accent = LightSecondary,")
            appendLine("            onAccent = LightOnSecondary,")
            appendLine("            background = LightBackground,")
            appendLine("            onBackground = LightOnBackground,")
            appendLine("            surface = LightSurface,")
            appendLine("            onSurface = LightOnSurface,")
            appendLine("            error = LightError,")
            appendLine("            onError = LightOnError,")
            appendLine("            errorHover = LightErrorHover,")
            appendLine("            warning = LightWarning,")
            appendLine("            onWarning = LightOnWarning,")
            appendLine("            warningHover = LightWarningHover,")
            appendLine("            success = LightSuccess,")
            appendLine("            onSuccess = LightOnSuccess,")
            appendLine("            successHover = LightSuccessHover,")
            appendLine("            border = LightBorder,")
            appendLine("            disabled = LightDisabled,")
            appendLine("            textDisabled = LightTextDisabled,")
            appendLine("            textPrimary = LightPrimary,")
            appendLine("            textSecondary = LightSecondary,")
            appendLine("            textTertiary = LightTertiary,")
            appendLine("        )")
            appendLine("    }")
            appendLine()
            appendLine("    val extendedTypography = VonageTypography()")
            appendLine("    val extendedShapes = VonageShapes()")
            appendLine("    val extendedDimens = VonageDimens()")
            appendLine()
            appendLine("    CompositionLocalProvider(")
            appendLine("        LocalVonageColors provides extendedColors,")
            appendLine("        LocalVonageTypography provides extendedTypography,")
            appendLine("        LocalVonageShapes provides extendedShapes,")
            appendLine("        LocalVonageDimens provides extendedDimens,")
            appendLine("    ) {")
            appendLine("        MaterialTheme(")
            appendLine("            colorScheme = colorScheme,")
            appendLine("            content = content,")
            appendLine("        )")
            appendLine("    }")
            appendLine("}")
            appendLine()

            appendLine("object VonageVideoTheme {")
            appendLine("    val colors: VonageColors")
            appendLine("        @Composable")
            appendLine("        get() = LocalVonageColors.current")
            appendLine("    val typography: VonageTypography")
            appendLine("        @Composable")
            appendLine("        get() = LocalVonageTypography.current")
            appendLine("    val shapes: VonageShapes")
            appendLine("        @Composable")
            appendLine("        get() = LocalVonageShapes.current")
            appendLine("    val dimens: VonageDimens")
            appendLine("        @Composable")
            appendLine("        get() = LocalVonageDimens.current")
            appendLine("}")
        }

        File(outputDir, "Theme.kt").writeText(content)
        println("Updated Theme.kt")
    }
}
