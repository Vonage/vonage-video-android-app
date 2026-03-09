package com.vonage.android.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val LocalVonageDimens = staticCompositionLocalOf {
    VonageDimens()
}

@Immutable
data class VonageDimens(
    // Spacing
    val spaceNone: Dp = 0.dp,
    val spaceXXSmall: Dp = 2.dp,
    val spaceXSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 12.dp,
    val spaceDefault: Dp = 16.dp,
    val spaceLarge: Dp = 24.dp,
    val spaceXLarge: Dp = 32.dp,
    val spaceXXLarge: Dp = 48.dp,
    val spaceXXXLarge: Dp = 64.dp,

    // Padding
    val paddingXSmall: Dp = 4.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 12.dp,
    val paddingDefault: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,
    val paddingXLarge: Dp = 32.dp,

    // Component sizes
    val buttonHeight: Dp = 48.dp,
    val buttonHeightSmall: Dp = 36.dp,
    val buttonHeightLarge: Dp = 56.dp,
    
    val iconSizeSmall: Dp = 16.dp,
    val iconSizeDefault: Dp = 24.dp,
    val iconSizeLarge: Dp = 32.dp,
    val iconSizeXLarge: Dp = 48.dp,

    val avatarSizeSmall: Dp = 32.dp,
    val avatarSizeDefault: Dp = 48.dp,
    val avatarSizeLarge: Dp = 64.dp,
    val avatarSizeXLarge: Dp = 96.dp,

    // Borders
    val borderWidthThin: Dp = 1.dp,
    val borderWidthDefault: Dp = 2.dp,
    val borderWidthThick: Dp = 4.dp,

    // Elevation/Shadow
    val elevationNone: Dp = 0.dp,
    val elevationSmall: Dp = 2.dp,
    val elevationDefault: Dp = 4.dp,
    val elevationMedium: Dp = 8.dp,
    val elevationLarge: Dp = 16.dp,

    // Card/Container
    val cardMinHeight: Dp = 100.dp,
    val cardMaxWidth: Dp = 600.dp,
    
    // Divider
    val dividerThickness: Dp = 1.dp,
    
    // Touch target
    val minTouchTarget: Dp = 48.dp,
)
