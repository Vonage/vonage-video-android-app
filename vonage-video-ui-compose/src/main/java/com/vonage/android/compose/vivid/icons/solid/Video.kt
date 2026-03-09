@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Video: ImageVector
    get() {
        val current = _videoSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Video",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M2 3 a2 2 0 0 0 -2 2 v14 a2 2 0 0 0 2 2 h13 a2 2 0 0 0 2 -2 v-3.8 l6.31 2.53 A.5 .5 0 0 0 24 17.26 V6.74 a.5 .5 0 0 0 -.69 -.47 L17 8.8 V5 a2 2 0 0 0 -2 -2z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 2 3
                moveTo(x = 2.0f, y = 3.0f)
                // a 2 2 0 0 0 -2 2
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.0f,
                    dy1 = 2.0f,
                )
                // v 14
                verticalLineToRelative(dy = 14.0f)
                // a 2 2 0 0 0 2 2
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.0f,
                    dy1 = 2.0f,
                )
                // h 13
                horizontalLineToRelative(dx = 13.0f)
                // a 2 2 0 0 0 2 -2
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 2.0f,
                    dy1 = -2.0f,
                )
                // v -3.8
                verticalLineToRelative(dy = -3.8f)
                // l 6.31 2.53
                lineToRelative(dx = 6.31f, dy = 2.53f)
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
                // a 2 2 0 0 0 -2 -2z
                arcToRelative(
                    a = 2.0f,
                    b = 2.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.0f,
                    dy1 = -2.0f,
                )
                close()
            }
        }.build().also { _videoSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _videoSolid: ImageVector? = null
