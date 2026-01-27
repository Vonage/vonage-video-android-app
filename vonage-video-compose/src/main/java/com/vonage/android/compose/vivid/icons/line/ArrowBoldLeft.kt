@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.ArrowBoldLeft: ImageVector
    get() {
        val current = _arrowBoldLeftLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.ArrowBoldLeft",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
            autoMirror = true,
        ).apply {
            // M8.33 19.68 a1.14 1.14 0 0 0 1.6 0 1.1 1.1 0 0 0 0 -1.57 l-5.09 -5 H22 c.62 0 1 -.5 1 -1.11 s-.38 -1.1 -1 -1.1 H4.84 l5.08 -5 a1.1 1.1 0 0 0 0 -1.58 1.14 1.14 0 0 0 -1.59 0 L1.35 11.2 a1.1 1.1 0 0 0 -.02 1.6z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 8.33 19.68
                moveTo(x = 8.33f, y = 19.68f)
                // a 1.14 1.14 0 0 0 1.6 0
                arcToRelative(
                    a = 1.14f,
                    b = 1.14f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.6f,
                    dy1 = 0.0f,
                )
                // a 1.1 1.1 0 0 0 0 -1.57
                arcToRelative(
                    a = 1.1f,
                    b = 1.1f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -1.57f,
                )
                // l -5.09 -5
                lineToRelative(dx = -5.09f, dy = -5.0f)
                // H 22
                horizontalLineTo(x = 22.0f)
                // c 0.62 0 1 -0.5 1 -1.11
                curveToRelative(
                    dx1 = 0.62f,
                    dy1 = 0.0f,
                    dx2 = 1.0f,
                    dy2 = -0.5f,
                    dx3 = 1.0f,
                    dy3 = -1.11f,
                )
                // s -0.38 -1.1 -1 -1.1
                reflectiveCurveToRelative(
                    dx1 = -0.38f,
                    dy1 = -1.1f,
                    dx2 = -1.0f,
                    dy2 = -1.1f,
                )
                // H 4.84
                horizontalLineTo(x = 4.84f)
                // l 5.08 -5
                lineToRelative(dx = 5.08f, dy = -5.0f)
                // a 1.1 1.1 0 0 0 0 -1.58
                arcToRelative(
                    a = 1.1f,
                    b = 1.1f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -1.58f,
                )
                // a 1.14 1.14 0 0 0 -1.59 0
                arcToRelative(
                    a = 1.14f,
                    b = 1.14f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.59f,
                    dy1 = 0.0f,
                )
                // L 1.35 11.2
                lineTo(x = 1.35f, y = 11.2f)
                // a 1.1 1.1 0 0 0 -0.02 1.6z
                arcToRelative(
                    a = 1.1f,
                    b = 1.1f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.02f,
                    dy1 = 1.6f,
                )
                close()
            }
        }.build().also { _arrowBoldLeftLine = it }
    }

@Suppress("ObjectPropertyName")
private var _arrowBoldLeftLine: ImageVector? = null
