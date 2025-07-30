@file:Suppress("MagicNumber")

package com.vonage.android.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Blue = Color(0xFF2563EB)

internal val DarkColorScheme = darkColorScheme(
    primary = Blue,
)

internal val LightColorScheme = lightColorScheme(
    primary = Blue,
)

internal val LocalVonageColors = staticCompositionLocalOf {
    VonageColors(
        primary = Color.Unspecified,
        background = Color.Unspecified,
        buttonPrimary = Color.Unspecified,
        buttonPrimaryDisabled = Color.Unspecified,
        inverseSurface = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textPrimaryDisabled = Color.Unspecified,
        textError = Color.Unspecified,
        surface = Color.Unspecified,
    )
}

@Immutable
data class VonageColors(
    val primary: Color,
    val background: Color,

    val textPrimary: Color,
    val textPrimaryDisabled: Color,
    val textError: Color,

    val buttonPrimary: Color,
    val buttonPrimaryDisabled: Color,

    val surface: Color,
    val inverseSurface: Color,
)
