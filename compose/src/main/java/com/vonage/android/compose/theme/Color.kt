@file:Suppress("MagicNumber")

package com.vonage.android.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Design tokens from tokens.json
// CTA (Call-to-Action) colors
val Cta50 = Color(0xFFF5F0FD)
val Cta500 = Color(0xFF9941FF)
val Cta600 = Color(0xFF871EFF)

// Information colors
val Information50 = Color(0xFFE8F4FB)
val Information400 = Color(0xFF2997F0)
val Information500 = Color(0xFF0276D5)

// Canvas colors
val Canvas = Color(0xFFFFFFFF)
val CanvasText = Color(0xFF000000)

// Accent colors
val Accent300 = Color(0xFFB3B3B3)
val Accent400 = Color(0xFF929292)
val Accent500 = Color(0xFF757575)
val Accent600 = Color(0xFF666666)

// Neutral colors
val Neutral100 = Color(0xFFE6E6E6)
val Neutral300 = Color(0xFFB3B3B3)
val Neutral400 = Color(0xFF929292)
val Neutral500 = Color(0xFF757575)

// Alert colors
val Alert500 = Color(0xFFE61D1D)
val Alert600 = Color(0xFFCD0000)

// Warning colors
val Warning500 = Color(0xFFBE5702)
val Warning600 = Color(0xFFA64C03)

// Success colors
val Success500 = Color(0xFF1C8731)
val Success600 = Color(0xFF1F7629)

internal val DarkColorScheme = darkColorScheme(
    primary = Cta500,
    onPrimary = CanvasText,
    primaryContainer = Accent600,
    onPrimaryContainer = Neutral100,
    secondary = Accent300,
    onSecondary = CanvasText,
    secondaryContainer = Accent600,
    onSecondaryContainer = Neutral100,
    tertiary = Accent400,
    onTertiary = CanvasText,
    background = CanvasText,
    onBackground = Neutral100,
    surface = Accent600,
    onSurface = Neutral100,
    surfaceVariant = Accent500,
    onSurfaceVariant = Neutral300,
    error = Alert500,
    onError = CanvasText,
    errorContainer = Alert600,
    onErrorContainer = Neutral100,
    outline = Accent500,
    outlineVariant = Accent600,
)

internal val LightColorScheme = lightColorScheme(
    primary = Cta500,
    onPrimary = Canvas,
    primaryContainer = Cta600,
    onPrimaryContainer = Canvas,
    secondary = CanvasText,
    onSecondary = Canvas,
    secondaryContainer = Accent500,
    onSecondaryContainer = Canvas,
    tertiary = Accent500,
    onTertiary = Canvas,
    background = Cta50,
    onBackground = Neutral500,
    surface = Canvas,
    onSurface = Neutral400,
    surfaceVariant = Cta50,
    onSurfaceVariant = Neutral500,
    error = Alert500,
    onError = Canvas,
    errorContainer = Alert600,
    onErrorContainer = Canvas,
    outline = Neutral100,
    outlineVariant = Neutral300,
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
    // Theme colors mapped from theme.json
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
    
    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
)

