package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.AudioMid: ImageVector
    get() {
        val current = _audioMidSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.AudioMid",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
            autoMirror = true,
        ).apply {
            // M10.91 3.1 a.5 .5 0 0 0 -.82 -.38 L4.37 7.67 H2.18 c-1.2 0 -2.18 1 -2.18 2.22 v4.22 a2.2 2.2 0 0 0 2.18 2.22 h2.19 l5.72 4.95 a.5 .5 0 0 0 .82 -.37z m2.39 5.04 a1 1 0 0 1 1.41 .02 5.5 5.5 0 0 1 0 7.69 1 1 0 1 1 -1.42 -1.4 3.5 3.5 0 0 0 0 -4.9 1 1 0 0 1 0 -1.4 m4.03 -3.13 a1 1 0 0 0 -1.42 1.4 8 8 0 0 1 0 11.17 1 1 0 1 0 1.42 1.4 10 10 0 0 0 0 -13.97
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 10.91 3.1
                moveTo(x = 10.91f, y = 3.1f)
                // a 0.5 0.5 0 0 0 -0.82 -0.38
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.82f,
                    dy1 = -0.38f,
                )
                // L 4.37 7.67
                lineTo(x = 4.37f, y = 7.67f)
                // H 2.18
                horizontalLineTo(x = 2.18f)
                // c -1.2 0 -2.18 1 -2.18 2.22
                curveToRelative(
                    dx1 = -1.2f,
                    dy1 = 0.0f,
                    dx2 = -2.18f,
                    dy2 = 1.0f,
                    dx3 = -2.18f,
                    dy3 = 2.22f,
                )
                // v 4.22
                verticalLineToRelative(dy = 4.22f)
                // a 2.2 2.2 0 0 0 2.18 2.22
                arcToRelative(
                    a = 2.2f,
                    b = 2.2f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.18f,
                    dy1 = 2.22f,
                )
                // h 2.19
                horizontalLineToRelative(dx = 2.19f)
                // l 5.72 4.95
                lineToRelative(dx = 5.72f, dy = 4.95f)
                // a 0.5 0.5 0 0 0 0.82 -0.37z
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.82f,
                    dy1 = -0.37f,
                )
                close()
                // m 2.39 5.04
                moveToRelative(dx = 2.39f, dy = 5.04f)
                // a 1 1 0 0 1 1.41 0.02
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.41f,
                    dy1 = 0.02f,
                )
                // a 5.5 5.5 0 0 1 0 7.69
                arcToRelative(
                    a = 5.5f,
                    b = 5.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 7.69f,
                )
                // a 1 1 0 1 1 -1.42 -1.4
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -1.42f,
                    dy1 = -1.4f,
                )
                // a 3.5 3.5 0 0 0 0 -4.9
                arcToRelative(
                    a = 3.5f,
                    b = 3.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -4.9f,
                )
                // a 1 1 0 0 1 0 -1.4
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.4f,
                )
                // m 4.03 -3.13
                moveToRelative(dx = 4.03f, dy = -3.13f)
                // a 1 1 0 0 0 -1.42 1.4
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.42f,
                    dy1 = 1.4f,
                )
                // a 8 8 0 0 1 0 11.17
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 11.17f,
                )
                // a 1 1 0 1 0 1.42 1.4
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 1.42f,
                    dy1 = 1.4f,
                )
                // a 10 10 0 0 0 0 -13.97
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -13.97f,
                )
            }
        }.build().also { _audioMidSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _audioMidSolid: ImageVector? = null
