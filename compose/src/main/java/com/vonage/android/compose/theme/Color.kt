@file:Suppress("MagicNumber")

package com.vonage.android.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Auto-generated from theme.json

// Light theme colors
val LightPrimary = Color(0xFF9941FF)
val LightPrimaryHover = Color(0xFF871EFF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF000000)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightTertiary = Color(0xFF757575)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightBackground = Color(0xFFF5F0FD)
val LightOnBackground = Color(0xFF757575)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF929292)
val LightError = Color(0xFFE61D1D)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorHover = Color(0xFFCD0000)
val LightWarning = Color(0xFFBE5702)
val LightOnWarning = Color(0xFFFFFFFF)
val LightWarningHover = Color(0xFFA64C03)
val LightSuccess = Color(0xFF1C8731)
val LightOnSuccess = Color(0xFFFFFFFF)
val LightSuccessHover = Color(0xFF1F7629)
val LightBorder = Color(0xFFE6E6E6)
val LightDisabled = Color(0xFFE6E6E6)
val LightTextDisabled = Color(0xFFB3B3B3)

// Dark theme colors
val DarkPrimary = Color(0xFFB377FF)
val DarkPrimaryHover = Color(0xFFC799FF)
val DarkOnPrimary = Color(0xFF000000)
val DarkSecondary = Color(0xFFFFFFFF)
val DarkOnSecondary = Color(0xFF000000)
val DarkTertiary = Color(0xFFB3B3B3)
val DarkOnTertiary = Color(0xFF000000)
val DarkBackground = Color(0xFF1C1C1E)
val DarkOnBackground = Color(0xFFB3B3B3)
val DarkSurface = Color(0xFF2C2C2E)
val DarkOnSurface = Color(0xFF929292)
val DarkError = Color(0xFFFF6B6B)
val DarkOnError = Color(0xFF000000)
val DarkErrorHover = Color(0xFFFF8787)
val DarkWarning = Color(0xFFFF9F4A)
val DarkOnWarning = Color(0xFF000000)
val DarkWarningHover = Color(0xFFFFB370)
val DarkSuccess = Color(0xFF4CD964)
val DarkOnSuccess = Color(0xFF000000)
val DarkSuccessHover = Color(0xFF6FE07D)
val DarkBorder = Color(0xFF3A3A3C)
val DarkDisabled = Color(0xFF3A3A3C)
val DarkTextDisabled = Color(0xFF636366)

internal val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = LightError,
    onError = LightOnError,
    outline = LightBorder,
)

internal val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = DarkError,
    onError = DarkOnError,
    outline = DarkBorder,
)

internal val LocalVonageColors = staticCompositionLocalOf {
    VonageColors(
        primary = Color.Unspecified,
        onPrimary = Color.Unspecified,
        primaryHover = Color.Unspecified,
        secondary = Color.Unspecified,
        onSecondary = Color.Unspecified,
        tertiary = Color.Unspecified,
        onTertiary = Color.Unspecified,
        accent = Color.Unspecified,
        onAccent = Color.Unspecified,
        background = Color.Unspecified,
        onBackground = Color.Unspecified,
        surface = Color.Unspecified,
        onSurface = Color.Unspecified,
        error = Color.Unspecified,
        onError = Color.Unspecified,
        errorHover = Color.Unspecified,
        warning = Color.Unspecified,
        onWarning = Color.Unspecified,
        warningHover = Color.Unspecified,
        success = Color.Unspecified,
        onSuccess = Color.Unspecified,
        successHover = Color.Unspecified,
        border = Color.Unspecified,
        disabled = Color.Unspecified,
        textDisabled = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textTertiary = Color.Unspecified,
    )
}

@Immutable
data class VonageColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryHover: Color,
    val secondary: Color,
    val onSecondary: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val accent: Color,
    val onAccent: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val error: Color,
    val onError: Color,
    val errorHover: Color,
    val warning: Color,
    val onWarning: Color,
    val warningHover: Color,
    val success: Color,
    val onSuccess: Color,
    val successHover: Color,
    val border: Color,
    val disabled: Color,
    val textDisabled: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
)
