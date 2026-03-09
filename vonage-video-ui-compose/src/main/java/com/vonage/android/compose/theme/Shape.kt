package com.vonage.android.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Auto-generated from theme.json

internal val shapeNone = RoundedCornerShape(0.dp)
internal val shapeExtraSmall = RoundedCornerShape(2.dp)
internal val shapeSmall = RoundedCornerShape(4.dp)
internal val shapeMedium = RoundedCornerShape(8.dp)
internal val shapeLarge = RoundedCornerShape(12.dp)
internal val shapeExtraLarge = RoundedCornerShape(24.dp)

internal val LocalVonageShapes = staticCompositionLocalOf {
    VonageShapes()
}

@Immutable
data class VonageShapes(
    val none: Shape = shapeNone,
    val extraSmall: Shape = shapeExtraSmall,
    val small: Shape = shapeSmall,
    val medium: Shape = shapeMedium,
    val large: Shape = shapeLarge,
    val extraLarge: Shape = shapeExtraLarge,
)
