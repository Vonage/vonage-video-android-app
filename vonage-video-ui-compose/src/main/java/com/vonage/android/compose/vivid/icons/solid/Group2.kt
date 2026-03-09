@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Group2: ImageVector
    get() {
        val current = _group2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Group2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M20 5 a3 3 0 1 1 -6 0 3 3 0 0 1 6 0 m2.06 11 c.56 0 1.02 -.45 .93 -1 a6.09 6.09 0 0 0 -10.9 -2.52 A10 10 0 0 1 16.99 16z M13 8 a4 4 0 1 1 -8 0 4 4 0 0 1 8 0
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 20 5
                moveTo(x = 20.0f, y = 5.0f)
                // a 3 3 0 1 1 -6 0
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -6.0f,
                    dy1 = 0.0f,
                )
                // a 3 3 0 0 1 6 0
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 6.0f,
                    dy1 = 0.0f,
                )
                // m 2.06 11
                moveToRelative(dx = 2.06f, dy = 11.0f)
                // c 0.56 0 1.02 -0.45 0.93 -1
                curveToRelative(
                    dx1 = 0.56f,
                    dy1 = 0.0f,
                    dx2 = 1.02f,
                    dy2 = -0.45f,
                    dx3 = 0.93f,
                    dy3 = -1.0f,
                )
                // a 6.09 6.09 0 0 0 -10.9 -2.52
                arcToRelative(
                    a = 6.09f,
                    b = 6.09f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -10.9f,
                    dy1 = -2.52f,
                )
                // A 10 10 0 0 1 16.99 16z
                arcTo(
                    horizontalEllipseRadius = 10.0f,
                    verticalEllipseRadius = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 16.99f,
                    y1 = 16.0f,
                )
                close()
                // M 13 8
                moveTo(x = 13.0f, y = 8.0f)
                // a 4 4 0 1 1 -8 0
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -8.0f,
                    dy1 = 0.0f,
                )
                // a 4 4 0 0 1 8 0
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 8.0f,
                    dy1 = 0.0f,
                )
            }
            // M17 21 c.06 .55 -.4 1 -.95 1 H1.95 c-.55 0 -1.01 -.45 -.94 -1 .5 -3.95 3.88 -7 7.99 -7 4.1 0 7.5 3.05 8 7
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 17 21
                moveTo(x = 17.0f, y = 21.0f)
                // c 0.06 0.55 -0.4 1 -0.95 1
                curveToRelative(
                    dx1 = 0.06f,
                    dy1 = 0.55f,
                    dx2 = -0.4f,
                    dy2 = 1.0f,
                    dx3 = -0.95f,
                    dy3 = 1.0f,
                )
                // H 1.95
                horizontalLineTo(x = 1.95f)
                // c -0.55 0 -1.01 -0.45 -0.94 -1
                curveToRelative(
                    dx1 = -0.55f,
                    dy1 = 0.0f,
                    dx2 = -1.01f,
                    dy2 = -0.45f,
                    dx3 = -0.94f,
                    dy3 = -1.0f,
                )
                // c 0.5 -3.95 3.88 -7 7.99 -7
                curveToRelative(
                    dx1 = 0.5f,
                    dy1 = -3.95f,
                    dx2 = 3.88f,
                    dy2 = -7.0f,
                    dx3 = 7.99f,
                    dy3 = -7.0f,
                )
                // c 4.1 0 7.5 3.05 8 7
                curveToRelative(
                    dx1 = 4.1f,
                    dy1 = 0.0f,
                    dx2 = 7.5f,
                    dy2 = 3.05f,
                    dx3 = 8.0f,
                    dy3 = 7.0f,
                )
            }
        }.build().also { _group2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _group2Solid: ImageVector? = null
