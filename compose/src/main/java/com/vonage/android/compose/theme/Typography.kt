package com.vonage.android.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val headlineTextStyle = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 40.sp,
    fontFamily = FontFamily.Default, // Replace with Inter font family when available
)

internal val subtitleTextStyle = TextStyle(
    fontSize = 32.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 44.sp,
    fontFamily = FontFamily.Default,
)

internal val heading1TextStyle = TextStyle(
    fontSize = 28.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 36.sp,
    fontFamily = FontFamily.Default,
)

internal val heading2TextStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 32.sp,
    fontFamily = FontFamily.Default,
)

internal val heading3TextStyle = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 28.sp,
    fontFamily = FontFamily.Default,
)

internal val heading4TextStyle = TextStyle(
    fontSize = 18.sp,
    fontWeight = FontWeight.Medium,
    lineHeight = 24.sp,
    fontFamily = FontFamily.Default,
)

internal val bodyExtendedTextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 24.sp,
    fontFamily = FontFamily.Default,
)

internal val bodyExtendedSemiboldTextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 24.sp,
    fontFamily = FontFamily.Default,
)

internal val bodyBaseTextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 20.sp,
    fontFamily = FontFamily.Default,
)

internal val bodyBaseSemiboldTextStyle = TextStyle(
    fontSize = 14.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 20.sp,
    fontFamily = FontFamily.Default,
)

internal val captionTextStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 16.sp,
    fontFamily = FontFamily.Default,
)

internal val captionSemiboldTextStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 16.sp,
    fontFamily = FontFamily.Default,
)

internal val LocalVonageTypography = staticCompositionLocalOf {
    VonageTypography()
}

@Immutable
data class VonageTypography(
    val headline: TextStyle = headlineTextStyle,
    val subtitle: TextStyle = subtitleTextStyle,
    val heading1: TextStyle = heading1TextStyle,
    val heading2: TextStyle = heading2TextStyle,
    val heading3: TextStyle = heading3TextStyle,
    val heading4: TextStyle = heading4TextStyle,
    val bodyExtended: TextStyle = bodyExtendedTextStyle,
    val bodyExtendedSemibold: TextStyle = bodyExtendedSemiboldTextStyle,
    val bodyBase: TextStyle = bodyBaseTextStyle,
    val bodyBaseSemibold: TextStyle = bodyBaseSemiboldTextStyle,
    val caption: TextStyle = captionTextStyle,
    val captionSemibold: TextStyle = captionSemiboldTextStyle,
)
