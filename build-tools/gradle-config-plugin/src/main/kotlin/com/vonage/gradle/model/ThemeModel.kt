package com.vonage.gradle.model

import com.google.gson.annotations.SerializedName

data class Theme(
    val metadata: Metadata,
    val colors: Colors,
    val borderRadius: BorderRadius,
    val typography: Typography
)

data class Metadata(
    val name: String,
    val version: String,
    val created: String,
    val description: String
)

data class Colors(
    val light: ColorScheme,
    val dark: ColorScheme
)

/**
 * A palette for one appearance (light or dark). The schema's `colorSet` requires a core set of
 * roles but allows additional semantic colors, so this is modelled as a plain map of role name
 * (kebab-case, as it appears in JSON) to hex value rather than a fixed data class.
 */
typealias ColorScheme = Map<String, String>

fun ColorScheme.role(name: String): String =
    this[name] ?: throw IllegalArgumentException("Missing required color role: $name")

val ColorScheme.primary: String get() = role("primary")
val ColorScheme.textPrimary: String get() = role("text-primary")
val ColorScheme.onPrimary: String get() = role("on-primary")
val ColorScheme.primaryHover: String get() = role("primary-hover")
val ColorScheme.secondary: String get() = role("secondary")
val ColorScheme.textSecondary: String get() = role("text-secondary")
val ColorScheme.onSecondary: String get() = role("on-secondary")
val ColorScheme.tertiary: String get() = role("tertiary")
val ColorScheme.textTertiary: String get() = role("text-tertiary")
val ColorScheme.onTertiary: String get() = role("on-tertiary")
val ColorScheme.background: String get() = role("background")
val ColorScheme.onBackground: String get() = role("on-background")
val ColorScheme.surface: String get() = role("surface")
val ColorScheme.onSurface: String get() = role("on-surface")
val ColorScheme.error: String get() = role("error")
val ColorScheme.onError: String get() = role("on-error")
val ColorScheme.errorHover: String get() = role("error-hover")
val ColorScheme.warning: String get() = role("warning")
val ColorScheme.onWarning: String get() = role("on-warning")
val ColorScheme.warningHover: String get() = role("warning-hover")
val ColorScheme.success: String get() = role("success")
val ColorScheme.onSuccess: String get() = role("on-success")
val ColorScheme.successHover: String get() = role("success-hover")
val ColorScheme.border: String get() = role("border")
val ColorScheme.disabled: String get() = role("disabled")
val ColorScheme.textDisabled: String get() = role("text-disabled")

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
