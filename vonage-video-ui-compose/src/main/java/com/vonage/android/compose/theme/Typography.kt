// Auto-generated from theme.json
package com.vonage.android.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val headlineTextStyle: TextStyle = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight(500),
            lineHeight = 40.sp,
            fontFamily = FontFamily.Default,
        )

internal val subtitleTextStyle: TextStyle = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight(500),
            lineHeight = 40.sp,
            fontFamily = FontFamily.Default,
        )

internal val heading1TextStyle: TextStyle = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight(500),
            lineHeight = 36.sp,
            fontFamily = FontFamily.Default,
        )

internal val heading2TextStyle: TextStyle = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight(300),
            lineHeight = 32.sp,
            fontFamily = FontFamily.Default,
        )

internal val heading3TextStyle: TextStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight(500),
            lineHeight = 28.sp,
            fontFamily = FontFamily.Default,
        )

internal val heading4TextStyle: TextStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight(500),
            lineHeight = 24.sp,
            fontFamily = FontFamily.Default,
        )

internal val bodyExtendedTextStyle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            lineHeight = 24.sp,
            fontFamily = FontFamily.Default,
        )

internal val bodyExtendedSemiboldTextStyle: TextStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight(600),
            lineHeight = 24.sp,
            fontFamily = FontFamily.Default,
        )

internal val bodyBaseTextStyle: TextStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            lineHeight = 20.sp,
            fontFamily = FontFamily.Default,
        )

internal val bodyBaseSemiboldTextStyle: TextStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(600),
            lineHeight = 20.sp,
            fontFamily = FontFamily.Default,
        )

internal val captionTextStyle: TextStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(400),
            lineHeight = 16.sp,
            fontFamily = FontFamily.Default,
        )

internal val captionSemiboldTextStyle: TextStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(600),
            lineHeight = 16.sp,
            fontFamily = FontFamily.Default,
        )

internal val LocalVonageTypography: ProvidableCompositionLocal<VonageTypography> =
        staticCompositionLocalOf {
    VonageTypography()
}

@Immutable
public data class VonageTypography(
    public val headline: TextStyle = headlineTextStyle,
    public val subtitle: TextStyle = subtitleTextStyle,
    public val heading1: TextStyle = heading1TextStyle,
    public val heading2: TextStyle = heading2TextStyle,
    public val heading3: TextStyle = heading3TextStyle,
    public val heading4: TextStyle = heading4TextStyle,
    public val bodyExtended: TextStyle = bodyExtendedTextStyle,
    public val bodyExtendedSemibold: TextStyle = bodyExtendedSemiboldTextStyle,
    public val bodyBase: TextStyle = bodyBaseTextStyle,
    public val bodyBaseSemibold: TextStyle = bodyBaseSemiboldTextStyle,
    public val caption: TextStyle = captionTextStyle,
    public val captionSemibold: TextStyle = captionSemiboldTextStyle,
)
