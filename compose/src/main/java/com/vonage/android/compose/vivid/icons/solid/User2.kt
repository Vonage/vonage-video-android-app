package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.User2: ImageVector
    get() {
        val current = _user2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.User2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M6.29 12.6 C3.8 13.47 2 15.34 2 17.89 v3.61 a1 1 0 0 0 1 1 h18 a1 1 0 0 0 1 -1 v-3.61 c0 -2.55 -1.8 -4.42 -4.29 -5.3 a8 8 0 0 1 -11.42 0 M17.5 7 a5.5 5.5 0 1 1 -11 0 5.5 5.5 0 0 1 11 0
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 6.29 12.6
                moveTo(x = 6.29f, y = 12.6f)
                // C 3.8 13.47 2 15.34 2 17.89
                curveTo(
                    x1 = 3.8f,
                    y1 = 13.47f,
                    x2 = 2.0f,
                    y2 = 15.34f,
                    x3 = 2.0f,
                    y3 = 17.89f,
                )
                // v 3.61
                verticalLineToRelative(dy = 3.61f)
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
                // h 18
                horizontalLineToRelative(dx = 18.0f)
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
                // v -3.61
                verticalLineToRelative(dy = -3.61f)
                // c 0 -2.55 -1.8 -4.42 -4.29 -5.3
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -2.55f,
                    dx2 = -1.8f,
                    dy2 = -4.42f,
                    dx3 = -4.29f,
                    dy3 = -5.3f,
                )
                // a 8 8 0 0 1 -11.42 0
                arcToRelative(
                    a = 8.0f,
                    b = 8.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -11.42f,
                    dy1 = 0.0f,
                )
                // M 17.5 7
                moveTo(x = 17.5f, y = 7.0f)
                // a 5.5 5.5 0 1 1 -11 0
                arcToRelative(
                    a = 5.5f,
                    b = 5.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -11.0f,
                    dy1 = 0.0f,
                )
                // a 5.5 5.5 0 0 1 11 0
                arcToRelative(
                    a = 5.5f,
                    b = 5.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 11.0f,
                    dy1 = 0.0f,
                )
            }
        }.build().also { _user2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _user2Solid: ImageVector? = null
