@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.AbcKeyboard: ImageVector
    get() {
        val current = _abcKeyboardSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.AbcKeyboard",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M4 2.5 a4 4 0 0 0 -4 4 v11 a4 4 0 0 0 4 4 h16 a4 4 0 0 0 4 -4 v-11 a4 4 0 0 0 -4 -4z M5.5 17 a1 1 0 0 1 1 -1 h11 a1 1 0 1 1 0 2 h-11 a1 1 0 0 1 -1 -1 M3.53 6 A.5 .5 0 0 0 3 6.5 V8 c0 .28 .24 .5 .53 .5 h1.44 C5.27 8.5 5.5 8.28 5.5 8 V6.5 C5.5 6.22 5.26 6 4.97 6z m15.5 0 a.5 .5 0 0 0 -.53 .5 V8 c0 .28 .24 .5 .53 .5 h1.44 C20.77 8.5 21 8.28 21 8 V6.5 C21 6.22 20.76 6 20.47 6z M3 11.5 C3 11.22 3.24 11 3.53 11 h1.44 c.3 0 .53 .22 .53 .5 V13 c0 .28 -.24 .5 -.53 .5 H3.53 A.5 .5 0 0 1 3 13z M8.62 6 a.5 .5 0 0 0 -.53 .5 V8 c0 .28 .24 .5 .53 .5 h1.35 c.3 0 .53 -.22 .53 -.5 V6.5 c0 -.28 -.24 -.5 -.53 -.5z m-.53 5.5 c0 -.28 .24 -.5 .53 -.5 h1.35 c.3 0 .53 .22 .53 .5 V13 c0 .28 -.24 .5 -.53 .5 H8.62 A.5 .5 0 0 1 8.09 13z M14 6 a.5 .5 0 0 0 -.53 .5 V8 c0 .28 .24 .5 .53 .5 h1.38 c.3 0 .53 -.22 .53 -.5 V6.5 c0 -.28 -.24 -.5 -.53 -.5z m-.53 5.5 c0 -.28 .24 -.5 .53 -.5 h1.38 c.3 0 .53 .22 .53 .5 V13 c0 .28 -.24 .5 -.53 .5 H14 a.5 .5 0 0 1 -.53 -.5z m5.56 -.5 a.5 .5 0 0 0 -.53 .5 V13 c0 .28 .24 .5 .53 .5 h1.44 c.3 0 .53 -.22 .53 -.5 v-1.5 c0 -.28 -.24 -.5 -.53 -.5z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 4 2.5
                moveTo(x = 4.0f, y = 2.5f)
                // a 4 4 0 0 0 -4 4
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -4.0f,
                    dy1 = 4.0f,
                )
                // v 11
                verticalLineToRelative(dy = 11.0f)
                // a 4 4 0 0 0 4 4
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 4.0f,
                    dy1 = 4.0f,
                )
                // h 16
                horizontalLineToRelative(dx = 16.0f)
                // a 4 4 0 0 0 4 -4
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 4.0f,
                    dy1 = -4.0f,
                )
                // v -11
                verticalLineToRelative(dy = -11.0f)
                // a 4 4 0 0 0 -4 -4z
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -4.0f,
                    dy1 = -4.0f,
                )
                close()
                // M 5.5 17
                moveTo(x = 5.5f, y = 17.0f)
                // a 1 1 0 0 1 1 -1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.0f,
                    dy1 = -1.0f,
                )
                // h 11
                horizontalLineToRelative(dx = 11.0f)
                // a 1 1 0 1 1 0 2
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 2.0f,
                )
                // h -11
                horizontalLineToRelative(dx = -11.0f)
                // a 1 1 0 0 1 -1 -1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.0f,
                    dy1 = -1.0f,
                )
                // M 3.53 6
                moveTo(x = 3.53f, y = 6.0f)
                // A 0.5 0.5 0 0 0 3 6.5
                arcTo(
                    horizontalEllipseRadius = 0.5f,
                    verticalEllipseRadius = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 3.0f,
                    y1 = 6.5f,
                )
                // V 8
                verticalLineTo(y = 8.0f)
                // c 0 0.28 0.24 0.5 0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = 0.24f,
                    dy2 = 0.5f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // h 1.44
                horizontalLineToRelative(dx = 1.44f)
                // C 5.27 8.5 5.5 8.28 5.5 8
                curveTo(
                    x1 = 5.27f,
                    y1 = 8.5f,
                    x2 = 5.5f,
                    y2 = 8.28f,
                    x3 = 5.5f,
                    y3 = 8.0f,
                )
                // V 6.5
                verticalLineTo(y = 6.5f)
                // C 5.5 6.22 5.26 6 4.97 6z
                curveTo(
                    x1 = 5.5f,
                    y1 = 6.22f,
                    x2 = 5.26f,
                    y2 = 6.0f,
                    x3 = 4.97f,
                    y3 = 6.0f,
                )
                close()
                // m 15.5 0
                moveToRelative(dx = 15.5f, dy = 0.0f)
                // a 0.5 0.5 0 0 0 -0.53 0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.53f,
                    dy1 = 0.5f,
                )
                // V 8
                verticalLineTo(y = 8.0f)
                // c 0 0.28 0.24 0.5 0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = 0.24f,
                    dy2 = 0.5f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // h 1.44
                horizontalLineToRelative(dx = 1.44f)
                // C 20.77 8.5 21 8.28 21 8
                curveTo(
                    x1 = 20.77f,
                    y1 = 8.5f,
                    x2 = 21.0f,
                    y2 = 8.28f,
                    x3 = 21.0f,
                    y3 = 8.0f,
                )
                // V 6.5
                verticalLineTo(y = 6.5f)
                // C 21 6.22 20.76 6 20.47 6z
                curveTo(
                    x1 = 21.0f,
                    y1 = 6.22f,
                    x2 = 20.76f,
                    y2 = 6.0f,
                    x3 = 20.47f,
                    y3 = 6.0f,
                )
                close()
                // M 3 11.5
                moveTo(x = 3.0f, y = 11.5f)
                // C 3 11.22 3.24 11 3.53 11
                curveTo(
                    x1 = 3.0f,
                    y1 = 11.22f,
                    x2 = 3.24f,
                    y2 = 11.0f,
                    x3 = 3.53f,
                    y3 = 11.0f,
                )
                // h 1.44
                horizontalLineToRelative(dx = 1.44f)
                // c 0.3 0 0.53 0.22 0.53 0.5
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = 0.0f,
                    dx2 = 0.53f,
                    dy2 = 0.22f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // V 13
                verticalLineTo(y = 13.0f)
                // c 0 0.28 -0.24 0.5 -0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = -0.24f,
                    dy2 = 0.5f,
                    dx3 = -0.53f,
                    dy3 = 0.5f,
                )
                // H 3.53
                horizontalLineTo(x = 3.53f)
                // A 0.5 0.5 0 0 1 3 13z
                arcTo(
                    horizontalEllipseRadius = 0.5f,
                    verticalEllipseRadius = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 3.0f,
                    y1 = 13.0f,
                )
                close()
                // M 8.62 6
                moveTo(x = 8.62f, y = 6.0f)
                // a 0.5 0.5 0 0 0 -0.53 0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.53f,
                    dy1 = 0.5f,
                )
                // V 8
                verticalLineTo(y = 8.0f)
                // c 0 0.28 0.24 0.5 0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = 0.24f,
                    dy2 = 0.5f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // h 1.35
                horizontalLineToRelative(dx = 1.35f)
                // c 0.3 0 0.53 -0.22 0.53 -0.5
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = 0.0f,
                    dx2 = 0.53f,
                    dy2 = -0.22f,
                    dx3 = 0.53f,
                    dy3 = -0.5f,
                )
                // V 6.5
                verticalLineTo(y = 6.5f)
                // c 0 -0.28 -0.24 -0.5 -0.53 -0.5z
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.28f,
                    dx2 = -0.24f,
                    dy2 = -0.5f,
                    dx3 = -0.53f,
                    dy3 = -0.5f,
                )
                close()
                // m -0.53 5.5
                moveToRelative(dx = -0.53f, dy = 5.5f)
                // c 0 -0.28 0.24 -0.5 0.53 -0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.28f,
                    dx2 = 0.24f,
                    dy2 = -0.5f,
                    dx3 = 0.53f,
                    dy3 = -0.5f,
                )
                // h 1.35
                horizontalLineToRelative(dx = 1.35f)
                // c 0.3 0 0.53 0.22 0.53 0.5
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = 0.0f,
                    dx2 = 0.53f,
                    dy2 = 0.22f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // V 13
                verticalLineTo(y = 13.0f)
                // c 0 0.28 -0.24 0.5 -0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = -0.24f,
                    dy2 = 0.5f,
                    dx3 = -0.53f,
                    dy3 = 0.5f,
                )
                // H 8.62
                horizontalLineTo(x = 8.62f)
                // A 0.5 0.5 0 0 1 8.09 13z
                arcTo(
                    horizontalEllipseRadius = 0.5f,
                    verticalEllipseRadius = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 8.09f,
                    y1 = 13.0f,
                )
                close()
                // M 14 6
                moveTo(x = 14.0f, y = 6.0f)
                // a 0.5 0.5 0 0 0 -0.53 0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.53f,
                    dy1 = 0.5f,
                )
                // V 8
                verticalLineTo(y = 8.0f)
                // c 0 0.28 0.24 0.5 0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = 0.24f,
                    dy2 = 0.5f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // h 1.38
                horizontalLineToRelative(dx = 1.38f)
                // c 0.3 0 0.53 -0.22 0.53 -0.5
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = 0.0f,
                    dx2 = 0.53f,
                    dy2 = -0.22f,
                    dx3 = 0.53f,
                    dy3 = -0.5f,
                )
                // V 6.5
                verticalLineTo(y = 6.5f)
                // c 0 -0.28 -0.24 -0.5 -0.53 -0.5z
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.28f,
                    dx2 = -0.24f,
                    dy2 = -0.5f,
                    dx3 = -0.53f,
                    dy3 = -0.5f,
                )
                close()
                // m -0.53 5.5
                moveToRelative(dx = -0.53f, dy = 5.5f)
                // c 0 -0.28 0.24 -0.5 0.53 -0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.28f,
                    dx2 = 0.24f,
                    dy2 = -0.5f,
                    dx3 = 0.53f,
                    dy3 = -0.5f,
                )
                // h 1.38
                horizontalLineToRelative(dx = 1.38f)
                // c 0.3 0 0.53 0.22 0.53 0.5
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = 0.0f,
                    dx2 = 0.53f,
                    dy2 = 0.22f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // V 13
                verticalLineTo(y = 13.0f)
                // c 0 0.28 -0.24 0.5 -0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = -0.24f,
                    dy2 = 0.5f,
                    dx3 = -0.53f,
                    dy3 = 0.5f,
                )
                // H 14
                horizontalLineTo(x = 14.0f)
                // a 0.5 0.5 0 0 1 -0.53 -0.5z
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.53f,
                    dy1 = -0.5f,
                )
                close()
                // m 5.56 -0.5
                moveToRelative(dx = 5.56f, dy = -0.5f)
                // a 0.5 0.5 0 0 0 -0.53 0.5
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.53f,
                    dy1 = 0.5f,
                )
                // V 13
                verticalLineTo(y = 13.0f)
                // c 0 0.28 0.24 0.5 0.53 0.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.28f,
                    dx2 = 0.24f,
                    dy2 = 0.5f,
                    dx3 = 0.53f,
                    dy3 = 0.5f,
                )
                // h 1.44
                horizontalLineToRelative(dx = 1.44f)
                // c 0.3 0 0.53 -0.22 0.53 -0.5
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = 0.0f,
                    dx2 = 0.53f,
                    dy2 = -0.22f,
                    dx3 = 0.53f,
                    dy3 = -0.5f,
                )
                // v -1.5
                verticalLineToRelative(dy = -1.5f)
                // c 0 -0.28 -0.24 -0.5 -0.53 -0.5z
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.28f,
                    dx2 = -0.24f,
                    dy2 = -0.5f,
                    dx3 = -0.53f,
                    dy3 = -0.5f,
                )
                close()
            }
        }.build().also { _abcKeyboardSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _abcKeyboardSolid: ImageVector? = null
