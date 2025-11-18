@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.EndCall: ImageVector
    get() {
        val current = _endCallSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.EndCall",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M12 7 h.02 q1.5 0 3.3 .28 a23 23 0 0 1 3.5 .83 13 13 0 0 1 3.04 1.39 4.4 4.4 0 0 1 1.9 2.17 3.7 3.7 0 0 1 -.04 2.72 l-.44 1 -.02 .03 a1.58 1.58 0 0 1 -1.41 .83 2 2 0 0 1 -.57 -.07 l-.37 -.09 H20.9 L19 15.66 c-1.26 -.28 -2.36 -.77 -2.83 -1.68 -.33 -.6 -.55 -1.27 -.4 -1.9 a1 1 0 0 0 -.2 -.19 h-.02 a3.5 3.5 0 0 0 -1.63 -.46 22 22 0 0 0 -1.89 -.12 h-.01 q-.6 0 -1.92 .14 a4 4 0 0 0 -1.66 .45 1 1 0 0 0 -.21 .19 c.15 .63 -.06 1.3 -.38 1.91 l-.01 .02 C7.36 14.9 6.27 15.39 5 15.69 a63 63 0 0 1 -2.3 .5 2 2 0 0 1 -.55 .08 1.6 1.6 0 0 1 -1.42 -.83 L.71 15.4 l-.43 -.98 a3.6 3.6 0 0 1 -.03 -2.72 4.4 4.4 0 0 1 1.9 -2.18 13 13 0 0 1 3.03 -1.4 A23 23 0 0 1 12 7
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 12 7
                moveTo(x = 12.0f, y = 7.0f)
                // h 0.02
                horizontalLineToRelative(dx = 0.02f)
                // q 1.5 0 3.3 0.28
                quadToRelative(
                    dx1 = 1.5f,
                    dy1 = 0.0f,
                    dx2 = 3.3f,
                    dy2 = 0.28f,
                )
                // a 23 23 0 0 1 3.5 0.83
                arcToRelative(
                    a = 23.0f,
                    b = 23.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.5f,
                    dy1 = 0.83f,
                )
                // a 13 13 0 0 1 3.04 1.39
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.04f,
                    dy1 = 1.39f,
                )
                // a 4.4 4.4 0 0 1 1.9 2.17
                arcToRelative(
                    a = 4.4f,
                    b = 4.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.9f,
                    dy1 = 2.17f,
                )
                // a 3.7 3.7 0 0 1 -0.04 2.72
                arcToRelative(
                    a = 3.7f,
                    b = 3.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.04f,
                    dy1 = 2.72f,
                )
                // l -0.44 1
                lineToRelative(dx = -0.44f, dy = 1.0f)
                // l -0.02 0.03
                lineToRelative(dx = -0.02f, dy = 0.03f)
                // a 1.58 1.58 0 0 1 -1.41 0.83
                arcToRelative(
                    a = 1.58f,
                    b = 1.58f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.41f,
                    dy1 = 0.83f,
                )
                // a 2 2 0 0 1 -0.57 -0.07
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.57f,
                    dy1 = -0.07f,
                )
                // l -0.37 -0.09
                lineToRelative(dx = -0.37f, dy = -0.09f)
                // H 20.9
                horizontalLineTo(x = 20.9f)
                // L 19 15.66
                lineTo(x = 19.0f, y = 15.66f)
                // c -1.26 -0.28 -2.36 -0.77 -2.83 -1.68
                curveToRelative(
                    dx1 = -1.26f,
                    dy1 = -0.28f,
                    dx2 = -2.36f,
                    dy2 = -0.77f,
                    dx3 = -2.83f,
                    dy3 = -1.68f,
                )
                // c -0.33 -0.6 -0.55 -1.27 -0.4 -1.9
                curveToRelative(
                    dx1 = -0.33f,
                    dy1 = -0.6f,
                    dx2 = -0.55f,
                    dy2 = -1.27f,
                    dx3 = -0.4f,
                    dy3 = -1.9f,
                )
                // a 1 1 0 0 0 -0.2 -0.19
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.2f,
                    dy1 = -0.19f,
                )
                // h -0.02
                horizontalLineToRelative(dx = -0.02f)
                // a 3.5 3.5 0 0 0 -1.63 -0.46
                arcToRelative(
                    a = 3.5f,
                    b = 3.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.63f,
                    dy1 = -0.46f,
                )
                // a 22 22 0 0 0 -1.89 -0.12
                arcToRelative(
                    a = 22.0f,
                    b = 22.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.89f,
                    dy1 = -0.12f,
                )
                // h -0.01
                horizontalLineToRelative(dx = -0.01f)
                // q -0.6 0 -1.92 0.14
                quadToRelative(
                    dx1 = -0.6f,
                    dy1 = 0.0f,
                    dx2 = -1.92f,
                    dy2 = 0.14f,
                )
                // a 4 4 0 0 0 -1.66 0.45
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.66f,
                    dy1 = 0.45f,
                )
                // a 1 1 0 0 0 -0.21 0.19
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.21f,
                    dy1 = 0.19f,
                )
                // c 0.15 0.63 -0.06 1.3 -0.38 1.91
                curveToRelative(
                    dx1 = 0.15f,
                    dy1 = 0.63f,
                    dx2 = -0.06f,
                    dy2 = 1.3f,
                    dx3 = -0.38f,
                    dy3 = 1.91f,
                )
                // l -0.01 0.02
                lineToRelative(dx = -0.01f, dy = 0.02f)
                // C 7.36 14.9 6.27 15.39 5 15.69
                curveTo(
                    x1 = 7.36f,
                    y1 = 14.9f,
                    x2 = 6.27f,
                    y2 = 15.39f,
                    x3 = 5.0f,
                    y3 = 15.69f,
                )
                // a 63 63 0 0 1 -2.3 0.5
                arcToRelative(
                    a = 63.0f,
                    b = 63.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.3f,
                    dy1 = 0.5f,
                )
                // a 2 2 0 0 1 -0.55 0.08
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.55f,
                    dy1 = 0.08f,
                )
                // a 1.6 1.6 0 0 1 -1.42 -0.83
                arcToRelative(
                    a = 1.6f,
                    b = 1.6f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.42f,
                    dy1 = -0.83f,
                )
                // L 0.71 15.4
                lineTo(x = 0.71f, y = 15.4f)
                // l -0.43 -0.98
                lineToRelative(dx = -0.43f, dy = -0.98f)
                // a 3.6 3.6 0 0 1 -0.03 -2.72
                arcToRelative(
                    a = 3.6f,
                    b = 3.6f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.03f,
                    dy1 = -2.72f,
                )
                // a 4.4 4.4 0 0 1 1.9 -2.18
                arcToRelative(
                    a = 4.4f,
                    b = 4.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.9f,
                    dy1 = -2.18f,
                )
                // a 13 13 0 0 1 3.03 -1.4
                arcToRelative(
                    a = 13.0f,
                    b = 13.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.03f,
                    dy1 = -1.4f,
                )
                // A 23 23 0 0 1 12 7
                arcTo(
                    horizontalEllipseRadius = 23.0f,
                    verticalEllipseRadius = 23.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 12.0f,
                    y1 = 7.0f,
                )
            }
        }.build().also { _endCallSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _endCallSolid: ImageVector? = null
