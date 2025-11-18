package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.ClosedCaptioning: ImageVector
    get() {
        val current = _closedCaptioningSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.ClosedCaptioning",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M23 3 H1 a1 1 0 0 0 -1 1 v16 a1 1 0 0 0 1 1 h22 a1 1 0 0 0 1 -1 V4 a1 1 0 0 0 -1 -1 M9.22 14.3 a4 4 0 0 0 1 -.15 10 10 0 0 0 .99 -.35 v1.8 a5.4 5.4 0 0 1 -2.23 .44 A3.5 3.5 0 0 1 6.26 15 a4.3 4.3 0 0 1 -.95 -2.98 5 5 0 0 1 .46 -2.14 3.3 3.3 0 0 1 1.32 -1.42 4 4 0 0 1 2.03 -.5 6 6 0 0 1 2.43 .55 l-.65 1.67 a7 7 0 0 0 -.87 -.34 A3 3 0 0 0 9.1 9.7 a1.4 1.4 0 0 0 -1.18 .62 3 3 0 0 0 -.43 1.71 q0 2.27 1.72 2.27 m7.13 0 a4 4 0 0 0 1.01 -.15 10 10 0 0 0 .99 -.35 v1.8 a5.4 5.4 0 0 1 -2.23 .44 A3.5 3.5 0 0 1 13.4 15 a4.3 4.3 0 0 1 -.95 -2.99 5 5 0 0 1 .46 -2.14 3.3 3.3 0 0 1 1.32 -1.42 4 4 0 0 1 2.03 -.5 6 6 0 0 1 2.43 .55 l-.65 1.67 a7 7 0 0 0 -.87 -.34 3 3 0 0 0 -.94 -.14 1.4 1.4 0 0 0 -1.18 .62 3 3 0 0 0 -.42 1.71 q0 2.27 1.72 2.27
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 23 3
                moveTo(x = 23.0f, y = 3.0f)
                // H 1
                horizontalLineTo(x = 1.0f)
                // a 1 1 0 0 0 -1 1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.0f,
                    dy1 = 1.0f,
                )
                // v 16
                verticalLineToRelative(dy = 16.0f)
                // a 1 1 0 0 0 1 1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.0f,
                    dy1 = 1.0f,
                )
                // h 22
                horizontalLineToRelative(dx = 22.0f)
                // a 1 1 0 0 0 1 -1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.0f,
                    dy1 = -1.0f,
                )
                // V 4
                verticalLineTo(y = 4.0f)
                // a 1 1 0 0 0 -1 -1
                arcToRelative(
                    a = 1.0f,
                    b = 1.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.0f,
                    dy1 = -1.0f,
                )
                // M 9.22 14.3
                moveTo(x = 9.22f, y = 14.3f)
                // a 4 4 0 0 0 1 -0.15
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.0f,
                    dy1 = -0.15f,
                )
                // a 10 10 0 0 0 0.99 -0.35
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.99f,
                    dy1 = -0.35f,
                )
                // v 1.8
                verticalLineToRelative(dy = 1.8f)
                // a 5.4 5.4 0 0 1 -2.23 0.44
                arcToRelative(
                    a = 5.4f,
                    b = 5.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.23f,
                    dy1 = 0.44f,
                )
                // A 3.5 3.5 0 0 1 6.26 15
                arcTo(
                    horizontalEllipseRadius = 3.5f,
                    verticalEllipseRadius = 3.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 6.26f,
                    y1 = 15.0f,
                )
                // a 4.3 4.3 0 0 1 -0.95 -2.98
                arcToRelative(
                    a = 4.3f,
                    b = 4.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.95f,
                    dy1 = -2.98f,
                )
                // a 5 5 0 0 1 0.46 -2.14
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.46f,
                    dy1 = -2.14f,
                )
                // a 3.3 3.3 0 0 1 1.32 -1.42
                arcToRelative(
                    a = 3.3f,
                    b = 3.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.32f,
                    dy1 = -1.42f,
                )
                // a 4 4 0 0 1 2.03 -0.5
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.03f,
                    dy1 = -0.5f,
                )
                // a 6 6 0 0 1 2.43 0.55
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.43f,
                    dy1 = 0.55f,
                )
                // l -0.65 1.67
                lineToRelative(dx = -0.65f, dy = 1.67f)
                // a 7 7 0 0 0 -0.87 -0.34
                arcToRelative(
                    a = 7.0f,
                    b = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.87f,
                    dy1 = -0.34f,
                )
                // A 3 3 0 0 0 9.1 9.7
                arcTo(
                    horizontalEllipseRadius = 3.0f,
                    verticalEllipseRadius = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 9.1f,
                    y1 = 9.7f,
                )
                // a 1.4 1.4 0 0 0 -1.18 0.62
                arcToRelative(
                    a = 1.4f,
                    b = 1.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.18f,
                    dy1 = 0.62f,
                )
                // a 3 3 0 0 0 -0.43 1.71
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.43f,
                    dy1 = 1.71f,
                )
                // q 0 2.27 1.72 2.27
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 2.27f,
                    dx2 = 1.72f,
                    dy2 = 2.27f,
                )
                // m 7.13 0
                moveToRelative(dx = 7.13f, dy = 0.0f)
                // a 4 4 0 0 0 1.01 -0.15
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.01f,
                    dy1 = -0.15f,
                )
                // a 10 10 0 0 0 0.99 -0.35
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.99f,
                    dy1 = -0.35f,
                )
                // v 1.8
                verticalLineToRelative(dy = 1.8f)
                // a 5.4 5.4 0 0 1 -2.23 0.44
                arcToRelative(
                    a = 5.4f,
                    b = 5.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.23f,
                    dy1 = 0.44f,
                )
                // A 3.5 3.5 0 0 1 13.4 15
                arcTo(
                    horizontalEllipseRadius = 3.5f,
                    verticalEllipseRadius = 3.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 13.4f,
                    y1 = 15.0f,
                )
                // a 4.3 4.3 0 0 1 -0.95 -2.99
                arcToRelative(
                    a = 4.3f,
                    b = 4.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.95f,
                    dy1 = -2.99f,
                )
                // a 5 5 0 0 1 0.46 -2.14
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.46f,
                    dy1 = -2.14f,
                )
                // a 3.3 3.3 0 0 1 1.32 -1.42
                arcToRelative(
                    a = 3.3f,
                    b = 3.3f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.32f,
                    dy1 = -1.42f,
                )
                // a 4 4 0 0 1 2.03 -0.5
                arcToRelative(
                    a = 4.0f,
                    b = 4.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.03f,
                    dy1 = -0.5f,
                )
                // a 6 6 0 0 1 2.43 0.55
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 2.43f,
                    dy1 = 0.55f,
                )
                // l -0.65 1.67
                lineToRelative(dx = -0.65f, dy = 1.67f)
                // a 7 7 0 0 0 -0.87 -0.34
                arcToRelative(
                    a = 7.0f,
                    b = 7.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.87f,
                    dy1 = -0.34f,
                )
                // a 3 3 0 0 0 -0.94 -0.14
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.94f,
                    dy1 = -0.14f,
                )
                // a 1.4 1.4 0 0 0 -1.18 0.62
                arcToRelative(
                    a = 1.4f,
                    b = 1.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.18f,
                    dy1 = 0.62f,
                )
                // a 3 3 0 0 0 -0.42 1.71
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.42f,
                    dy1 = 1.71f,
                )
                // q 0 2.27 1.72 2.27
                quadToRelative(
                    dx1 = 0.0f,
                    dy1 = 2.27f,
                    dx2 = 1.72f,
                    dy2 = 2.27f,
                )
            }
        }.build().also { _closedCaptioningSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _closedCaptioningSolid: ImageVector? = null
