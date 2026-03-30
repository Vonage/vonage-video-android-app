@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.ChevronDown: ImageVector
    get() {
        val current = _chevronDownLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.vonage.android.theme.VididTheme.ChevronDownLine",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M3.13 9.33 a.75 .75 0 0 1 1.04 -.2 L12 14.35 l7.83 -5.22 a.75 .75 0 1 1 .84 1.24 l-8.25 5.5 a.8 .8 0 0 1 -.84 0 l-8.25 -5.5 a.75 .75 0 0 1 -.2 -1.04
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 3.13 9.33
                moveTo(x = 3.13f, y = 9.33f)
                // a 0.75 0.75 0 0 1 1.04 -0.2
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.04f,
                    dy1 = -0.2f,
                )
                // L 12 14.35
                lineTo(x = 12.0f, y = 14.35f)
                // l 7.83 -5.22
                lineToRelative(dx = 7.83f, dy = -5.22f)
                // a 0.75 0.75 0 1 1 0.84 1.24
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0.84f,
                    dy1 = 1.24f,
                )
                // l -8.25 5.5
                lineToRelative(dx = -8.25f, dy = 5.5f)
                // a 0.8 0.8 0 0 1 -0.84 0
                arcToRelative(
                    a = 0.8f,
                    b = 0.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.84f,
                    dy1 = 0.0f,
                )
                // l -8.25 -5.5
                lineToRelative(dx = -8.25f, dy = -5.5f)
                // a 0.75 0.75 0 0 1 -0.2 -1.04
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.2f,
                    dy1 = -1.04f,
                )
            }
        }.build().also { _chevronDownLine = it }
    }

@Suppress("ObjectPropertyName")
private var _chevronDownLine: ImageVector? = null
