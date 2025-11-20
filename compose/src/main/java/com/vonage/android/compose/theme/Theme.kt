package com.vonage.android.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

@Composable
fun VonageVideoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Extended colors based on theme.json vonage theme
    val extendedColors = VonageColors(
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
