package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Call: ImageVector
    get() {
        val current = _callSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Call",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M4.02 .06 a2 2 0 0 1 1.27 .1 2.3 2.3 0 0 1 1.02 .96 L7.9 3.59 a8 8 0 0 1 1 2.03 3.2 3.2 0 0 1 .06 1.86 C8.72 8.3 8.35 9.06 7.66 9.46 a1 1 0 0 0 .04 .4 V9.9 a5 5 0 0 0 1.08 1.89 29 29 0 0 0 1.6 1.81 27 27 0 0 0 1.84 1.6 5 5 0 0 0 1.91 1.11 1 1 0 0 0 .41 .04 c.4 -.68 1.15 -1.07 1.97 -1.32 h.02 c1.15 -.33 2.5 .2 3.88 1.07 a83 83 0 0 1 2.49 1.6 2.3 2.3 0 0 1 .94 1.02 2 2 0 0 1 .1 1.27 l-.02 .05 -.46 1.23 a4.3 4.3 0 0 1 -2.28 2.35 h-.02 a5.4 5.4 0 0 1 -3.5 .19 17 17 0 0 1 -3.87 -1.5 30 30 0 0 1 -7 -5.08 29.8 29.8 0 0 1 -5.1 -7 A16 16 0 0 1 .2 6.33 a5.4 5.4 0 0 1 .19 -3.5 V2.82 a4.3 4.3 0 0 1 2.35 -2.28 l1.23 -.46z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 4.02 0.06
                moveTo(x = 4.02f, y = 0.06f)
                // a 2 2 0 0 1 1.27 0.1
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.27f,
                    dy1 = 0.1f,
                )
                // a 2.3 2.3 0 0 1 1.02 0.96
                arcToRelative(
                    a = 2.3f,
                    b = 2.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.02f,
                    dy1 = 0.96f,
                )
                // L 7.9 3.59
                lineTo(x = 7.9f, y = 3.59f)
                // a 8 8 0 0 1 1 2.03
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.0f,
                    dy1 = 2.03f,
                )
                // a 3.2 3.2 0 0 1 0.06 1.86
                arcToRelative(
                    a = 3.2f,
                    b = 3.2f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.06f,
                    dy1 = 1.86f,
                )
                // C 8.72 8.3 8.35 9.06 7.66 9.46
                curveTo(
                    x1 = 8.72f,
                    y1 = 8.3f,
                    x2 = 8.35f,
                    y2 = 9.06f,
                    x3 = 7.66f,
                    y3 = 9.46f,
                )
                // a 1 1 0 0 0 0.04 0.4
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.04f,
                    dy1 = 0.4f,
                )
                // V 9.9
                verticalLineTo(y = 9.9f)
                // a 5 5 0 0 0 1.08 1.89
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.08f,
                    dy1 = 1.89f,
                )
                // a 29 29 0 0 0 1.6 1.81
                arcToRelative(
                    a = 29.0f,
                    b = 29.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.6f,
                    dy1 = 1.81f,
                )
                // a 27 27 0 0 0 1.84 1.6
                arcToRelative(
                    a = 27.0f,
                    b = 27.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.84f,
                    dy1 = 1.6f,
                )
                // a 5 5 0 0 0 1.91 1.11
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.91f,
                    dy1 = 1.11f,
                )
                // a 1 1 0 0 0 0.41 0.04
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.41f,
                    dy1 = 0.04f,
                )
                // c 0.4 -0.68 1.15 -1.07 1.97 -1.32
                curveToRelative(
                    dx1 = 0.4f,
                    dy1 = -0.68f,
                    dx2 = 1.15f,
                    dy2 = -1.07f,
                    dx3 = 1.97f,
                    dy3 = -1.32f,
                )
                // h 0.02
                horizontalLineToRelative(dx = 0.02f)
                // c 1.15 -0.33 2.5 0.2 3.88 1.07
                curveToRelative(
                    dx1 = 1.15f,
                    dy1 = -0.33f,
                    dx2 = 2.5f,
                    dy2 = 0.2f,
                    dx3 = 3.88f,
                    dy3 = 1.07f,
                )
                // a 83 83 0 0 1 2.49 1.6
                arcToRelative(
                    a = 83.0f,
                    b = 83.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.49f,
                    dy1 = 1.6f,
                )
                // a 2.3 2.3 0 0 1 0.94 1.02
                arcToRelative(
                    a = 2.3f,
                    b = 2.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.94f,
                    dy1 = 1.02f,
                )
                // a 2 2 0 0 1 0.1 1.27
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.1f,
                    dy1 = 1.27f,
                )
                // l -0.02 0.05
                lineToRelative(dx = -0.02f, dy = 0.05f)
                // l -0.46 1.23
                lineToRelative(dx = -0.46f, dy = 1.23f)
                // a 4.3 4.3 0 0 1 -2.28 2.35
                arcToRelative(
                    a = 4.3f,
                    b = 4.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.28f,
                    dy1 = 2.35f,
                )
                // h -0.02
                horizontalLineToRelative(dx = -0.02f)
                // a 5.4 5.4 0 0 1 -3.5 0.19
                arcToRelative(
                    a = 5.4f,
                    b = 5.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.5f,
                    dy1 = 0.19f,
                )
                // a 17 17 0 0 1 -3.87 -1.5
                arcToRelative(
                    a = 17.0f,
                    b = 17.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.87f,
                    dy1 = -1.5f,
                )
                // a 30 30 0 0 1 -7 -5.08
                arcToRelative(
                    a = 30.0f,
                    b = 30.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -7.0f,
                    dy1 = -5.08f,
                )
                // a 29.8 29.8 0 0 1 -5.1 -7
                arcToRelative(
                    a = 29.8f,
                    b = 29.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -5.1f,
                    dy1 = -7.0f,
                )
                // A 16 16 0 0 1 0.2 6.33
                arcTo(
                    horizontalEllipseRadius = 16.0f,
                    verticalEllipseRadius = 16.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 0.2f,
                    y1 = 6.33f,
                )
                // a 5.4 5.4 0 0 1 0.19 -3.5
                arcToRelative(
                    a = 5.4f,
                    b = 5.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.19f,
                    dy1 = -3.5f,
                )
                // V 2.82
                verticalLineTo(y = 2.82f)
                // a 4.3 4.3 0 0 1 2.35 -2.28
                arcToRelative(
                    a = 4.3f,
                    b = 4.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.35f,
                    dy1 = -2.28f,
                )
                // l 1.23 -0.46z
                lineToRelative(dx = 1.23f, dy = -0.46f)
                close()
            }
        }.build().also { _callSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _callSolid: ImageVector? = null
