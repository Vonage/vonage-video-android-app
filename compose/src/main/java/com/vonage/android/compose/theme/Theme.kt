package com.vonage.android.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// Auto-generated from theme.json

@Composable
fun VonageVideoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) {
        VonageColors(
            primary = DarkPrimary,
            onPrimary = DarkOnPrimary,
            primaryHover = DarkPrimaryHover,
            secondary = DarkSecondary,
            onSecondary = DarkOnSecondary,
            tertiary = DarkTertiary,
            onTertiary = DarkOnTertiary,
            accent = DarkPrimary,
            onAccent = DarkOnPrimary,
            background = DarkBackground,
            onBackground = DarkOnBackground,
            surface = DarkSurface,
            onSurface = DarkOnSurface,
            error = DarkError,
            onError = DarkOnError,
            errorHover = DarkErrorHover,
            warning = DarkWarning,
            onWarning = DarkOnWarning,
            warningHover = DarkWarningHover,
            success = DarkSuccess,
            onSuccess = DarkOnSuccess,
            successHover = DarkSuccessHover,
            border = DarkBorder,
            disabled = DarkDisabled,
            textDisabled = DarkTextDisabled,
            textPrimary = DarkPrimary,
            textSecondary = DarkSecondary,
            textTertiary = DarkTertiary,
        )
    } else {
        VonageColors(
            primary = LightPrimary,
            onPrimary = LightOnPrimary,
            primaryHover = LightPrimaryHover,
            secondary = LightSecondary,
            onSecondary = LightOnSecondary,
            tertiary = LightTertiary,
            onTertiary = LightOnTertiary,
            accent = LightSecondary,
            onAccent = LightOnSecondary,
            background = LightBackground,
            onBackground = LightOnBackground,
            surface = LightSurface,
            onSurface = LightOnSurface,
            error = LightError,
            onError = LightOnError,
            errorHover = LightErrorHover,
            warning = LightWarning,
            onWarning = LightOnWarning,
            warningHover = LightWarningHover,
            success = LightSuccess,
            onSuccess = LightOnSuccess,
            successHover = LightSuccessHover,
            border = LightBorder,
            disabled = LightDisabled,
            textDisabled = LightTextDisabled,
            textPrimary = LightPrimary,
            textSecondary = LightSecondary,
            textTertiary = LightTertiary,
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
