@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Share2: ImageVector
    get() {
        val current = _share2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Share2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M18 9 a4.5 4.5 0 1 0 -4.39 -3.53 L8.78 8.5 A4.5 4.5 0 0 0 6 7.5 a4.5 4.5 0 0 0 0 9 4.5 4.5 0 0 0 2.78 -1 l4.83 3.03 A4.5 4.5 0 1 0 18 15 a4.5 4.5 0 0 0 -2.78 1 l-4.83 -3.03 a5 5 0 0 0 0 -1.94 L15.22 8 A4.5 4.5 0 0 0 18 9
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 18 9
                moveTo(x = 18.0f, y = 9.0f)
                // a 4.5 4.5 0 1 0 -4.39 -3.53
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = -4.39f,
                    dy1 = -3.53f,
                )
                // L 8.78 8.5
                lineTo(x = 8.78f, y = 8.5f)
                // A 4.5 4.5 0 0 0 6 7.5
                arcTo(
                    horizontalEllipseRadius = 4.5f,
                    verticalEllipseRadius = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 6.0f,
                    y1 = 7.5f,
                )
                // a 4.5 4.5 0 0 0 0 9
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 9.0f,
                )
                // a 4.5 4.5 0 0 0 2.78 -1
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.78f,
                    dy1 = -1.0f,
                )
                // l 4.83 3.03
                lineToRelative(dx = 4.83f, dy = 3.03f)
                // A 4.5 4.5 0 1 0 18 15
                arcTo(
                    horizontalEllipseRadius = 4.5f,
                    verticalEllipseRadius = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    x1 = 18.0f,
                    y1 = 15.0f,
                )
                // a 4.5 4.5 0 0 0 -2.78 1
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.78f,
                    dy1 = 1.0f,
                )
                // l -4.83 -3.03
                lineToRelative(dx = -4.83f, dy = -3.03f)
                // a 5 5 0 0 0 0 -1.94
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -1.94f,
                )
                // L 15.22 8
                lineTo(x = 15.22f, y = 8.0f)
                // A 4.5 4.5 0 0 0 18 9
                arcTo(
                    horizontalEllipseRadius = 4.5f,
                    verticalEllipseRadius = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 18.0f,
                    y1 = 9.0f,
                )
            }
        }.build().also { _share2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _share2Solid: ImageVector? = null
