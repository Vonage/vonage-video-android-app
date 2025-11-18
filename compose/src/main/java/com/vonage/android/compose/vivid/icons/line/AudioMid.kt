package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.AudioMid: ImageVector
    get() {
        val current = _audioMidLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.AudioMid",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M2.2 7.74 h2.21 l5.78 -4.86 c.33 -.28 .84 -.05 .84 .37 v17.5 c0 .42 -.51 .65 -.84 .37 l-5.78 -4.86 h-2.2 A2.2 2.2 0 0 1 0 14.07 V9.93 a2.2 2.2 0 0 1 2.2 -2.19 m0 1.48 h2.78 L9.5 5.4 v13.2 l-4.53 -3.82 H2.2 a.7 .7 0 0 1 -.7 -.7 V9.92 a.7 .7 0 0 1 .7 -.71
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 2.2 7.74
                moveTo(x = 2.2f, y = 7.74f)
                // h 2.21
                horizontalLineToRelative(dx = 2.21f)
                // l 5.78 -4.86
                lineToRelative(dx = 5.78f, dy = -4.86f)
                // c 0.33 -0.28 0.84 -0.05 0.84 0.37
                curveToRelative(
                    dx1 = 0.33f,
                    dy1 = -0.28f,
                    dx2 = 0.84f,
                    dy2 = -0.05f,
                    dx3 = 0.84f,
                    dy3 = 0.37f,
                )
                // v 17.5
                verticalLineToRelative(dy = 17.5f)
                // c 0 0.42 -0.51 0.65 -0.84 0.37
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.42f,
                    dx2 = -0.51f,
                    dy2 = 0.65f,
                    dx3 = -0.84f,
                    dy3 = 0.37f,
                )
                // l -5.78 -4.86
                lineToRelative(dx = -5.78f, dy = -4.86f)
                // h -2.2
                horizontalLineToRelative(dx = -2.2f)
                // A 2.2 2.2 0 0 1 0 14.07
                arcTo(
                    horizontalEllipseRadius = 2.2f,
                    verticalEllipseRadius = 2.2f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 0.0f,
                    y1 = 14.07f,
                )
                // V 9.93
                verticalLineTo(y = 9.93f)
                // a 2.2 2.2 0 0 1 2.2 -2.19
                arcToRelative(
                    a = 2.2f,
                    b = 2.2f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.2f,
                    dy1 = -2.19f,
                )
                // m 0 1.48
                moveToRelative(dx = 0.0f, dy = 1.48f)
                // h 2.78
                horizontalLineToRelative(dx = 2.78f)
                // L 9.5 5.4
                lineTo(x = 9.5f, y = 5.4f)
                // v 13.2
                verticalLineToRelative(dy = 13.2f)
                // l -4.53 -3.82
                lineToRelative(dx = -4.53f, dy = -3.82f)
                // H 2.2
                horizontalLineTo(x = 2.2f)
                // a 0.7 0.7 0 0 1 -0.7 -0.7
                arcToRelative(
                    a = 0.7f,
                    b = 0.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.7f,
                    dy1 = -0.7f,
                )
                // V 9.92
                verticalLineTo(y = 9.92f)
                // a 0.7 0.7 0 0 1 0.7 -0.71
                arcToRelative(
                    a = 0.7f,
                    b = 0.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.7f,
                    dy1 = -0.71f,
                )
            }
            // M14.69 8.4 a.77 .77 0 0 0 -1.07 -.01 .7 .7 0 0 0 -.01 1.04 3.63 3.63 0 0 1 0 5.14 .7 .7 0 0 0 0 1.05 .77 .77 0 0 0 1.08 -.01 c2 -1.99 2 -5.23 0 -7.21 m1.57 -3.1 a.77 .77 0 0 1 1.08 0 9.4 9.4 0 0 1 0 13.4 .77 .77 0 0 1 -1.08 0 .7 .7 0 0 1 0 -1.04 7.96 7.96 0 0 0 0 -11.32 .7 .7 0 0 1 0 -1.04
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 14.69 8.4
                moveTo(x = 14.69f, y = 8.4f)
                // a 0.77 0.77 0 0 0 -1.07 -0.01
                arcToRelative(
                    a = 0.77f,
                    b = 0.77f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.07f,
                    dy1 = -0.01f,
                )
                // a 0.7 0.7 0 0 0 -0.01 1.04
                arcToRelative(
                    a = 0.7f,
                    b = 0.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.01f,
                    dy1 = 1.04f,
                )
                // a 3.63 3.63 0 0 1 0 5.14
                arcToRelative(
                    a = 3.63f,
                    b = 3.63f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 5.14f,
                )
                // a 0.7 0.7 0 0 0 0 1.05
                arcToRelative(
                    a = 0.7f,
                    b = 0.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 1.05f,
                )
                // a 0.77 0.77 0 0 0 1.08 -0.01
                arcToRelative(
                    a = 0.77f,
                    b = 0.77f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.08f,
                    dy1 = -0.01f,
                )
                // c 2 -1.99 2 -5.23 0 -7.21
                curveToRelative(
                    dx1 = 2.0f,
                    dy1 = -1.99f,
                    dx2 = 2.0f,
                    dy2 = -5.23f,
                    dx3 = 0.0f,
                    dy3 = -7.21f,
                )
                // m 1.57 -3.1
                moveToRelative(dx = 1.57f, dy = -3.1f)
                // a 0.77 0.77 0 0 1 1.08 0
                arcToRelative(
                    a = 0.77f,
                    b = 0.77f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.08f,
                    dy1 = 0.0f,
                )
                // a 9.4 9.4 0 0 1 0 13.4
                arcToRelative(
                    a = 9.4f,
                    b = 9.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 13.4f,
                )
                // a 0.77 0.77 0 0 1 -1.08 0
                arcToRelative(
                    a = 0.77f,
                    b = 0.77f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.08f,
                    dy1 = 0.0f,
                )
                // a 0.7 0.7 0 0 1 0 -1.04
                arcToRelative(
                    a = 0.7f,
                    b = 0.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.04f,
                )
                // a 7.96 7.96 0 0 0 0 -11.32
                arcToRelative(
                    a = 7.96f,
                    b = 7.96f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -11.32f,
                )
                // a 0.7 0.7 0 0 1 0 -1.04
                arcToRelative(
                    a = 0.7f,
                    b = 0.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.04f,
                )
            }
        }.build().also { _audioMidLine = it }
    }

@Suppress("ObjectPropertyName")
private var _audioMidLine: ImageVector? = null
