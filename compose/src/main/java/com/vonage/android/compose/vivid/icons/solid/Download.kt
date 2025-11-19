@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Download: ImageVector
    get() {
        val current = _downloadSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Download",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M13.5 .5 V11 h3.6 L12 17 l-5.1 -6 h3.6 V.5z M1.5 24 h21 c.9 0 1.5 -.6 1.5 -1.5 V9 c0 -.9 -.6 -1.5 -1.5 -1.5 H18 v3 h3 V21 H3 V10.5 h3 v-3 H1.5 C.6 7.5 0 8.1 0 9 v13.5 C0 23.4 .6 24 1.5 24
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 13.5 0.5
                moveTo(x = 13.5f, y = 0.5f)
                // V 11
                verticalLineTo(y = 11.0f)
                // h 3.6
                horizontalLineToRelative(dx = 3.6f)
                // L 12 17
                lineTo(x = 12.0f, y = 17.0f)
                // l -5.1 -6
                lineToRelative(dx = -5.1f, dy = -6.0f)
                // h 3.6
                horizontalLineToRelative(dx = 3.6f)
                // V 0.5z
                verticalLineTo(y = 0.5f)
                close()
                // M 1.5 24
                moveTo(x = 1.5f, y = 24.0f)
                // h 21
                horizontalLineToRelative(dx = 21.0f)
                // c 0.9 0 1.5 -0.6 1.5 -1.5
                curveToRelative(
                    dx1 = 0.9f,
                    dy1 = 0.0f,
                    dx2 = 1.5f,
                    dy2 = -0.6f,
                    dx3 = 1.5f,
                    dy3 = -1.5f,
                )
                // V 9
                verticalLineTo(y = 9.0f)
                // c 0 -0.9 -0.6 -1.5 -1.5 -1.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.9f,
                    dx2 = -0.6f,
                    dy2 = -1.5f,
                    dx3 = -1.5f,
                    dy3 = -1.5f,
                )
                // H 18
                horizontalLineTo(x = 18.0f)
                // v 3
                verticalLineToRelative(dy = 3.0f)
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // V 21
                verticalLineTo(y = 21.0f)
                // H 3
                horizontalLineTo(x = 3.0f)
                // V 10.5
                verticalLineTo(y = 10.5f)
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // v -3
                verticalLineToRelative(dy = -3.0f)
                // H 1.5
                horizontalLineTo(x = 1.5f)
                // C 0.6 7.5 0 8.1 0 9
                curveTo(
                    x1 = 0.6f,
                    y1 = 7.5f,
                    x2 = 0.0f,
                    y2 = 8.1f,
                    x3 = 0.0f,
                    y3 = 9.0f,
                )
                // v 13.5
                verticalLineToRelative(dy = 13.5f)
                // C 0 23.4 0.6 24 1.5 24
                curveTo(
                    x1 = 0.0f,
                    y1 = 23.4f,
                    x2 = 0.6f,
                    y2 = 24.0f,
                    x3 = 1.5f,
                    y3 = 24.0f,
                )
            }
        }.build().also { _downloadSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _downloadSolid: ImageVector? = null
