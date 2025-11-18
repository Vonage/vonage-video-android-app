@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.ScreenShare: ImageVector
    get() {
        val current = _screenShareSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.ScreenShare",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M23 1 H1 a1 1 0 0 0 -1 1 v16 a1 1 0 0 0 1 1 h22 a1 1 0 0 0 1 -1 V2 a1 1 0 0 0 -1 -1 M13 14.5 a.5 .5 0 0 1 -.5 .5 h-1 a.5 .5 0 0 1 -.5 -.5 v-4 a.5 .5 0 0 0 -.5 -.5 H7.87 a.37 .37 0 0 1 -.29 -.6 l3.76 -4.7 a.54 .54 0 0 1 .82 -.02 l4.25 4.72 a.36 .36 0 0 1 -.27 .6 H13.5 a.5 .5 0 0 0 -.5 .5z M6 21 h12 c.9 0 1.5 .6 1.5 1.5 S18.9 24 18 24 H6 c-.9 0 -1.5 -.6 -1.5 -1.5 S5.1 21 6 21
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 23 1
                moveTo(x = 23.0f, y = 1.0f)
                // H 1
                horizontalLineTo(x = 1.0f)
                // a 1 1 0 0 0 -1 1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.0f,
                    dy1 = 1.0f,
                )
                // v 16
                verticalLineToRelative(dy = 16.0f)
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
                // h 22
                horizontalLineToRelative(dx = 22.0f)
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
                // M 13 14.5
                moveTo(x = 13.0f, y = 14.5f)
                // a 0.5 0.5 0 0 1 -0.5 0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.5f,
                    dy1 = 0.5f,
                )
                // h -1
                horizontalLineToRelative(dx = -1.0f)
                // a 0.5 0.5 0 0 1 -0.5 -0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.5f,
                    dy1 = -0.5f,
                )
                // v -4
                verticalLineToRelative(dy = -4.0f)
                // a 0.5 0.5 0 0 0 -0.5 -0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.5f,
                    dy1 = -0.5f,
                )
                // H 7.87
                horizontalLineTo(x = 7.87f)
                // a 0.37 0.37 0 0 1 -0.29 -0.6
                arcToRelative(
                    a = 0.37f,
                    b = 0.37f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.29f,
                    dy1 = -0.6f,
                )
                // l 3.76 -4.7
                lineToRelative(dx = 3.76f, dy = -4.7f)
                // a 0.54 0.54 0 0 1 0.82 -0.02
                arcToRelative(
                    a = 0.54f,
                    b = 0.54f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.82f,
                    dy1 = -0.02f,
                )
                // l 4.25 4.72
                lineToRelative(dx = 4.25f, dy = 4.72f)
                // a 0.36 0.36 0 0 1 -0.27 0.6
                arcToRelative(
                    a = 0.36f,
                    b = 0.36f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.27f,
                    dy1 = 0.6f,
                )
                // H 13.5
                horizontalLineTo(x = 13.5f)
                // a 0.5 0.5 0 0 0 -0.5 0.5z
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.5f,
                    dy1 = 0.5f,
                )
                close()
                // M 6 21
                moveTo(x = 6.0f, y = 21.0f)
                // h 12
                horizontalLineToRelative(dx = 12.0f)
                // c 0.9 0 1.5 0.6 1.5 1.5
                curveToRelative(
                    dx1 = 0.9f,
                    dy1 = 0.0f,
                    dx2 = 1.5f,
                    dy2 = 0.6f,
                    dx3 = 1.5f,
                    dy3 = 1.5f,
                )
                // S 18.9 24 18 24
                reflectiveCurveTo(
                    x1 = 18.9f,
                    y1 = 24.0f,
                    x2 = 18.0f,
                    y2 = 24.0f,
                )
                // H 6
                horizontalLineTo(x = 6.0f)
                // c -0.9 0 -1.5 -0.6 -1.5 -1.5
                curveToRelative(
                    dx1 = -0.9f,
                    dy1 = 0.0f,
                    dx2 = -1.5f,
                    dy2 = -0.6f,
                    dx3 = -1.5f,
                    dy3 = -1.5f,
                )
                // S 5.1 21 6 21
                reflectiveCurveTo(
                    x1 = 5.1f,
                    y1 = 21.0f,
                    x2 = 6.0f,
                    y2 = 21.0f,
                )
            }
        }.build().also { _screenShareSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _screenShareSolid: ImageVector? = null
