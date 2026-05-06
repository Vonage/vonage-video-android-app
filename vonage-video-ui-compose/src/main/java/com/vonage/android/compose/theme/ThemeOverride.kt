package com.vonage.android.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * [VonageVideoTheme] overload that applies a fully custom [VonageColors] for light and dark modes.
 *
 * Use this when you need to override the default Vonage palette (e.g. from `MeetingRoomTheme`).
 * Typography, shapes, and dimensions are unchanged.
 *
 * @param lightColors Color palette for light mode.
 * @param darkColors  Color palette for dark mode.
 * @param darkTheme   When `true` the dark palette is applied. Defaults to [isSystemInDarkTheme].
 * @param content     The composable content to be themed.
 */
@Composable
fun VonageVideoTheme(
    lightColors: VonageColors,
    darkColors: VonageColors,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val extendedColors = if (darkTheme) darkColors else lightColors

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = darkColors.primary,
            onPrimary = darkColors.onPrimary,
            secondary = darkColors.secondary,
            onSecondary = darkColors.onSecondary,
            tertiary = darkColors.tertiary,
            onTertiary = darkColors.onTertiary,
            background = darkColors.background,
            onBackground = darkColors.onBackground,
            surface = darkColors.surface,
            onSurface = darkColors.onSurface,
            error = darkColors.error,
            onError = darkColors.onError,
            outline = darkColors.border,
        )
    } else {
        lightColorScheme(
            primary = lightColors.primary,
            onPrimary = lightColors.onPrimary,
            secondary = lightColors.secondary,
            onSecondary = lightColors.onSecondary,
            tertiary = lightColors.tertiary,
            onTertiary = lightColors.onTertiary,
            background = lightColors.background,
            onBackground = lightColors.onBackground,
            surface = lightColors.surface,
            onSurface = lightColors.onSurface,
            error = lightColors.error,
            onError = lightColors.onError,
            outline = lightColors.border,
        )
    }

    CompositionLocalProvider(
        LocalVonageColors provides extendedColors,
        LocalVonageTypography provides VonageTypography(),
        LocalVonageShapes provides VonageShapes(),
        LocalVonageDimens provides VonageDimens(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
