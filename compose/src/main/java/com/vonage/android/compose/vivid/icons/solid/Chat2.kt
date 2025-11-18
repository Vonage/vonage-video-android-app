@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Chat2: ImageVector
    get() {
        val current = _chat2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Chat2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
            autoMirror = true,
        ).apply {
            // M0 8.25 C0 3.69 4.37 0 9.75 0 s9.75 3.7 9.75 8.25 -4.36 8.25 -9.75 8.25 c-1.5 0 -2.93 -.3 -4.2 -.81 L1.5 18 v-5.37 A7.3 7.3 0 0 1 0 8.25 m22.28 2.04 A5.7 5.7 0 0 1 24 14.25 5.6 5.6 0 0 1 22.48 18 h.02 v6 l-5.54 -3.17 A12 12 0 0 1 15 21 c-2.17 0 -4.15 -.57 -5.7 -1.53 l.2 .01 .25 .02 c6.24 0 11.44 -3.98 12.53 -9.2
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 0 8.25
                moveTo(x = 0.0f, y = 8.25f)
                // C 0 3.69 4.37 0 9.75 0
                curveTo(
                    x1 = 0.0f,
                    y1 = 3.69f,
                    x2 = 4.37f,
                    y2 = 0.0f,
                    x3 = 9.75f,
                    y3 = 0.0f,
                )
                // s 9.75 3.7 9.75 8.25
                reflectiveCurveToRelative(
                    dx1 = 9.75f,
                    dy1 = 3.7f,
                    dx2 = 9.75f,
                    dy2 = 8.25f,
                )
                // s -4.36 8.25 -9.75 8.25
                reflectiveCurveToRelative(
                    dx1 = -4.36f,
                    dy1 = 8.25f,
                    dx2 = -9.75f,
                    dy2 = 8.25f,
                )
                // c -1.5 0 -2.93 -0.3 -4.2 -0.81
                curveToRelative(
                    dx1 = -1.5f,
                    dy1 = 0.0f,
                    dx2 = -2.93f,
                    dy2 = -0.3f,
                    dx3 = -4.2f,
                    dy3 = -0.81f,
                )
                // L 1.5 18
                lineTo(x = 1.5f, y = 18.0f)
                // v -5.37
                verticalLineToRelative(dy = -5.37f)
                // A 7.3 7.3 0 0 1 0 8.25
                arcTo(
                    horizontalEllipseRadius = 7.3f,
                    verticalEllipseRadius = 7.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 0.0f,
                    y1 = 8.25f,
                )
                // m 22.28 2.04
                moveToRelative(dx = 22.28f, dy = 2.04f)
                // A 5.7 5.7 0 0 1 24 14.25
                arcTo(
                    horizontalEllipseRadius = 5.7f,
                    verticalEllipseRadius = 5.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 24.0f,
                    y1 = 14.25f,
                )
                // A 5.6 5.6 0 0 1 22.48 18
                arcTo(
                    horizontalEllipseRadius = 5.6f,
                    verticalEllipseRadius = 5.6f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 22.48f,
                    y1 = 18.0f,
                )
                // h 0.02
                horizontalLineToRelative(dx = 0.02f)
                // v 6
                verticalLineToRelative(dy = 6.0f)
                // l -5.54 -3.17
                lineToRelative(dx = -5.54f, dy = -3.17f)
                // A 12 12 0 0 1 15 21
                arcTo(
                    horizontalEllipseRadius = 12.0f,
                    verticalEllipseRadius = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 15.0f,
                    y1 = 21.0f,
                )
                // c -2.17 0 -4.15 -0.57 -5.7 -1.53
                curveToRelative(
                    dx1 = -2.17f,
                    dy1 = 0.0f,
                    dx2 = -4.15f,
                    dy2 = -0.57f,
                    dx3 = -5.7f,
                    dy3 = -1.53f,
                )
                // l 0.2 0.01
                lineToRelative(dx = 0.2f, dy = 0.01f)
                // l 0.25 0.02
                lineToRelative(dx = 0.25f, dy = 0.02f)
                // c 6.24 0 11.44 -3.98 12.53 -9.2
                curveToRelative(
                    dx1 = 6.24f,
                    dy1 = 0.0f,
                    dx2 = 11.44f,
                    dy2 = -3.98f,
                    dx3 = 12.53f,
                    dy3 = -9.2f,
                )
            }
        }.build().also { _chat2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _chat2Solid: ImageVector? = null
