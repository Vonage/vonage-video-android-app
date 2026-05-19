package com.vonage.android.meetingroom.api

import com.vonage.android.compose.theme.VonageColors
import com.vonage.android.compose.theme.DarkBackground
import com.vonage.android.compose.theme.DarkBorder
import com.vonage.android.compose.theme.DarkDisabled
import com.vonage.android.compose.theme.DarkError
import com.vonage.android.compose.theme.DarkErrorHover
import com.vonage.android.compose.theme.DarkOnBackground
import com.vonage.android.compose.theme.DarkOnError
import com.vonage.android.compose.theme.DarkOnPrimary
import com.vonage.android.compose.theme.DarkOnSecondary
import com.vonage.android.compose.theme.DarkOnSurface
import com.vonage.android.compose.theme.DarkOnTertiary
import com.vonage.android.compose.theme.DarkOnWarning
import com.vonage.android.compose.theme.DarkOnSuccess
import com.vonage.android.compose.theme.DarkPrimary
import com.vonage.android.compose.theme.DarkPrimaryHover
import com.vonage.android.compose.theme.DarkSecondary
import com.vonage.android.compose.theme.DarkSuccess
import com.vonage.android.compose.theme.DarkSuccessHover
import com.vonage.android.compose.theme.DarkSurface
import com.vonage.android.compose.theme.DarkTertiary
import com.vonage.android.compose.theme.DarkTextDisabled
import com.vonage.android.compose.theme.DarkWarning
import com.vonage.android.compose.theme.DarkWarningHover
import com.vonage.android.compose.theme.LightBackground
import com.vonage.android.compose.theme.LightBorder
import com.vonage.android.compose.theme.LightDisabled
import com.vonage.android.compose.theme.LightError
import com.vonage.android.compose.theme.LightErrorHover
import com.vonage.android.compose.theme.LightOnBackground
import com.vonage.android.compose.theme.LightOnError
import com.vonage.android.compose.theme.LightOnPrimary
import com.vonage.android.compose.theme.LightOnSecondary
import com.vonage.android.compose.theme.LightOnSurface
import com.vonage.android.compose.theme.LightOnTertiary
import com.vonage.android.compose.theme.LightOnWarning
import com.vonage.android.compose.theme.LightOnSuccess
import com.vonage.android.compose.theme.LightPrimary
import com.vonage.android.compose.theme.LightPrimaryHover
import com.vonage.android.compose.theme.LightSecondary
import com.vonage.android.compose.theme.LightSuccess
import com.vonage.android.compose.theme.LightSuccessHover
import com.vonage.android.compose.theme.LightSurface
import com.vonage.android.compose.theme.LightTertiary
import com.vonage.android.compose.theme.LightTextDisabled
import com.vonage.android.compose.theme.LightWarning
import com.vonage.android.compose.theme.LightWarningHover

/**
 * Color theme for the meeting room.
 *
 * Supply explicit [lightColors] and/or [darkColors] to override the default Vonage palette.
 * Start from [MeetingRoomTheme.vonage] and use [VonageColors.copy] to customise individual
 * color roles.
 *
 * Example:
 * ```kotlin
 * MeetingRoomBuilder(baseUrl, roomName)
 *     .theme(
 *         MeetingRoomTheme(
 *             lightColors = MeetingRoomTheme.vonage.lightColors.copy(primary = Color.Blue),
 *             darkColors  = MeetingRoomTheme.vonage.darkColors.copy(primary  = Color.Blue),
 *         )
 *     )
 * ```
 *
 * @param lightColors Color palette used in light mode.
 * @param darkColors  Color palette used in dark mode.
 */
@ExperimentalMeetingRoomApi
data class MeetingRoomTheme(
    val lightColors: VonageColors,
    val darkColors: VonageColors,
) {
    companion object {
        /** Default Vonage color theme — mirrors the values from `theme.json`. */
        val vonage: MeetingRoomTheme
            get() = MeetingRoomTheme(
                lightColors = vonageLightColors(),
                darkColors = vonageDarkColors(),
            )
    }
}

/** Constructs [VonageColors] from the default Vonage light-mode palette. */
@ExperimentalMeetingRoomApi
fun vonageLightColors(): VonageColors = VonageColors(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryHover = LightPrimaryHover,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    // accent aliases secondary in the default Vonage light theme
    accent = LightSecondary,
    onAccent = LightOnSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = LightError,
    onError = LightOnError,
    errorHover = LightErrorHover,
    warning = LightWarning,
    onWarning = LightOnWarning,
    warningHover = LightWarningHover,
    success = LightSuccess,
    onSuccess = LightOnSuccess,
    successHover = LightSuccessHover,
    border = LightBorder,
    disabled = LightDisabled,
    textDisabled = LightTextDisabled,
    textPrimary = LightPrimary,
    textSecondary = LightSecondary,
    textTertiary = LightTertiary,
)

/** Constructs [VonageColors] from the default Vonage dark-mode palette. */
@ExperimentalMeetingRoomApi
fun vonageDarkColors(): VonageColors = VonageColors(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryHover = DarkPrimaryHover,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    // accent aliases primary in the default Vonage dark theme
    accent = DarkPrimary,
    onAccent = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = DarkError,
    onError = DarkOnError,
    errorHover = DarkErrorHover,
    warning = DarkWarning,
    onWarning = DarkOnWarning,
    warningHover = DarkWarningHover,
    success = DarkSuccess,
    onSuccess = DarkOnSuccess,
    successHover = DarkSuccessHover,
    border = DarkBorder,
    disabled = DarkDisabled,
    textDisabled = DarkTextDisabled,
    textPrimary = DarkPrimary,
    textSecondary = DarkSecondary,
    textTertiary = DarkTertiary,
)
