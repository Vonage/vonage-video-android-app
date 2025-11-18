package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Headset2: ImageVector
    get() {
        val current = _headset2Solid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Headset2",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M12 0 C6.48 0 2 4.4 2 9.82 v7.63 a3.3 3.3 0 0 0 3.33 3.28 h3.34 V12 H4.22 V9.82 A7.7 7.7 0 0 1 12 2.18 a7.7 7.7 0 0 1 7.78 7.64 V12 h-4.45 v8.73 h4.45 V22 H12 v2 h6.67 A3.3 3.3 0 0 0 22 20.73 V9.82 A9.9 9.9 0 0 0 12 0
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 12 0
                moveTo(x = 12.0f, y = 0.0f)
                // C 6.48 0 2 4.4 2 9.82
                curveTo(
                    x1 = 6.48f,
                    y1 = 0.0f,
                    x2 = 2.0f,
                    y2 = 4.4f,
                    x3 = 2.0f,
                    y3 = 9.82f,
                )
                // v 7.63
                verticalLineToRelative(dy = 7.63f)
                // a 3.3 3.3 0 0 0 3.33 3.28
                arcToRelative(
                    a = 3.3f,
                    b = 3.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 3.33f,
                    dy1 = 3.28f,
                )
                // h 3.34
                horizontalLineToRelative(dx = 3.34f)
                // V 12
                verticalLineTo(y = 12.0f)
                // H 4.22
                horizontalLineTo(x = 4.22f)
                // V 9.82
                verticalLineTo(y = 9.82f)
                // A 7.7 7.7 0 0 1 12 2.18
                arcTo(
                    horizontalEllipseRadius = 7.7f,
                    verticalEllipseRadius = 7.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 12.0f,
                    y1 = 2.18f,
                )
                // a 7.7 7.7 0 0 1 7.78 7.64
                arcToRelative(
                    a = 7.7f,
                    b = 7.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 7.78f,
                    dy1 = 7.64f,
                )
                // V 12
                verticalLineTo(y = 12.0f)
                // h -4.45
                horizontalLineToRelative(dx = -4.45f)
                // v 8.73
                verticalLineToRelative(dy = 8.73f)
                // h 4.45
                horizontalLineToRelative(dx = 4.45f)
                // V 22
                verticalLineTo(y = 22.0f)
                // H 12
                horizontalLineTo(x = 12.0f)
                // v 2
                verticalLineToRelative(dy = 2.0f)
                // h 6.67
                horizontalLineToRelative(dx = 6.67f)
                // A 3.3 3.3 0 0 0 22 20.73
                arcTo(
                    horizontalEllipseRadius = 3.3f,
                    verticalEllipseRadius = 3.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 22.0f,
                    y1 = 20.73f,
                )
                // V 9.82
                verticalLineTo(y = 9.82f)
                // A 9.9 9.9 0 0 0 12 0
                arcTo(
                    horizontalEllipseRadius = 9.9f,
                    verticalEllipseRadius = 9.9f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 12.0f,
                    y1 = 0.0f,
                )
            }
        }.build().also { _headset2Solid = it }
    }

@Suppress("ObjectPropertyName")
private var _headset2Solid: ImageVector? = null
