@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Error: ImageVector
    get() {
        val current = _errorSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Error",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M12 1 a11 11 0 1 0 0 22 11 11 0 0 0 0 -22 m1.5 15.5 c0 -.9 -.6 -1.5 -1.5 -1.5 s-1.5 .6 -1.5 1.5 S11.1 18 12 18 s1.5 -.6 1.5 -1.5 m-.5 -3 a.5 .5 0 0 0 .5 -.5 V6.5 A.5 .5 0 0 0 13 6 h-2 a.5 .5 0 0 0 -.5 .5 V13 a.5 .5 0 0 0 .5 .5z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 12 1
                moveTo(x = 12.0f, y = 1.0f)
                // a 11 11 0 1 0 0 22
                arcToRelative(
                    a = 11.0f,
                    b = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 22.0f,
                )
                // a 11 11 0 0 0 0 -22
                arcToRelative(
                    a = 11.0f,
                    b = 11.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -22.0f,
                )
                // m 1.5 15.5
                moveToRelative(dx = 1.5f, dy = 15.5f)
                // c 0 -0.9 -0.6 -1.5 -1.5 -1.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.9f,
                    dx2 = -0.6f,
                    dy2 = -1.5f,
                    dx3 = -1.5f,
                    dy3 = -1.5f,
                )
                // s -1.5 0.6 -1.5 1.5
                reflectiveCurveToRelative(
                    dx1 = -1.5f,
                    dy1 = 0.6f,
                    dx2 = -1.5f,
                    dy2 = 1.5f,
                )
                // S 11.1 18 12 18
                reflectiveCurveTo(
                    x1 = 11.1f,
                    y1 = 18.0f,
                    x2 = 12.0f,
                    y2 = 18.0f,
                )
                // s 1.5 -0.6 1.5 -1.5
                reflectiveCurveToRelative(
                    dx1 = 1.5f,
                    dy1 = -0.6f,
                    dx2 = 1.5f,
                    dy2 = -1.5f,
                )
                // m -0.5 -3
                moveToRelative(dx = -0.5f, dy = -3.0f)
                // a 0.5 0.5 0 0 0 0.5 -0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.5f,
                    dy1 = -0.5f,
                )
                // V 6.5
                verticalLineTo(y = 6.5f)
                // A 0.5 0.5 0 0 0 13 6
                arcTo(
                    horizontalEllipseRadius = 0.5f,
                    verticalEllipseRadius = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 13.0f,
                    y1 = 6.0f,
                )
                // h -2
                horizontalLineToRelative(dx = -2.0f)
                // a 0.5 0.5 0 0 0 -0.5 0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.5f,
                    dy1 = 0.5f,
                )
                // V 13
                verticalLineTo(y = 13.0f)
                // a 0.5 0.5 0 0 0 0.5 0.5z
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.5f,
                    dy1 = 0.5f,
                )
                close()
            }
        }.build().also { _errorSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _errorSolid: ImageVector? = null
