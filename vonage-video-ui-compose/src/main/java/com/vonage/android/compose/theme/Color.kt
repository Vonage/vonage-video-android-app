// Auto-generated from theme.json
@file:Suppress("MagicNumber")

package com.vonage.android.compose.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlin.Suppress

public val LightPrimary: Color = Color(0xFF9941FF)

public val LightPrimaryHover: Color = Color(0xFF871EFF)

public val LightOnPrimary: Color = Color(0xFFFFFFFF)

public val LightSecondary: Color = Color(0xFF000000)

public val LightOnSecondary: Color = Color(0xFFFFFFFF)

public val LightTertiary: Color = Color(0xFF757575)

public val LightOnTertiary: Color = Color(0xFFFFFFFF)

public val LightBackground: Color = Color(0xFFF5F0FD)

public val LightOnBackground: Color = Color(0xFF757575)

public val LightSurface: Color = Color(0xFFFFFFFF)

public val LightOnSurface: Color = Color(0xFF929292)

public val LightError: Color = Color(0xFFE61D1D)

public val LightOnError: Color = Color(0xFFFFFFFF)

public val LightErrorHover: Color = Color(0xFFCD0000)

public val LightWarning: Color = Color(0xFFBE5702)

public val LightOnWarning: Color = Color(0xFFFFFFFF)

public val LightWarningHover: Color = Color(0xFFA64C03)

public val LightSuccess: Color = Color(0xFF1C8731)

public val LightOnSuccess: Color = Color(0xFFFFFFFF)

public val LightSuccessHover: Color = Color(0xFF1F7629)

public val LightBorder: Color = Color(0xFFE6E6E6)

public val LightDisabled: Color = Color(0xFFE6E6E6)

public val LightTextDisabled: Color = Color(0xFFB3B3B3)

public val DarkPrimary: Color = Color(0xFFB377FF)

public val DarkPrimaryHover: Color = Color(0xFFC799FF)

public val DarkOnPrimary: Color = Color(0xFF000000)

public val DarkSecondary: Color = Color(0xFFFFFFFF)

public val DarkOnSecondary: Color = Color(0xFF000000)

public val DarkTertiary: Color = Color(0xFFB3B3B3)

public val DarkOnTertiary: Color = Color(0xFF000000)

public val DarkBackground: Color = Color(0xFF1C1C1E)

public val DarkOnBackground: Color = Color(0xFFB3B3B3)

public val DarkSurface: Color = Color(0xFF2C2C2E)

public val DarkOnSurface: Color = Color(0xFF929292)

public val DarkError: Color = Color(0xFFFF6B6B)

public val DarkOnError: Color = Color(0xFF000000)

public val DarkErrorHover: Color = Color(0xFFFF8787)

public val DarkWarning: Color = Color(0xFFFF9F4A)

public val DarkOnWarning: Color = Color(0xFF000000)

public val DarkWarningHover: Color = Color(0xFFFFB370)

public val DarkSuccess: Color = Color(0xFF4CD964)

public val DarkOnSuccess: Color = Color(0xFF000000)

public val DarkSuccessHover: Color = Color(0xFF6FE07D)

public val DarkBorder: Color = Color(0xFF3A3A3C)

public val DarkDisabled: Color = Color(0xFF3A3A3C)

public val DarkTextDisabled: Color = Color(0xFF636366)

internal val LightColorScheme: ColorScheme = lightColorScheme(
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

internal val DarkColorScheme: ColorScheme = darkColorScheme(
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

internal val LocalVonageColors: ProvidableCompositionLocal<VonageColors> =
        staticCompositionLocalOf {
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
public data class VonageColors(
    public val primary: Color,
    public val onPrimary: Color,
    public val primaryHover: Color,
    public val secondary: Color,
    public val onSecondary: Color,
    public val tertiary: Color,
    public val onTertiary: Color,
    public val accent: Color,
    public val onAccent: Color,
    public val background: Color,
    public val onBackground: Color,
    public val surface: Color,
    public val onSurface: Color,
    public val error: Color,
    public val onError: Color,
    public val errorHover: Color,
    public val warning: Color,
    public val onWarning: Color,
    public val warningHover: Color,
    public val success: Color,
    public val onSuccess: Color,
    public val successHover: Color,
    public val border: Color,
    public val disabled: Color,
    public val textDisabled: Color,
    public val textPrimary: Color,
    public val textSecondary: Color,
    public val textTertiary: Color,
)
