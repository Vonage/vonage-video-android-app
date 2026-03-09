@file:Suppress("MaxLineLength")

package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.VideoActive: ImageVector
    get() {
        val current = _videoActiveLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.VideoActive",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M14.25 6.75 a3 3 0 1 0 0 -6 3 3 0 0 0 0 6
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 14.25 6.75
                moveTo(x = 14.25f, y = 6.75f)
                // a 3 3 0 1 0 0 -6
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -6.0f,
                )
                // a 3 3 0 0 0 0 6
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 6.0f,
                )
            }
            // M2.25 3 H9 v1.5 H2.25 A.75 .75 0 0 0 1.5 5.25 v13.5 a.75 .75 0 0 0 .75 .75 h12 A.75 .75 0 0 0 15 18.75 V9.23 l1.5 -.6 v.01 l6.47 -2.59 A.75 .75 0 0 1 24 6.75 v10.5 a.75 .75 0 0 1 -1.03 .7 l-6.47 -2.6 v3.4 c0 1.24 -1 2.25 -2.25 2.25 h-12 C1.01 21 0 20 0 18.75 V5.25 C0 4.01 1 3 2.25 3 M16.5 13.74 l6 2.4 V7.86 l-6 2.4z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 2.25 3
                moveTo(x = 2.25f, y = 3.0f)
                // H 9
                horizontalLineTo(x = 9.0f)
                // v 1.5
                verticalLineToRelative(dy = 1.5f)
                // H 2.25
                horizontalLineTo(x = 2.25f)
                // A 0.75 0.75 0 0 0 1.5 5.25
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 1.5f,
                    y1 = 5.25f,
                )
                // v 13.5
                verticalLineToRelative(dy = 13.5f)
                // a 0.75 0.75 0 0 0 0.75 0.75
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.75f,
                    dy1 = 0.75f,
                )
                // h 12
                horizontalLineToRelative(dx = 12.0f)
                // A 0.75 0.75 0 0 0 15 18.75
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 15.0f,
                    y1 = 18.75f,
                )
                // V 9.23
                verticalLineTo(y = 9.23f)
                // l 1.5 -0.6
                lineToRelative(dx = 1.5f, dy = -0.6f)
                // v 0.01
                verticalLineToRelative(dy = 0.01f)
                // l 6.47 -2.59
                lineToRelative(dx = 6.47f, dy = -2.59f)
                // A 0.75 0.75 0 0 1 24 6.75
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 24.0f,
                    y1 = 6.75f,
                )
                // v 10.5
                verticalLineToRelative(dy = 10.5f)
                // a 0.75 0.75 0 0 1 -1.03 0.7
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.03f,
                    dy1 = 0.7f,
                )
                // l -6.47 -2.6
                lineToRelative(dx = -6.47f, dy = -2.6f)
                // v 3.4
                verticalLineToRelative(dy = 3.4f)
                // c 0 1.24 -1 2.25 -2.25 2.25
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 1.24f,
                    dx2 = -1.0f,
                    dy2 = 2.25f,
                    dx3 = -2.25f,
                    dy3 = 2.25f,
                )
                // h -12
                horizontalLineToRelative(dx = -12.0f)
                // C 1.01 21 0 20 0 18.75
                curveTo(
                    x1 = 1.01f,
                    y1 = 21.0f,
                    x2 = 0.0f,
                    y2 = 20.0f,
                    x3 = 0.0f,
                    y3 = 18.75f,
                )
                // V 5.25
                verticalLineTo(y = 5.25f)
                // C 0 4.01 1 3 2.25 3
                curveTo(
                    x1 = 0.0f,
                    y1 = 4.01f,
                    x2 = 1.0f,
                    y2 = 3.0f,
                    x3 = 2.25f,
                    y3 = 3.0f,
                )
                // M 16.5 13.74
                moveTo(x = 16.5f, y = 13.74f)
                // l 6 2.4
                lineToRelative(dx = 6.0f, dy = 2.4f)
                // V 7.86
                verticalLineTo(y = 7.86f)
                // l -6 2.4z
                lineToRelative(dx = -6.0f, dy = 2.4f)
                close()
            }
        }.build().also { _videoActiveLine = it }
    }

@Suppress("ObjectPropertyName")
private var _videoActiveLine: ImageVector? = null
