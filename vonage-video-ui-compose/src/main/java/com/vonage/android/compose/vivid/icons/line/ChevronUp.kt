@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.ChevronUp: ImageVector
    get() {
        val current = _chevronUpLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.ChevronUp",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M3.13 14.67 a.75 .75 0 0 0 1.04 .2 L12 9.65 l7.83 5.22 a.75 .75 0 0 0 .84 -1.24 l-8.25 -5.5 a.8 .8 0 0 0 -.84 0 l-8.25 5.5 a.75 .75 0 0 0 -.2 1.04
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 3.13 14.67
                moveTo(x = 3.13f, y = 14.67f)
                // a 0.75 0.75 0 0 0 1.04 0.2
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.04f,
                    dy1 = 0.2f,
                )
                // L 12 9.65
                lineTo(x = 12.0f, y = 9.65f)
                // l 7.83 5.22
                lineToRelative(dx = 7.83f, dy = 5.22f)
                // a 0.75 0.75 0 0 0 0.84 -1.24
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.84f,
                    dy1 = -1.24f,
                )
                // l -8.25 -5.5
                lineToRelative(dx = -8.25f, dy = -5.5f)
                // a 0.8 0.8 0 0 0 -0.84 0
                arcToRelative(
                    a = 0.8f,
                    b = 0.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.84f,
                    dy1 = 0.0f,
                )
                // l -8.25 5.5
                lineToRelative(dx = -8.25f, dy = 5.5f)
                // a 0.75 0.75 0 0 0 -0.2 1.04
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.2f,
                    dy1 = 1.04f,
                )
            }
        }.build().also { _chevronUpLine = it }
    }

@Suppress("ObjectPropertyName")
private var _chevronUpLine: ImageVector? = null
