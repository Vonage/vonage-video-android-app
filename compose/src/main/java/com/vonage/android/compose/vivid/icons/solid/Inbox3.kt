@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Inbox3: ImageVector
    get() {
        val current = _inbox3Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Inbox3",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M19.5 3 h-15 C3.6 3 3 2.4 3 1.5 S3.6 0 4.5 0 h15 C20.4 0 21 .6 21 1.5 S20.4 3 19.5 3
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 19.5 3
                moveTo(x = 19.5f, y = 3.0f)
                // h -15
                horizontalLineToRelative(dx = -15.0f)
                // C 3.6 3 3 2.4 3 1.5
                curveTo(
                    x1 = 3.6f,
                    y1 = 3.0f,
                    x2 = 3.0f,
                    y2 = 2.4f,
                    x3 = 3.0f,
                    y3 = 1.5f,
                )
                // S 3.6 0 4.5 0
                reflectiveCurveTo(
                    x1 = 3.6f,
                    y1 = 0.0f,
                    x2 = 4.5f,
                    y2 = 0.0f,
                )
                // h 15
                horizontalLineToRelative(dx = 15.0f)
                // C 20.4 0 21 0.6 21 1.5
                curveTo(
                    x1 = 20.4f,
                    y1 = 0.0f,
                    x2 = 21.0f,
                    y2 = 0.6f,
                    x3 = 21.0f,
                    y3 = 1.5f,
                )
                // S 20.4 3 19.5 3
                reflectiveCurveTo(
                    x1 = 20.4f,
                    y1 = 3.0f,
                    x2 = 19.5f,
                    y2 = 3.0f,
                )
            }
            // M1.5 6 h21 C23.4 6 24 6.6 24 7.5 v15 c0 .9 -.6 1.5 -1.5 1.5 h-21 C.6 24 0 23.4 0 22.5 v-15 C0 6.6 .6 6 1.5 6 M6 16.5 h12 v-6 h-3 v3 H9 v-3 H6z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 1.5 6
                moveTo(x = 1.5f, y = 6.0f)
                // h 21
                horizontalLineToRelative(dx = 21.0f)
                // C 23.4 6 24 6.6 24 7.5
                curveTo(
                    x1 = 23.4f,
                    y1 = 6.0f,
                    x2 = 24.0f,
                    y2 = 6.6f,
                    x3 = 24.0f,
                    y3 = 7.5f,
                )
                // v 15
                verticalLineToRelative(dy = 15.0f)
                // c 0 0.9 -0.6 1.5 -1.5 1.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.9f,
                    dx2 = -0.6f,
                    dy2 = 1.5f,
                    dx3 = -1.5f,
                    dy3 = 1.5f,
                )
                // h -21
                horizontalLineToRelative(dx = -21.0f)
                // C 0.6 24 0 23.4 0 22.5
                curveTo(
                    x1 = 0.6f,
                    y1 = 24.0f,
                    x2 = 0.0f,
                    y2 = 23.4f,
                    x3 = 0.0f,
                    y3 = 22.5f,
                )
                // v -15
                verticalLineToRelative(dy = -15.0f)
                // C 0 6.6 0.6 6 1.5 6
                curveTo(
                    x1 = 0.0f,
                    y1 = 6.6f,
                    x2 = 0.6f,
                    y2 = 6.0f,
                    x3 = 1.5f,
                    y3 = 6.0f,
                )
                // M 6 16.5
                moveTo(x = 6.0f, y = 16.5f)
                // h 12
                horizontalLineToRelative(dx = 12.0f)
                // v -6
                verticalLineToRelative(dy = -6.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // v 3
                verticalLineToRelative(dy = 3.0f)
                // H 9
                horizontalLineTo(x = 9.0f)
                // v -3
                verticalLineToRelative(dy = -3.0f)
                // H 6z
                horizontalLineTo(x = 6.0f)
                close()
            }
        }.build().also { _inbox3Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _inbox3Solid: ImageVector? = null
