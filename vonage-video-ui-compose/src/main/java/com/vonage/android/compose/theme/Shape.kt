// Auto-generated from theme.json
package com.vonage.android.compose.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

internal val shapeNone: RoundedCornerShape = RoundedCornerShape(0.dp)

internal val shapeExtraSmall: RoundedCornerShape = RoundedCornerShape(2.dp)

internal val shapeSmall: RoundedCornerShape = RoundedCornerShape(4.dp)

internal val shapeMedium: RoundedCornerShape = RoundedCornerShape(8.dp)

internal val shapeLarge: RoundedCornerShape = RoundedCornerShape(12.dp)

internal val shapeExtraLarge: RoundedCornerShape = RoundedCornerShape(24.dp)

internal val LocalVonageShapes: ProvidableCompositionLocal<VonageShapes> =
        staticCompositionLocalOf {
    VonageShapes()
}

@Immutable
public data class VonageShapes(
    public val none: Shape = shapeNone,
    public val extraSmall: Shape = shapeExtraSmall,
    public val small: Shape = shapeSmall,
    public val medium: Shape = shapeMedium,
    public val large: Shape = shapeLarge,
    public val extraLarge: Shape = shapeExtraLarge,
)
