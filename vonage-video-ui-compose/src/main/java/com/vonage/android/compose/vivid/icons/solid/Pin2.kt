@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Pin2: ImageVector
    get() {
        val current = _pin2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Pin2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // m23.55 8.55 -8.1 -8.1 a1.45 1.45 0 0 0 -2.1 0 1.45 1.45 0 0 0 0 2.1 l.9 .9 L4.05 9 l-1.5 -1.5 a1.45 1.45 0 0 0 -2.1 0 1.45 1.45 0 0 0 0 2.1 l5.85 5.85 -5.37 5.5 a1.5 1.5 0 0 0 2.13 2.1 l5.49 -5.5 6 6 c.75 .75 1.8 .3 2.1 0 .6 -.6 .6 -1.5 0 -2.1 l-1.5 -1.5 L20.7 9.9 l.9 .9 c.9 .9 1.8 .3 2.1 0 a1.8 1.8 0 0 0 -.15 -2.25
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 23.55 8.55
                moveTo(x = 23.55f, y = 8.55f)
                // l -8.1 -8.1
                lineToRelative(dx = -8.1f, dy = -8.1f)
                // a 1.45 1.45 0 0 0 -2.1 0
                arcToRelative(
                    a = 1.45f,
                    b = 1.45f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.1f,
                    dy1 = 0.0f,
                )
                // a 1.45 1.45 0 0 0 0 2.1
                arcToRelative(
                    a = 1.45f,
                    b = 1.45f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 2.1f,
                )
                // l 0.9 0.9
                lineToRelative(dx = 0.9f, dy = 0.9f)
                // L 4.05 9
                lineTo(x = 4.05f, y = 9.0f)
                // l -1.5 -1.5
                lineToRelative(dx = -1.5f, dy = -1.5f)
                // a 1.45 1.45 0 0 0 -2.1 0
                arcToRelative(
                    a = 1.45f,
                    b = 1.45f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.1f,
                    dy1 = 0.0f,
                )
                // a 1.45 1.45 0 0 0 0 2.1
                arcToRelative(
                    a = 1.45f,
                    b = 1.45f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 2.1f,
                )
                // l 5.85 5.85
                lineToRelative(dx = 5.85f, dy = 5.85f)
                // l -5.37 5.5
                lineToRelative(dx = -5.37f, dy = 5.5f)
                // a 1.5 1.5 0 0 0 2.13 2.1
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.13f,
                    dy1 = 2.1f,
                )
                // l 5.49 -5.5
                lineToRelative(dx = 5.49f, dy = -5.5f)
                // l 6 6
                lineToRelative(dx = 6.0f, dy = 6.0f)
                // c 0.75 0.75 1.8 0.3 2.1 0
                curveToRelative(
                    dx1 = 0.75f,
                    dy1 = 0.75f,
                    dx2 = 1.8f,
                    dy2 = 0.3f,
                    dx3 = 2.1f,
                    dy3 = 0.0f,
                )
                // c 0.6 -0.6 0.6 -1.5 0 -2.1
                curveToRelative(
                    dx1 = 0.6f,
                    dy1 = -0.6f,
                    dx2 = 0.6f,
                    dy2 = -1.5f,
                    dx3 = 0.0f,
                    dy3 = -2.1f,
                )
                // l -1.5 -1.5
                lineToRelative(dx = -1.5f, dy = -1.5f)
                // L 20.7 9.9
                lineTo(x = 20.7f, y = 9.9f)
                // l 0.9 0.9
                lineToRelative(dx = 0.9f, dy = 0.9f)
                // c 0.9 0.9 1.8 0.3 2.1 0
                curveToRelative(
                    dx1 = 0.9f,
                    dy1 = 0.9f,
                    dx2 = 1.8f,
                    dy2 = 0.3f,
                    dx3 = 2.1f,
                    dy3 = 0.0f,
                )
                // a 1.8 1.8 0 0 0 -0.15 -2.25
                arcToRelative(
                    a = 1.8f,
                    b = 1.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.15f,
                    dy1 = -2.25f,
                )
            }
        }.build().also { _pin2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _pin2Solid: ImageVector? = null
