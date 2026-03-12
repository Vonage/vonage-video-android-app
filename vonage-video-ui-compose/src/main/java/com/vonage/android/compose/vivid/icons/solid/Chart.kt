@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Chart: ImageVector
    get() {
        val current = _chartSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Chart",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M13.5 0 h-3 C9.6 0 9 .6 9 1.5 v21 c0 .9 .6 1.5 1.5 1.5 h3 c.9 0 1.5 -.6 1.5 -1.5 v-21 C15 .6 14.4 0 13.5 0 m-9 15 h-3 C.6 15 0 15.6 0 16.5 v6 C0 23.4 .6 24 1.5 24 h3 C5.4 24 6 23.4 6 22.5 v-6 C6 15.6 5.4 15 4.5 15 m18 -7.5 h-3 C18.6 7.5 18 8.1 18 9 v13.5 c0 .9 .6 1.5 1.5 1.5 h3 c.9 0 1.5 -.6 1.5 -1.5 V9 c0 -.9 -.6 -1.5 -1.5 -1.5
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 13.5 0
                moveTo(x = 13.5f, y = 0.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // C 9.6 0 9 0.6 9 1.5
                curveTo(
                    x1 = 9.6f,
                    y1 = 0.0f,
                    x2 = 9.0f,
                    y2 = 0.6f,
                    x3 = 9.0f,
                    y3 = 1.5f,
                )
                // v 21
                verticalLineToRelative(dy = 21.0f)
                // c 0 0.9 0.6 1.5 1.5 1.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.9f,
                    dx2 = 0.6f,
                    dy2 = 1.5f,
                    dx3 = 1.5f,
                    dy3 = 1.5f,
                )
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // c 0.9 0 1.5 -0.6 1.5 -1.5
                curveToRelative(
                    dx1 = 0.9f,
                    dy1 = 0.0f,
                    dx2 = 1.5f,
                    dy2 = -0.6f,
                    dx3 = 1.5f,
                    dy3 = -1.5f,
                )
                // v -21
                verticalLineToRelative(dy = -21.0f)
                // C 15 0.6 14.4 0 13.5 0
                curveTo(
                    x1 = 15.0f,
                    y1 = 0.6f,
                    x2 = 14.4f,
                    y2 = 0.0f,
                    x3 = 13.5f,
                    y3 = 0.0f,
                )
                // m -9 15
                moveToRelative(dx = -9.0f, dy = 15.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // C 0.6 15 0 15.6 0 16.5
                curveTo(
                    x1 = 0.6f,
                    y1 = 15.0f,
                    x2 = 0.0f,
                    y2 = 15.6f,
                    x3 = 0.0f,
                    y3 = 16.5f,
                )
                // v 6
                verticalLineToRelative(dy = 6.0f)
                // C 0 23.4 0.6 24 1.5 24
                curveTo(
                    x1 = 0.0f,
                    y1 = 23.4f,
                    x2 = 0.6f,
                    y2 = 24.0f,
                    x3 = 1.5f,
                    y3 = 24.0f,
                )
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // C 5.4 24 6 23.4 6 22.5
                curveTo(
                    x1 = 5.4f,
                    y1 = 24.0f,
                    x2 = 6.0f,
                    y2 = 23.4f,
                    x3 = 6.0f,
                    y3 = 22.5f,
                )
                // v -6
                verticalLineToRelative(dy = -6.0f)
                // C 6 15.6 5.4 15 4.5 15
                curveTo(
                    x1 = 6.0f,
                    y1 = 15.6f,
                    x2 = 5.4f,
                    y2 = 15.0f,
                    x3 = 4.5f,
                    y3 = 15.0f,
                )
                // m 18 -7.5
                moveToRelative(dx = 18.0f, dy = -7.5f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // C 18.6 7.5 18 8.1 18 9
                curveTo(
                    x1 = 18.6f,
                    y1 = 7.5f,
                    x2 = 18.0f,
                    y2 = 8.1f,
                    x3 = 18.0f,
                    y3 = 9.0f,
                )
                // v 13.5
                verticalLineToRelative(dy = 13.5f)
                // c 0 0.9 0.6 1.5 1.5 1.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.9f,
                    dx2 = 0.6f,
                    dy2 = 1.5f,
                    dx3 = 1.5f,
                    dy3 = 1.5f,
                )
                // h 3
                horizontalLineToRelative(dx = 3.0f)
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
            }
        }.build().also { _chartSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _chartSolid: ImageVector? = null
