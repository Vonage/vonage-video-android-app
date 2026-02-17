@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.MicMute: ImageVector
    get() {
        val current = _micMuteSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.MicMute",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M12 0 a6.5 6.5 0 0 1 4.08 1.42 L5.84 11.66 A6 6 0 0 1 5.5 9.6 V6.4 C5.5 2.87 8.41 0 12 0 M3 10.33 q.02 1.81 .82 3.35 L1.7 15.8 A9.7 9.7 0 0 1 0 10.33 C0 9.6 .67 9 1.5 9 S3 9.6 3 10.33 M.44 23.56 a1.5 1.5 0 0 1 0 -2.12 l21 -21 a1.5 1.5 0 1 1 2.12 2.12 L18.5 7.62 V9.6 a6.45 6.45 0 0 1 -8.17 6.19 l-1.9 1.9 A10 10 0 0 0 12 18.32 c4.97 0 9 -3.58 9 -8 C21 9.6 21.67 9 22.5 9 S24 9.6 24 10.33 c0 5.44 -4.58 9.93 -10.5 10.59 V24 h-3 v-3.08 a13 13 0 0 1 -4.13 -1.17 l-3.8 3.81 a1.5 1.5 0 0 1 -2.13 0
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 12 0
                moveTo(x = 12.0f, y = 0.0f)
                // a 6.5 6.5 0 0 1 4.08 1.42
                arcToRelative(
                    a = 6.5f,
                    b = 6.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 4.08f,
                    dy1 = 1.42f,
                )
                // L 5.84 11.66
                lineTo(x = 5.84f, y = 11.66f)
                // A 6 6 0 0 1 5.5 9.6
                arcTo(
                    horizontalEllipseRadius = 6.0f,
                    verticalEllipseRadius = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 5.5f,
                    y1 = 9.6f,
                )
                // V 6.4
                verticalLineTo(y = 6.4f)
                // C 5.5 2.87 8.41 0 12 0
                curveTo(
                    x1 = 5.5f,
                    y1 = 2.87f,
                    x2 = 8.41f,
                    y2 = 0.0f,
                    x3 = 12.0f,
                    y3 = 0.0f,
                )
                // M 3 10.33
                moveTo(x = 3.0f, y = 10.33f)
                // q 0.02 1.81 0.82 3.35
                quadToRelative(
                    dx1 = 0.02f,
                    dy1 = 1.81f,
                    dx2 = 0.82f,
                    dy2 = 3.35f,
                )
                // L 1.7 15.8
                lineTo(x = 1.7f, y = 15.8f)
                // A 9.7 9.7 0 0 1 0 10.33
                arcTo(
                    horizontalEllipseRadius = 9.7f,
                    verticalEllipseRadius = 9.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 0.0f,
                    y1 = 10.33f,
                )
                // C 0 9.6 0.67 9 1.5 9
                curveTo(
                    x1 = 0.0f,
                    y1 = 9.6f,
                    x2 = 0.67f,
                    y2 = 9.0f,
                    x3 = 1.5f,
                    y3 = 9.0f,
                )
                // S 3 9.6 3 10.33
                reflectiveCurveTo(
                    x1 = 3.0f,
                    y1 = 9.6f,
                    x2 = 3.0f,
                    y2 = 10.33f,
                )
                // M 0.44 23.56
                moveTo(x = 0.44f, y = 23.56f)
                // a 1.5 1.5 0 0 1 0 -2.12
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -2.12f,
                )
                // l 21 -21
                lineToRelative(dx = 21.0f, dy = -21.0f)
                // a 1.5 1.5 0 1 1 2.12 2.12
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 2.12f,
                    dy1 = 2.12f,
                )
                // L 18.5 7.62
                lineTo(x = 18.5f, y = 7.62f)
                // V 9.6
                verticalLineTo(y = 9.6f)
                // a 6.45 6.45 0 0 1 -8.17 6.19
                arcToRelative(
                    a = 6.45f,
                    b = 6.45f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -8.17f,
                    dy1 = 6.19f,
                )
                // l -1.9 1.9
                lineToRelative(dx = -1.9f, dy = 1.9f)
                // A 10 10 0 0 0 12 18.32
                arcTo(
                    horizontalEllipseRadius = 10.0f,
                    verticalEllipseRadius = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 12.0f,
                    y1 = 18.32f,
                )
                // c 4.97 0 9 -3.58 9 -8
                curveToRelative(
                    dx1 = 4.97f,
                    dy1 = 0.0f,
                    dx2 = 9.0f,
                    dy2 = -3.58f,
                    dx3 = 9.0f,
                    dy3 = -8.0f,
                )
                // C 21 9.6 21.67 9 22.5 9
                curveTo(
                    x1 = 21.0f,
                    y1 = 9.6f,
                    x2 = 21.67f,
                    y2 = 9.0f,
                    x3 = 22.5f,
                    y3 = 9.0f,
                )
                // S 24 9.6 24 10.33
                reflectiveCurveTo(
                    x1 = 24.0f,
                    y1 = 9.6f,
                    x2 = 24.0f,
                    y2 = 10.33f,
                )
                // c 0 5.44 -4.58 9.93 -10.5 10.59
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 5.44f,
                    dx2 = -4.58f,
                    dy2 = 9.93f,
                    dx3 = -10.5f,
                    dy3 = 10.59f,
                )
                // V 24
                verticalLineTo(y = 24.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // v -3.08
                verticalLineToRelative(dy = -3.08f)
                // a 13 13 0 0 1 -4.13 -1.17
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -4.13f,
                    dy1 = -1.17f,
                )
                // l -3.8 3.81
                lineToRelative(dx = -3.8f, dy = 3.81f)
                // a 1.5 1.5 0 0 1 -2.13 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.13f,
                    dy1 = 0.0f,
                )
            }
        }.build().also { _micMuteSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _micMuteSolid: ImageVector? = null
