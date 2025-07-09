@file:Suppress("MagicNumber")

package com.vonage.android.compose.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Blue = Color(0xFF2563EB)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

internal val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

internal val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

internal val LocalVonageColors = staticCompositionLocalOf {
    VonageColors(
        buttonPrimary = Color.Unspecified,
        buttonPrimaryDisabled = Color.Unspecified,
        textPrimary = Color.Unspecified,
        textPrimaryDisabled = Color.Unspecified,
        textError = Color.Unspecified,
    )
}

@Immutable
data class VonageColors(
    val textPrimary: Color,
    val textPrimaryDisabled: Color,
    val textError: Color,

    val buttonPrimary: Color,
    val buttonPrimaryDisabled: Color,
)
