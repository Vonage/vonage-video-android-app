package com.vonage.android.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun VonageVideoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Extended colors based on theme.json
    val extendedColors = if (darkTheme) {
        VonageColors(
            primary = DarkPrimary,
            onPrimary = CanvasText,
            primaryHover = DarkPrimaryHover,
            secondary = Canvas,
            onSecondary = CanvasText,
            tertiary = Neutral300,
            onTertiary = CanvasText,
            accent = DarkPrimary,
            onAccent = CanvasText,
            background = DarkBackground,
            onBackground = Neutral300,
            surface = DarkSurface,
            onSurface = Neutral400,
            error = DarkError,
            onError = CanvasText,
            errorHover = DarkErrorHover,
            warning = DarkWarning,
            onWarning = CanvasText,
            warningHover = DarkWarningHover,
            success = DarkSuccess,
            onSuccess = CanvasText,
            successHover = DarkSuccessHover,
            border = DarkBorder,
            disabled = DarkBorder,
            textDisabled = DarkTextDisabled,
            textPrimary = DarkPrimary,
            textSecondary = Canvas,
            textTertiary = Neutral300,
        )
    } else {
        VonageColors(
            primary = Cta500,
            onPrimary = Canvas,
            primaryHover = Cta600,
            secondary = CanvasText,
            onSecondary = Canvas,
            tertiary = Accent500,
            onTertiary = Canvas,
            accent = CanvasText,
            onAccent = Canvas,
            background = Cta50,
            onBackground = Neutral500,
            surface = Canvas,
            onSurface = Neutral400,
            error = Alert500,
            onError = Canvas,
            errorHover = Alert600,
            warning = Warning500,
            onWarning = Canvas,
            warningHover = Warning600,
            success = Success500,
            onSuccess = Canvas,
            successHover = Success600,
            border = Neutral100,
            disabled = Neutral100,
            textDisabled = Neutral300,
            textPrimary = Cta500,
            textSecondary = CanvasText,
            textTertiary = Accent500,
        )
    }

    val extendedTypography = VonageTypography()
    val extendedShapes = VonageShapes()
    val extendedDimens = VonageDimens()

    CompositionLocalProvider(
        LocalVonageColors provides extendedColors,
        LocalVonageTypography provides extendedTypography,
        LocalVonageShapes provides extendedShapes,
        LocalVonageDimens provides extendedDimens,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

object VonageVideoTheme {
    val colors: VonageColors
        @Composable
        get() = LocalVonageColors.current
    val typography: VonageTypography
        @Composable
        get() = LocalVonageTypography.current
    val shapes: VonageShapes
        @Composable
        get() = LocalVonageShapes.current
    val dimens: VonageDimens
        @Composable
        get() = LocalVonageDimens.current
}
