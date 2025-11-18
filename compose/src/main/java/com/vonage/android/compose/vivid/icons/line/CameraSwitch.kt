@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.CameraSwitch: ImageVector
    get() {
        val current = _cameraSwitchLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.CameraSwitch",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M8.36 7.58 a.75 .75 0 0 1 1.06 0 l3.15 3.15 h-3.3 a3 3 0 1 0 0 6 .75 .75 0 1 1 0 1.5 4.5 4.5 0 0 1 -.3 -8.99 l-.61 -.6 a.75 .75 0 0 1 0 -1.06 m7.28 12.3 a.75 .75 0 0 1 -1.06 0 l-3.15 -3.15 h3.3 a3 3 0 0 0 0 -6 .75 .75 0 0 1 0 -1.5 4.5 4.5 0 0 1 .3 9 l.61 .6 a.75 .75 0 0 1 0 1.06
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 8.36 7.58
                moveTo(x = 8.36f, y = 7.58f)
                // a 0.75 0.75 0 0 1 1.06 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.06f,
                    dy1 = 0.0f,
                )
                // l 3.15 3.15
                lineToRelative(dx = 3.15f, dy = 3.15f)
                // h -3.3
                horizontalLineToRelative(dx = -3.3f)
                // a 3 3 0 1 0 0 6
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 6.0f,
                )
                // a 0.75 0.75 0 1 1 0 1.5
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 1.5f,
                )
                // a 4.5 4.5 0 0 1 -0.3 -8.99
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.3f,
                    dy1 = -8.99f,
                )
                // l -0.61 -0.6
                lineToRelative(dx = -0.61f, dy = -0.6f)
                // a 0.75 0.75 0 0 1 0 -1.06
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.06f,
                )
                // m 7.28 12.3
                moveToRelative(dx = 7.28f, dy = 12.3f)
                // a 0.75 0.75 0 0 1 -1.06 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.06f,
                    dy1 = 0.0f,
                )
                // l -3.15 -3.15
                lineToRelative(dx = -3.15f, dy = -3.15f)
                // h 3.3
                horizontalLineToRelative(dx = 3.3f)
                // a 3 3 0 0 0 0 -6
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -6.0f,
                )
                // a 0.75 0.75 0 0 1 0 -1.5
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.5f,
                )
                // a 4.5 4.5 0 0 1 0.3 9
                arcToRelative(
                    a = 4.5f,
                    b = 4.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.3f,
                    dy1 = 9.0f,
                )
                // l 0.61 0.6
                lineToRelative(dx = 0.61f, dy = 0.6f)
                // a 0.75 0.75 0 0 1 0 1.06
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 1.06f,
                )
            }
            // M18.75 5.5 16.2 1.6 C15.9 1.3 15.45 1 15 1 H9 C8.55 1 8.1 1.3 7.8 1.6 L5.25 5.5 H1.5 C.6 5.5 0 6.1 0 7 v14.5 C0 22.4 .6 23 1.5 23 h21 c.9 0 1.5 -.6 1.5 -1.5 V7 c0 -.9 -.6 -1.5 -1.5 -1.5z m-3.7 -2.92 L17.94 7 h4.56 v14.5 h-21 V7 h4.56 l2.9 -4.42 V2.57 l.1 -.07 h5.87 l.1 .07z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 18.75 5.5
                moveTo(x = 18.75f, y = 5.5f)
                // L 16.2 1.6
                lineTo(x = 16.2f, y = 1.6f)
                // C 15.9 1.3 15.45 1 15 1
                curveTo(
                    x1 = 15.9f,
                    y1 = 1.3f,
                    x2 = 15.45f,
                    y2 = 1.0f,
                    x3 = 15.0f,
                    y3 = 1.0f,
                )
                // H 9
                horizontalLineTo(x = 9.0f)
                // C 8.55 1 8.1 1.3 7.8 1.6
                curveTo(
                    x1 = 8.55f,
                    y1 = 1.0f,
                    x2 = 8.1f,
                    y2 = 1.3f,
                    x3 = 7.8f,
                    y3 = 1.6f,
                )
                // L 5.25 5.5
                lineTo(x = 5.25f, y = 5.5f)
                // H 1.5
                horizontalLineTo(x = 1.5f)
                // C 0.6 5.5 0 6.1 0 7
                curveTo(
                    x1 = 0.6f,
                    y1 = 5.5f,
                    x2 = 0.0f,
                    y2 = 6.1f,
                    x3 = 0.0f,
                    y3 = 7.0f,
                )
                // v 14.5
                verticalLineToRelative(dy = 14.5f)
                // C 0 22.4 0.6 23 1.5 23
                curveTo(
                    x1 = 0.0f,
                    y1 = 22.4f,
                    x2 = 0.6f,
                    y2 = 23.0f,
                    x3 = 1.5f,
                    y3 = 23.0f,
                )
                // h 21
                horizontalLineToRelative(dx = 21.0f)
                // c 0.9 0 1.5 -0.6 1.5 -1.5
                curveToRelative(
                    dx1 = 0.9f,
                    dy1 = 0.0f,
                    dx2 = 1.5f,
                    dy2 = -0.6f,
                    dx3 = 1.5f,
                    dy3 = -1.5f,
                )
                // V 7
                verticalLineTo(y = 7.0f)
                // c 0 -0.9 -0.6 -1.5 -1.5 -1.5z
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.9f,
                    dx2 = -0.6f,
                    dy2 = -1.5f,
                    dx3 = -1.5f,
                    dy3 = -1.5f,
                )
                close()
                // m -3.7 -2.92
                moveToRelative(dx = -3.7f, dy = -2.92f)
                // L 17.94 7
                lineTo(x = 17.94f, y = 7.0f)
                // h 4.56
                horizontalLineToRelative(dx = 4.56f)
                // v 14.5
                verticalLineToRelative(dy = 14.5f)
                // h -21
                horizontalLineToRelative(dx = -21.0f)
                // V 7
                verticalLineTo(y = 7.0f)
                // h 4.56
                horizontalLineToRelative(dx = 4.56f)
                // l 2.9 -4.42
                lineToRelative(dx = 2.9f, dy = -4.42f)
                // V 2.57
                verticalLineTo(y = 2.57f)
                // l 0.1 -0.07
                lineToRelative(dx = 0.1f, dy = -0.07f)
                // h 5.87
                horizontalLineToRelative(dx = 5.87f)
                // l 0.1 0.07z
                lineToRelative(dx = 0.1f, dy = 0.07f)
                close()
            }
        }.build().also { _cameraSwitchLine = it }
    }

@Suppress("ObjectPropertyName")
private var _cameraSwitchLine: ImageVector? = null
