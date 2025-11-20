package com.vonage.gradle.model

import com.google.gson.annotations.SerializedName

data class ThemeConfig(
    val themes: Map<String, Theme>
)

data class Theme(
    val colors: Colors,
    val borderRadius: BorderRadius,
    val typography: Typography
)

data class Colors(
    val light: ColorScheme,
    val dark: ColorScheme
)

data class ColorScheme(
    val primary: String,
    @SerializedName("text-primary")
    val textPrimary: String,
    @SerializedName("on-primary")
    val onPrimary: String,
    @SerializedName("primary-hover")
    val primaryHover: String,
    val secondary: String,
    @SerializedName("text-secondary")
    val textSecondary: String,
    @SerializedName("on-secondary")
    val onSecondary: String,
    val tertiary: String,
    @SerializedName("text-tertiary")
    val textTertiary: String,
    @SerializedName("on-tertiary")
    val onTertiary: String,
    val background: String,
    @SerializedName("on-background")
    val onBackground: String,
    val surface: String,
    @SerializedName("on-surface")
    val onSurface: String,
    val error: String,
    @SerializedName("on-error")
    val onError: String,
    @SerializedName("error-hover")
    val errorHover: String,
    val warning: String,
    @SerializedName("on-warning")
    val onWarning: String,
    @SerializedName("warning-hover")
    val warningHover: String,
    val success: String,
    @SerializedName("on-success")
    val onSuccess: String,
    @SerializedName("success-hover")
    val successHover: String,
    val border: String,
    val disabled: String,
    @SerializedName("text-disabled")
    val textDisabled: String
)

data class BorderRadius(
    val none: Int,
    @SerializedName("extra-small")
    val extraSmall: Int,
    val small: Int,
    val medium: Int,
    val large: Int,
    @SerializedName("extra-large")
    val extraLarge: Int
)

data class Typography(
    @SerializedName("font-family")
    val fontFamily: String,
    val mobile: TypographyStyles,
    val desktop: TypographyStyles
)

data class TypographyStyles(
    val headline: TextStyle,
    val subtitle: TextStyle,
    @SerializedName("heading-1")
    val heading1: TextStyle,
    @SerializedName("heading-2")
    val heading2: TextStyle,
    @SerializedName("heading-3")
    val heading3: TextStyle,
    @SerializedName("heading-4")
    val heading4: TextStyle,
    @SerializedName("body-extended")
    val bodyExtended: TextStyle,
    @SerializedName("body-extended-semibold")
    val bodyExtendedSemibold: TextStyle,
    @SerializedName("body-base")
    val bodyBase: TextStyle,
    @SerializedName("body-base-semibold")
    val bodyBaseSemibold: TextStyle,
    val caption: TextStyle,
    @SerializedName("caption-semibold")
    val captionSemibold: TextStyle
)

data class TextStyle(
    @SerializedName("font-size")
    val fontSize: String,
    @SerializedName("line-height")
    val lineHeight: String,
    @SerializedName("font-weight")
    val fontWeight: Int
)
