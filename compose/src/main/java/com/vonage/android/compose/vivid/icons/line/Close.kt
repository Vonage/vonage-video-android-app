package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.Close: ImageVector
    get() {
        val current = _closeLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.Close",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M20.78 4.28 a.75 .75 0 0 0 -1.06 -1.06 L12 10.94 4.28 3.22 a.75 .75 0 0 0 -1.06 1.06 L10.94 12 l-7.72 7.72 a.75 .75 0 1 0 1.06 1.06 L12 13.06 l7.72 7.72 a.75 .75 0 1 0 1.06 -1.06 L13.06 12z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 20.78 4.28
                moveTo(x = 20.78f, y = 4.28f)
                // a 0.75 0.75 0 0 0 -1.06 -1.06
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.06f,
                    dy1 = -1.06f,
                )
                // L 12 10.94
                lineTo(x = 12.0f, y = 10.94f)
                // L 4.28 3.22
                lineTo(x = 4.28f, y = 3.22f)
                // a 0.75 0.75 0 0 0 -1.06 1.06
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.06f,
                    dy1 = 1.06f,
                )
                // L 10.94 12
                lineTo(x = 10.94f, y = 12.0f)
                // l -7.72 7.72
                lineToRelative(dx = -7.72f, dy = 7.72f)
                // a 0.75 0.75 0 1 0 1.06 1.06
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 1.06f,
                    dy1 = 1.06f,
                )
                // L 12 13.06
                lineTo(x = 12.0f, y = 13.06f)
                // l 7.72 7.72
                lineToRelative(dx = 7.72f, dy = 7.72f)
                // a 0.75 0.75 0 1 0 1.06 -1.06
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 1.06f,
                    dy1 = -1.06f,
                )
                // L 13.06 12z
                lineTo(x = 13.06f, y = 12.0f)
                close()
            }
        }.build().also { _closeLine = it }
    }

@Suppress("ObjectPropertyName")
private var _closeLine: ImageVector? = null
