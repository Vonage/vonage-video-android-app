@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.ScreenShareOff: ImageVector
    get() {
        val current = _screenShareOffSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.ScreenShareOff",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M2.57 .45 a1.5 1.5 0 1 0 -2.14 2.1 l20.5 21 a1.5 1.5 0 0 0 2.14 -2.1 L20.68 19 H23 a1 1 0 0 0 1 -1 V2 a1 1 0 0 0 -1 -1 H3.1z M9.26 7.3 13 11.13 V10.5 a.5 .5 0 0 1 .5 -.5 h2.64 a.36 .36 0 0 0 .27 -.6 l-4.25 -4.72 a.54 .54 0 0 0 -.82 .02z M0 18 a1 1 0 0 0 1 1 h11.5 L0 6.5z m6 3 c-.9 0 -1.5 .6 -1.5 1.5 S5.1 24 6 24 h11.5 l-3 -3z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 2.57 0.45
                moveTo(x = 2.57f, y = 0.45f)
                // a 1.5 1.5 0 1 0 -2.14 2.1
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = -2.14f,
                    dy1 = 2.1f,
                )
                // l 20.5 21
                lineToRelative(dx = 20.5f, dy = 21.0f)
                // a 1.5 1.5 0 0 0 2.14 -2.1
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.14f,
                    dy1 = -2.1f,
                )
                // L 20.68 19
                lineTo(x = 20.68f, y = 19.0f)
                // H 23
                horizontalLineTo(x = 23.0f)
                // a 1 1 0 0 0 1 -1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.0f,
                    dy1 = -1.0f,
                )
                // V 2
                verticalLineTo(y = 2.0f)
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
                // H 3.1z
                horizontalLineTo(x = 3.1f)
                close()
                // M 9.26 7.3
                moveTo(x = 9.26f, y = 7.3f)
                // L 13 11.13
                lineTo(x = 13.0f, y = 11.13f)
                // V 10.5
                verticalLineTo(y = 10.5f)
                // a 0.5 0.5 0 0 1 0.5 -0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.5f,
                    dy1 = -0.5f,
                )
                // h 2.64
                horizontalLineToRelative(dx = 2.64f)
                // a 0.36 0.36 0 0 0 0.27 -0.6
                arcToRelative(
                    a = 0.36f,
                    b = 0.36f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.27f,
                    dy1 = -0.6f,
                )
                // l -4.25 -4.72
                lineToRelative(dx = -4.25f, dy = -4.72f)
                // a 0.54 0.54 0 0 0 -0.82 0.02z
                arcToRelative(
                    a = 0.54f,
                    b = 0.54f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.82f,
                    dy1 = 0.02f,
                )
                close()
                // M 0 18
                moveTo(x = 0.0f, y = 18.0f)
                // a 1 1 0 0 0 1 1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.0f,
                    dy1 = 1.0f,
                )
                // h 11.5
                horizontalLineToRelative(dx = 11.5f)
                // L 0 6.5z
                lineTo(x = 0.0f, y = 6.5f)
                close()
                // m 6 3
                moveToRelative(dx = 6.0f, dy = 3.0f)
                // c -0.9 0 -1.5 0.6 -1.5 1.5
                curveToRelative(
                    dx1 = -0.9f,
                    dy1 = 0.0f,
                    dx2 = -1.5f,
                    dy2 = 0.6f,
                    dx3 = -1.5f,
                    dy3 = 1.5f,
                )
                // S 5.1 24 6 24
                reflectiveCurveTo(
                    x1 = 5.1f,
                    y1 = 24.0f,
                    x2 = 6.0f,
                    y2 = 24.0f,
                )
                // h 11.5
                horizontalLineToRelative(dx = 11.5f)
                // l -3 -3z
                lineToRelative(dx = -3.0f, dy = -3.0f)
                close()
            }
        }.build().also { _screenShareOffSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _screenShareOffSolid: ImageVector? = null
