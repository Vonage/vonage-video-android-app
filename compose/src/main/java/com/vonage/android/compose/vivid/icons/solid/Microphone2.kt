package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Microphone2: ImageVector
    get() {
        val current = _microphone2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Microphone2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // M5.5 9.6 c0 3.53 2.91 6.4 6.5 6.4 s6.5 -2.87 6.5 -6.4 V6.4 C18.5 2.87 15.59 0 12 0 a6.45 6.45 0 0 0 -6.5 6.4z M3 10.33 C3 9.6 2.33 9 1.5 9 S0 9.6 0 10.33 c0 5.44 4.58 9.93 10.5 10.59 V24 h3 v-3.08 C19.42 20.26 24 15.77 24 10.33 24 9.6 23.33 9 22.5 9 S21 9.6 21 10.33 c0 4.42 -4.03 8 -9 8 s-9 -3.58 -9 -8
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 5.5 9.6
                moveTo(x = 5.5f, y = 9.6f)
                // c 0 3.53 2.91 6.4 6.5 6.4
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 3.53f,
                    dx2 = 2.91f,
                    dy2 = 6.4f,
                    dx3 = 6.5f,
                    dy3 = 6.4f,
                )
                // s 6.5 -2.87 6.5 -6.4
                reflectiveCurveToRelative(
                    dx1 = 6.5f,
                    dy1 = -2.87f,
                    dx2 = 6.5f,
                    dy2 = -6.4f,
                )
                // V 6.4
                verticalLineTo(y = 6.4f)
                // C 18.5 2.87 15.59 0 12 0
                curveTo(
                    x1 = 18.5f,
                    y1 = 2.87f,
                    x2 = 15.59f,
                    y2 = 0.0f,
                    x3 = 12.0f,
                    y3 = 0.0f,
                )
                // a 6.45 6.45 0 0 0 -6.5 6.4z
                arcToRelative(
                    a = 6.45f,
                    b = 6.45f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -6.5f,
                    dy1 = 6.4f,
                )
                close()
                // M 3 10.33
                moveTo(x = 3.0f, y = 10.33f)
                // C 3 9.6 2.33 9 1.5 9
                curveTo(
                    x1 = 3.0f,
                    y1 = 9.6f,
                    x2 = 2.33f,
                    y2 = 9.0f,
                    x3 = 1.5f,
                    y3 = 9.0f,
                )
                // S 0 9.6 0 10.33
                reflectiveCurveTo(
                    x1 = 0.0f,
                    y1 = 9.6f,
                    x2 = 0.0f,
                    y2 = 10.33f,
                )
                // c 0 5.44 4.58 9.93 10.5 10.59
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 5.44f,
                    dx2 = 4.58f,
                    dy2 = 9.93f,
                    dx3 = 10.5f,
                    dy3 = 10.59f,
                )
                // V 24
                verticalLineTo(y = 24.0f)
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // v -3.08
                verticalLineToRelative(dy = -3.08f)
                // C 19.42 20.26 24 15.77 24 10.33
                curveTo(
                    x1 = 19.42f,
                    y1 = 20.26f,
                    x2 = 24.0f,
                    y2 = 15.77f,
                    x3 = 24.0f,
                    y3 = 10.33f,
                )
                // C 24 9.6 23.33 9 22.5 9
                curveTo(
                    x1 = 24.0f,
                    y1 = 9.6f,
                    x2 = 23.33f,
                    y2 = 9.0f,
                    x3 = 22.5f,
                    y3 = 9.0f,
                )
                // S 21 9.6 21 10.33
                reflectiveCurveTo(
                    x1 = 21.0f,
                    y1 = 9.6f,
                    x2 = 21.0f,
                    y2 = 10.33f,
                )
                // c 0 4.42 -4.03 8 -9 8
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 4.42f,
                    dx2 = -4.03f,
                    dy2 = 8.0f,
                    dx3 = -9.0f,
                    dy3 = 8.0f,
                )
                // s -9 -3.58 -9 -8
                reflectiveCurveToRelative(
                    dx1 = -9.0f,
                    dy1 = -3.58f,
                    dx2 = -9.0f,
                    dy2 = -8.0f,
                )
            }
        }.build().also { _microphone2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _microphone2Solid: ImageVector? = null
