@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.ChevronDown: ImageVector
    get() {
        val current = _chevronDownSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.ChevronDown",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M18.56 8.33 12 13.58 5.44 8.33 a1.5 1.5 0 0 0 -1.88 2.34 l7.5 6 a1.5 1.5 0 0 0 1.88 0 l7.5 -6 a1.5 1.5 0 1 0 -1.88 -2.34
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 18.56 8.33
                moveTo(x = 18.56f, y = 8.33f)
                // L 12 13.58
                lineTo(x = 12.0f, y = 13.58f)
                // L 5.44 8.33
                lineTo(x = 5.44f, y = 8.33f)
                // a 1.5 1.5 0 0 0 -1.88 2.34
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.88f,
                    dy1 = 2.34f,
                )
                // l 7.5 6
                lineToRelative(dx = 7.5f, dy = 6.0f)
                // a 1.5 1.5 0 0 0 1.88 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.88f,
                    dy1 = 0.0f,
                )
                // l 7.5 -6
                lineToRelative(dx = 7.5f, dy = -6.0f)
                // a 1.5 1.5 0 1 0 -1.88 -2.34
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = -1.88f,
                    dy1 = -2.34f,
                )
            }
        }.build().also { _chevronDownSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _chevronDownSolid: ImageVector? = null
