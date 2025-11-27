@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.Enter: ImageVector
    get() {
        val current = _enterLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "com.vonage.android.theme.VididTheme.EnterLine",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M9.75 1.6 C9.34 1.6 9 1.96 9 2.4 v4.8 C9 7.64 8.66 8 8.25 8 S7.5 7.64 7.5 7.2 V2.4 c0 -1.33 1 -2.4 2.25 -2.4 h12 C22.99 0 24 1.07 24 2.4 v19.2 c0 1.32 -1 2.4 -2.25 2.4 h-12 c-1.24 0 -2.25 -1.07 -2.25 -2.4 v-4.8 c0 -.44 .34 -.8 .75 -.8 S9 16.36 9 16.8 v4.8 c0 .44 .34 .8 .75 .8 h12 c.41 0 .75 -.36 .75 -.8 V2.4 c0 -.44 -.34 -.8 -.75 -.8z M0 12 c0 -.44 .34 -.8 .75 -.8 h14.69 l-3.22 -3.43 a.84 .84 0 0 1 0 -1.14 .72 .72 0 0 1 1.06 0 l4.5 4.8 A.8 .8 0 0 1 18 12 a1 1 0 0 1 -.22 .57 l-4.5 4.8 a.72 .72 0 0 1 -1.06 0 .84 .84 0 0 1 0 -1.14 l3.22 -3.43 H.75 C.34 12.8 0 12.44 0 12
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 9.75 1.6
                moveTo(x = 9.75f, y = 1.6f)
                // C 9.34 1.6 9 1.96 9 2.4
                curveTo(
                    x1 = 9.34f,
                    y1 = 1.6f,
                    x2 = 9.0f,
                    y2 = 1.96f,
                    x3 = 9.0f,
                    y3 = 2.4f,
                )
                // v 4.8
                verticalLineToRelative(dy = 4.8f)
                // C 9 7.64 8.66 8 8.25 8
                curveTo(
                    x1 = 9.0f,
                    y1 = 7.64f,
                    x2 = 8.66f,
                    y2 = 8.0f,
                    x3 = 8.25f,
                    y3 = 8.0f,
                )
                // S 7.5 7.64 7.5 7.2
                reflectiveCurveTo(
                    x1 = 7.5f,
                    y1 = 7.64f,
                    x2 = 7.5f,
                    y2 = 7.2f,
                )
                // V 2.4
                verticalLineTo(y = 2.4f)
                // c 0 -1.33 1 -2.4 2.25 -2.4
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -1.33f,
                    dx2 = 1.0f,
                    dy2 = -2.4f,
                    dx3 = 2.25f,
                    dy3 = -2.4f,
                )
                // h 12
                horizontalLineToRelative(dx = 12.0f)
                // C 22.99 0 24 1.07 24 2.4
                curveTo(
                    x1 = 22.99f,
                    y1 = 0.0f,
                    x2 = 24.0f,
                    y2 = 1.07f,
                    x3 = 24.0f,
                    y3 = 2.4f,
                )
                // v 19.2
                verticalLineToRelative(dy = 19.2f)
                // c 0 1.32 -1 2.4 -2.25 2.4
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 1.32f,
                    dx2 = -1.0f,
                    dy2 = 2.4f,
                    dx3 = -2.25f,
                    dy3 = 2.4f,
                )
                // h -12
                horizontalLineToRelative(dx = -12.0f)
                // c -1.24 0 -2.25 -1.07 -2.25 -2.4
                curveToRelative(
                    dx1 = -1.24f,
                    dy1 = 0.0f,
                    dx2 = -2.25f,
                    dy2 = -1.07f,
                    dx3 = -2.25f,
                    dy3 = -2.4f,
                )
                // v -4.8
                verticalLineToRelative(dy = -4.8f)
                // c 0 -0.44 0.34 -0.8 0.75 -0.8
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.44f,
                    dx2 = 0.34f,
                    dy2 = -0.8f,
                    dx3 = 0.75f,
                    dy3 = -0.8f,
                )
                // S 9 16.36 9 16.8
                reflectiveCurveTo(
                    x1 = 9.0f,
                    y1 = 16.36f,
                    x2 = 9.0f,
                    y2 = 16.8f,
                )
                // v 4.8
                verticalLineToRelative(dy = 4.8f)
                // c 0 0.44 0.34 0.8 0.75 0.8
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = 0.44f,
                    dx2 = 0.34f,
                    dy2 = 0.8f,
                    dx3 = 0.75f,
                    dy3 = 0.8f,
                )
                // h 12
                horizontalLineToRelative(dx = 12.0f)
                // c 0.41 0 0.75 -0.36 0.75 -0.8
                curveToRelative(
                    dx1 = 0.41f,
                    dy1 = 0.0f,
                    dx2 = 0.75f,
                    dy2 = -0.36f,
                    dx3 = 0.75f,
                    dy3 = -0.8f,
                )
                // V 2.4
                verticalLineTo(y = 2.4f)
                // c 0 -0.44 -0.34 -0.8 -0.75 -0.8z
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.44f,
                    dx2 = -0.34f,
                    dy2 = -0.8f,
                    dx3 = -0.75f,
                    dy3 = -0.8f,
                )
                close()
                // M 0 12
                moveTo(x = 0.0f, y = 12.0f)
                // c 0 -0.44 0.34 -0.8 0.75 -0.8
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -0.44f,
                    dx2 = 0.34f,
                    dy2 = -0.8f,
                    dx3 = 0.75f,
                    dy3 = -0.8f,
                )
                // h 14.69
                horizontalLineToRelative(dx = 14.69f)
                // l -3.22 -3.43
                lineToRelative(dx = -3.22f, dy = -3.43f)
                // a 0.84 0.84 0 0 1 0 -1.14
                arcToRelative(
                    a = 0.84f,
                    b = 0.84f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.14f,
                )
                // a 0.72 0.72 0 0 1 1.06 0
                arcToRelative(
                    a = 0.72f,
                    b = 0.72f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.06f,
                    dy1 = 0.0f,
                )
                // l 4.5 4.8
                lineToRelative(dx = 4.5f, dy = 4.8f)
                // A 0.8 0.8 0 0 1 18 12
                arcTo(
                    horizontalEllipseRadius = 0.8f,
                    verticalEllipseRadius = 0.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 18.0f,
                    y1 = 12.0f,
                )
                // a 1 1 0 0 1 -0.22 0.57
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.22f,
                    dy1 = 0.57f,
                )
                // l -4.5 4.8
                lineToRelative(dx = -4.5f, dy = 4.8f)
                // a 0.72 0.72 0 0 1 -1.06 0
                arcToRelative(
                    a = 0.72f,
                    b = 0.72f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.06f,
                    dy1 = 0.0f,
                )
                // a 0.84 0.84 0 0 1 0 -1.14
                arcToRelative(
                    a = 0.84f,
                    b = 0.84f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -1.14f,
                )
                // l 3.22 -3.43
                lineToRelative(dx = 3.22f, dy = -3.43f)
                // H 0.75
                horizontalLineTo(x = 0.75f)
                // C 0.34 12.8 0 12.44 0 12
                curveTo(
                    x1 = 0.34f,
                    y1 = 12.8f,
                    x2 = 0.0f,
                    y2 = 12.44f,
                    x3 = 0.0f,
                    y3 = 12.0f,
                )
            }
        }.build().also { _enterLine = it }
    }

@Suppress("ObjectPropertyName")
private var _enterLine: ImageVector? = null
