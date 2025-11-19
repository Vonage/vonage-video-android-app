@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.ClosedCaptioningOff: ImageVector
    get() {
        val current = _closedCaptioningOffSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.ClosedCaptioningOff",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M3.06 .94 a1.5 1.5 0 1 0 -2.12 2.12 l20 20 A1.5 1.5 0 0 0 23.1 21 1 1 0 0 0 24 20 V4 a1 1 0 0 0 -1 -1 H5.12z m7.12 7.12 1.11 1.11 .26 -.66 a6 6 0 0 0 -1.37 -.45 m2.48 2.48 2 2 -.03 -.5 a3 3 0 0 1 .42 -1.72 1.4 1.4 0 0 1 1.18 -.62 3 3 0 0 1 .94 .14 7 7 0 0 1 .87 .34 l.65 -1.67 a6 6 0 0 0 -2.43 -.55 4 4 0 0 0 -2.03 .5 3.3 3.3 0 0 0 -1.32 1.42 5 5 0 0 0 -.25 .66 m3.76 3.76 1.47 1.47 .46 -.17 v-1.8 a10 10 0 0 1 -.99 .35 4 4 0 0 1 -.94 .15
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 3.06 0.94
                moveTo(x = 3.06f, y = 0.94f)
                // a 1.5 1.5 0 1 0 -2.12 2.12
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = -2.12f,
                    dy1 = 2.12f,
                )
                // l 20 20
                lineToRelative(dx = 20.0f, dy = 20.0f)
                // A 1.5 1.5 0 0 0 23.1 21
                arcTo(
                    horizontalEllipseRadius = 1.5f,
                    verticalEllipseRadius = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 23.1f,
                    y1 = 21.0f,
                )
                // A 1 1 0 0 0 24 20
                arcTo(
                    horizontalEllipseRadius = 1.0f,
                    verticalEllipseRadius = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 24.0f,
                    y1 = 20.0f,
                )
                // V 4
                verticalLineTo(y = 4.0f)
                // a 1 1 0 0 0 -1 -1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.0f,
                    dy1 = -1.0f,
                )
                // H 5.12z
                horizontalLineTo(x = 5.12f)
                close()
                // m 7.12 7.12
                moveToRelative(dx = 7.12f, dy = 7.12f)
                // l 1.11 1.11
                lineToRelative(dx = 1.11f, dy = 1.11f)
                // l 0.26 -0.66
                lineToRelative(dx = 0.26f, dy = -0.66f)
                // a 6 6 0 0 0 -1.37 -0.45
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.37f,
                    dy1 = -0.45f,
                )
                // m 2.48 2.48
                moveToRelative(dx = 2.48f, dy = 2.48f)
                // l 2 2
                lineToRelative(dx = 2.0f, dy = 2.0f)
                // l -0.03 -0.5
                lineToRelative(dx = -0.03f, dy = -0.5f)
                // a 3 3 0 0 1 0.42 -1.72
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.42f,
                    dy1 = -1.72f,
                )
                // a 1.4 1.4 0 0 1 1.18 -0.62
                arcToRelative(
                    a = 1.4f,
                    b = 1.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.18f,
                    dy1 = -0.62f,
                )
                // a 3 3 0 0 1 0.94 0.14
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.94f,
                    dy1 = 0.14f,
                )
                // a 7 7 0 0 1 0.87 0.34
                arcToRelative(
                    a = 7.0f,
                    b = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.87f,
                    dy1 = 0.34f,
                )
                // l 0.65 -1.67
                lineToRelative(dx = 0.65f, dy = -1.67f)
                // a 6 6 0 0 0 -2.43 -0.55
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.43f,
                    dy1 = -0.55f,
                )
                // a 4 4 0 0 0 -2.03 0.5
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.03f,
                    dy1 = 0.5f,
                )
                // a 3.3 3.3 0 0 0 -1.32 1.42
                arcToRelative(
                    a = 3.3f,
                    b = 3.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.32f,
                    dy1 = 1.42f,
                )
                // a 5 5 0 0 0 -0.25 0.66
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.25f,
                    dy1 = 0.66f,
                )
                // m 3.76 3.76
                moveToRelative(dx = 3.76f, dy = 3.76f)
                // l 1.47 1.47
                lineToRelative(dx = 1.47f, dy = 1.47f)
                // l 0.46 -0.17
                lineToRelative(dx = 0.46f, dy = -0.17f)
                // v -1.8
                verticalLineToRelative(dy = -1.8f)
                // a 10 10 0 0 1 -0.99 0.35
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.99f,
                    dy1 = 0.35f,
                )
                // a 4 4 0 0 1 -0.94 0.15
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.94f,
                    dy1 = 0.15f,
                )
            }
            // m15 21 -5.04 -5.04 a6 6 0 0 1 -.98 .07 A3.5 3.5 0 0 1 6.26 15 a4.3 4.3 0 0 1 -.95 -2.98 q0 -.33 .03 -.68 L0 6 v14 a1 1 0 0 0 1 1z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 15 21
                moveTo(x = 15.0f, y = 21.0f)
                // l -5.04 -5.04
                lineToRelative(dx = -5.04f, dy = -5.04f)
                // a 6 6 0 0 1 -0.98 0.07
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.98f,
                    dy1 = 0.07f,
                )
                // A 3.5 3.5 0 0 1 6.26 15
                arcTo(
                    horizontalEllipseRadius = 3.5f,
                    verticalEllipseRadius = 3.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 6.26f,
                    y1 = 15.0f,
                )
                // a 4.3 4.3 0 0 1 -0.95 -2.98
                arcToRelative(
                    a = 4.3f,
                    b = 4.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.95f,
                    dy1 = -2.98f,
                )
                // q 0 -0.33 0.03 -0.68
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.33f,
                    dx2 = 0.03f,
                    dy2 = -0.68f,
                )
                // L 0 6
                lineTo(x = 0.0f, y = 6.0f)
                // v 14
                verticalLineToRelative(dy = 14.0f)
                // a 1 1 0 0 0 1 1z
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.0f,
                    dy1 = 1.0f,
                )
                close()
            }
        }.build().also { _closedCaptioningOffSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _closedCaptioningOffSolid: ImageVector? = null
