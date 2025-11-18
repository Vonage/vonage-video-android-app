@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.VideoOff: ImageVector
    get() {
        val current = _videoOffSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.VideoOff",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M21.39 23.55 a1.53 1.53 0 1 0 2.16 -2.16 l-5.94 -5.95 5.7 2.29 A.5 .5 0 0 0 24 17.26 V6.74 a.5 .5 0 0 0 -.69 -.47 L17 8.8 V5 a2 2 0 0 0 -2 -2 H5.17 L2.6 .45 A1.53 1.53 0 1 0 .45 2.6z M0 6.17 14.83 21 H2 a2 2 0 0 1 -2 -2z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 21.39 23.55
                moveTo(x = 21.39f, y = 23.55f)
                // a 1.53 1.53 0 1 0 2.16 -2.16
                arcToRelative(
                    a = 1.53f,
                    b = 1.53f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 2.16f,
                    dy1 = -2.16f,
                )
                // l -5.94 -5.95
                lineToRelative(dx = -5.94f, dy = -5.95f)
                // l 5.7 2.29
                lineToRelative(dx = 5.7f, dy = 2.29f)
                // A 0.5 0.5 0 0 0 24 17.26
                arcTo(
                    horizontalEllipseRadius = 0.5f,
                    verticalEllipseRadius = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 24.0f,
                    y1 = 17.26f,
                )
                // V 6.74
                verticalLineTo(y = 6.74f)
                // a 0.5 0.5 0 0 0 -0.69 -0.47
                arcToRelative(
                    a = 0.5f,
                    b = 0.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.69f,
                    dy1 = -0.47f,
                )
                // L 17 8.8
                lineTo(x = 17.0f, y = 8.8f)
                // V 5
                verticalLineTo(y = 5.0f)
                // a 2 2 0 0 0 -2 -2
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.0f,
                    dy1 = -2.0f,
                )
                // H 5.17
                horizontalLineTo(x = 5.17f)
                // L 2.6 0.45
                lineTo(x = 2.6f, y = 0.45f)
                // A 1.53 1.53 0 1 0 0.45 2.6z
                arcTo(
                    horizontalEllipseRadius = 1.53f,
                    verticalEllipseRadius = 1.53f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    x1 = 0.45f,
                    y1 = 2.6f,
                )
                close()
                // M 0 6.17
                moveTo(x = 0.0f, y = 6.17f)
                // L 14.83 21
                lineTo(x = 14.83f, y = 21.0f)
                // H 2
                horizontalLineTo(x = 2.0f)
                // a 2 2 0 0 1 -2 -2z
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.0f,
                    dy1 = -2.0f,
                )
                close()
            }
        }.build().also { _videoOffSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _videoOffSolid: ImageVector? = null
