@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Warning: ImageVector
    get() {
        val current = _warningSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Warning",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M23.8 20.8 13.45 1.36 a1.64 1.64 0 0 0 -2.88 0 L.19 20.79 a1.64 1.64 0 0 0 1.45 2.41 h20.72 a1.64 1.64 0 0 0 1.45 -2.4 M12 19.92 a1.64 1.64 0 1 1 0 -3.27 1.64 1.64 0 0 1 0 3.27 m1.64 -5.4 a.5 .5 0 0 1 -.5 .5 h-2.28 a.5 .5 0 0 1 -.5 -.5 V8.97 a.5 .5 0 0 1 .5 -.5 h2.28 a.5 .5 0 0 1 .5 .5z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 23.8 20.8
                moveTo(x = 23.8f, y = 20.8f)
                // L 13.45 1.36
                lineTo(x = 13.45f, y = 1.36f)
                // a 1.64 1.64 0 0 0 -2.88 0
                arcToRelative(
                    a = 1.64f,
                    b = 1.64f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.88f,
                    dy1 = 0.0f,
                )
                // L 0.19 20.79
                lineTo(x = 0.19f, y = 20.79f)
                // a 1.64 1.64 0 0 0 1.45 2.41
                arcToRelative(
                    a = 1.64f,
                    b = 1.64f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.45f,
                    dy1 = 2.41f,
                )
                // h 20.72
                horizontalLineToRelative(dx = 20.72f)
                // a 1.64 1.64 0 0 0 1.45 -2.4
                arcToRelative(
                    a = 1.64f,
                    b = 1.64f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.45f,
                    dy1 = -2.4f,
                )
                // M 12 19.92
                moveTo(x = 12.0f, y = 19.92f)
                // a 1.64 1.64 0 1 1 0 -3.27
                arcToRelative(
                    a = 1.64f,
                    b = 1.64f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -3.27f,
                )
                // a 1.64 1.64 0 0 1 0 3.27
                arcToRelative(
                    a = 1.64f,
                    b = 1.64f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 3.27f,
                )
                // m 1.64 -5.4
                moveToRelative(dx = 1.64f, dy = -5.4f)
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
                // h -2.28
                horizontalLineToRelative(dx = -2.28f)
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
                // V 8.97
                verticalLineTo(y = 8.97f)
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
                // h 2.28
                horizontalLineToRelative(dx = 2.28f)
                // a 0.5 0.5 0 0 1 0.5 0.5z
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.5f,
                    dy1 = 0.5f,
                )
                close()
            }
        }.build().also { _warningSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _warningSolid: ImageVector? = null
