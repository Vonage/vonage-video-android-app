@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.Plus: ImageVector
    get() {
        val current = _plusLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.Plus",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M11.25 21.75 a.75 .75 0 0 0 1.5 0 v-9 h9 a.75 .75 0 0 0 0 -1.5 h-9 v-9 a.75 .75 0 0 0 -1.5 0 v9 h-9 a.75 .75 0 1 0 0 1.5 h9z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 11.25 21.75
                moveTo(x = 11.25f, y = 21.75f)
                // a 0.75 0.75 0 0 0 1.5 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.5f,
                    dy1 = 0.0f,
                )
                // v -9
                verticalLineToRelative(dy = -9.0f)
                // h 9
                horizontalLineToRelative(dx = 9.0f)
                // a 0.75 0.75 0 0 0 0 -1.5
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -1.5f,
                )
                // h -9
                horizontalLineToRelative(dx = -9.0f)
                // v -9
                verticalLineToRelative(dy = -9.0f)
                // a 0.75 0.75 0 0 0 -1.5 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.5f,
                    dy1 = 0.0f,
                )
                // v 9
                verticalLineToRelative(dy = 9.0f)
                // h -9
                horizontalLineToRelative(dx = -9.0f)
                // a 0.75 0.75 0 1 0 0 1.5
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 1.5f,
                )
                // h 9z
                horizontalLineToRelative(dx = 9.0f)
                close()
            }
        }.build().also { _plusLine = it }
    }

@Suppress("ObjectPropertyName")
private var _plusLine: ImageVector? = null
