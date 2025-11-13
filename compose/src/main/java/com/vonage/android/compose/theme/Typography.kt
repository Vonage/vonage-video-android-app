package com.vonage.android.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val titleLargeTextStyle = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Bold,
    lineHeight = 46.sp,
)

internal val titleTextStyle = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 28.sp,
)

internal val bodyTextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
)

internal val bodyMediumTextStyle = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
)

internal val labelTextStyle = TextStyle(
    fontSize = 11.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 16.sp,
)

internal val LocalVonageTypography = staticCompositionLocalOf {
    VonageTypography()
}

@Immutable
data class VonageTypography(
    val titleLarge: TextStyle = titleLargeTextStyle,
    val body: TextStyle = bodyTextStyle,
    val bodyMedium: TextStyle = bodyMediumTextStyle,
    val title: TextStyle = titleTextStyle,
    val label: TextStyle = labelTextStyle,
)
