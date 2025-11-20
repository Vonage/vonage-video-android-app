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

    val extendedColors = VonageColors(
        primary = colorScheme.primary,
        onPrimary = colorScheme.onPrimary,
        primaryHover = colorScheme.primary,
        secondary = colorScheme.secondary,
        onSecondary = colorScheme.onSecondary,
        tertiary = colorScheme.tertiary,
        onTertiary = colorScheme.onTertiary,
        accent = colorScheme.primary,
        onAccent = colorScheme.onPrimary,
        background = colorScheme.background,
        onBackground = colorScheme.onBackground,
        surface = colorScheme.surface,
        onSurface = colorScheme.onSurface,
        error = colorScheme.error,
        onError = colorScheme.onError,
        errorHover = colorScheme.onError,
        warning = colorScheme.primary,
        onWarning = colorScheme.primary,
        warningHover = colorScheme.primary,
        success = colorScheme.primary,
        onSuccess = colorScheme.primary,
        successHover = colorScheme.primary,
        border = colorScheme.primary,
        disabled = colorScheme.primary,
        textDisabled = colorScheme.primary,
        textPrimary = colorScheme.primary,
        textSecondary = colorScheme.secondary,
        textTertiary = colorScheme.tertiary,
    )

    val extendedTypography = VonageTypography()

    val extendedShapes = VonageShapes()

    CompositionLocalProvider(
        LocalVonageColors provides extendedColors,
        LocalVonageTypography provides extendedTypography,
        LocalVonageShapes provides extendedShapes,
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
}
