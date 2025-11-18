package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.MessageSent: ImageVector
    get() {
        val current = _messageSentSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.MessageSent",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M24 12.08 q0 -.9 -.93 -1.36 L2.24 1.1 c-.62 -.15 -1.25 -.15 -1.71 .3 a1.7 1.7 0 0 0 -.47 1.66 l2.49 7.37 11.19 1.66 -11.2 1.8 -2.48 7.22 c-.15 .6 0 1.2 .31 1.5 .47 .46 1.09 .46 1.71 .31 l20.83 -9.63 c.78 -.15 1.09 -.6 1.09 -1.2
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 24 12.08
                moveTo(x = 24.0f, y = 12.08f)
                // q 0 -0.9 -0.93 -1.36
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.9f,
                    dx2 = -0.93f,
                    dy2 = -1.36f,
                )
                // L 2.24 1.1
                lineTo(x = 2.24f, y = 1.1f)
                // c -0.62 -0.15 -1.25 -0.15 -1.71 0.3
                curveToRelative(
                    dx1 = -0.62f,
                    dy1 = -0.15f,
                    dx2 = -1.25f,
                    dy2 = -0.15f,
                    dx3 = -1.71f,
                    dy3 = 0.3f,
                )
                // a 1.7 1.7 0 0 0 -0.47 1.66
                arcToRelative(
                    a = 1.7f,
                    b = 1.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.47f,
                    dy1 = 1.66f,
                )
                // l 2.49 7.37
                lineToRelative(dx = 2.49f, dy = 7.37f)
                // l 11.19 1.66
                lineToRelative(dx = 11.19f, dy = 1.66f)
                // l -11.2 1.8
                lineToRelative(dx = -11.2f, dy = 1.8f)
                // l -2.48 7.22
                lineToRelative(dx = -2.48f, dy = 7.22f)
                // c -0.15 0.6 0 1.2 0.31 1.5
                curveToRelative(
                    dx1 = -0.15f,
                    dy1 = 0.6f,
                    dx2 = 0.0f,
                    dy2 = 1.2f,
                    dx3 = 0.31f,
                    dy3 = 1.5f,
                )
                // c 0.47 0.46 1.09 0.46 1.71 0.31
                curveToRelative(
                    dx1 = 0.47f,
                    dy1 = 0.46f,
                    dx2 = 1.09f,
                    dy2 = 0.46f,
                    dx3 = 1.71f,
                    dy3 = 0.31f,
                )
                // l 20.83 -9.63
                lineToRelative(dx = 20.83f, dy = -9.63f)
                // c 0.78 -0.15 1.09 -0.6 1.09 -1.2
                curveToRelative(
                    dx1 = 0.78f,
                    dy1 = -0.15f,
                    dx2 = 1.09f,
                    dy2 = -0.6f,
                    dx3 = 1.09f,
                    dy3 = -1.2f,
                )
            }
        }.build().also { _messageSentSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _messageSentSolid: ImageVector? = null
