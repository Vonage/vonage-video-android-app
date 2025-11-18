package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.BlurOff: ImageVector
    get() {
        val current = _blurOffLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.BlurOff",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M1.3 .22 A.76 .76 0 0 0 .21 1.3 l22.49 22.49 a.76 .76 0 1 0 1.07 -1.07 l-3.68 -3.68 v-.08 a1.16 1.16 0 0 0 -1.24 -1.16 l-2.9 -2.9 a1.74 1.74 0 0 0 -2.21 -2.22 l-2.42 -2.42 a1.74 1.74 0 0 0 -2.22 -2.2 l-2.9 -2.91 A1.16 1.16 0 0 0 4.97 3.9z m13.02 1.94 a.57 .57 0 0 0 .57 -.58 A.57 .57 0 0 0 14.32 1 a.57 .57 0 0 0 -.58 .58 .57 .57 0 0 0 .58 .58 M3.89 9.68 a1.2 1.2 0 0 1 .67 -1.04 l1.54 1.53 a1.16 1.16 0 0 1 -2.2 -.49 m4.06 4.64 a1.7 1.7 0 0 1 .82 -1.48 l2.4 2.4 a1.73 1.73 0 0 1 -3.22 -.92 m5.88 3.58 a1.16 1.16 0 1 0 1.54 1.54z m-9.94 -3.58 a1.16 1.16 0 0 1 1.16 -1.16 1.16 1.16 0 0 1 1.16 1.16 1.16 1.16 0 0 1 -1.16 1.15 1.16 1.16 0 0 1 -1.16 -1.15 m1.16 3.47 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.16 M1 9.69 A.57 .57 0 0 1 1.58 9.1 a.57 .57 0 0 1 .58 .57 .57 .57 0 0 1 -.58 .58 A.57 .57 0 0 1 1 9.68 m21.42 .57 A.57 .57 0 0 0 23 9.68 a.57 .57 0 0 0 -.58 -.57 .57 .57 0 0 0 -.58 .57 .57 .57 0 0 0 .58 .58 m-6.95 -5.2 a1.16 1.16 0 0 1 -1.15 1.15 1.16 1.16 0 0 1 -1.16 -1.16 1.16 1.16 0 0 1 1.16 -1.16 1.16 1.16 0 0 1 1.15 1.16 M1 14.31 a.57 .57 0 0 1 .58 -.58 .57 .57 0 0 1 .58 .58 .57 .57 0 0 1 -.58 .57 A.57 .57 0 0 1 1 14.32 m8.68 7.52 a.57 .57 0 0 0 -.57 .58 A.57 .57 0 0 0 9.68 23 a.57 .57 0 0 0 .58 -.58 .57 .57 0 0 0 -.58 -.58 m.58 -20.26 a.57 .57 0 0 1 -.58 .58 .57 .57 0 0 1 -.57 -.58 A.57 .57 0 0 1 9.68 1 a.57 .57 0 0 1 .58 .58 M9.68 6.2 a1.16 1.16 0 0 0 1.16 -1.16 A1.16 1.16 0 0 0 9.68 3.9 a1.16 1.16 0 0 0 -1.15 1.16 1.16 1.16 0 0 0 1.15 1.16 m8.11 8.1 a1.16 1.16 0 0 1 1.16 -1.15 1.16 1.16 0 0 1 1.16 1.16 1.16 1.16 0 0 1 -1.16 1.15 1.16 1.16 0 0 1 -1.16 -1.15 m1.16 -5.78 a1.16 1.16 0 0 0 -1.16 1.15 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.15 m-1.16 -3.48 a1.16 1.16 0 0 1 1.16 -1.16 1.16 1.16 0 0 1 1.16 1.16 1.16 1.16 0 0 1 -1.16 1.16 1.16 1.16 0 0 1 -1.16 -1.16 m4.63 8.69 a.57 .57 0 0 0 -.58 .58 .57 .57 0 0 0 .58 .57 .57 .57 0 0 0 .58 -.57 .57 .57 0 0 0 -.58 -.58 m-8.68 8.68 a.57 .57 0 0 1 .58 -.58 .57 .57 0 0 1 .57 .58 .57 .57 0 0 1 -.57 .58 .57 .57 0 0 1 -.58 -.58 M9.68 17.8 a1.16 1.16 0 0 0 -1.15 1.16 1.16 1.16 0 0 0 1.15 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.16 m2.9 -8.1 a1.73 1.73 0 1 1 3.47 -.01 1.73 1.73 0 0 1 -3.47 0
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 1.3 0.22
                moveTo(x = 1.3f, y = 0.22f)
                // A 0.76 0.76 0 0 0 0.21 1.3
                arcTo(
                    horizontalEllipseRadius = 0.76f,
                    verticalEllipseRadius = 0.76f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 0.21f,
                    y1 = 1.3f,
                )
                // l 22.49 22.49
                lineToRelative(dx = 22.49f, dy = 22.49f)
                // a 0.76 0.76 0 1 0 1.07 -1.07
                arcToRelative(
                    a = 0.76f,
                    b = 0.76f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 1.07f,
                    dy1 = -1.07f,
                )
                // l -3.68 -3.68
                lineToRelative(dx = -3.68f, dy = -3.68f)
                // v -0.08
                verticalLineToRelative(dy = -0.08f)
                // a 1.16 1.16 0 0 0 -1.24 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.24f,
                    dy1 = -1.16f,
                )
                // l -2.9 -2.9
                lineToRelative(dx = -2.9f, dy = -2.9f)
                // a 1.74 1.74 0 0 0 -2.21 -2.22
                arcToRelative(
                    a = 1.74f,
                    b = 1.74f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.21f,
                    dy1 = -2.22f,
                )
                // l -2.42 -2.42
                lineToRelative(dx = -2.42f, dy = -2.42f)
                // a 1.74 1.74 0 0 0 -2.22 -2.2
                arcToRelative(
                    a = 1.74f,
                    b = 1.74f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -2.22f,
                    dy1 = -2.2f,
                )
                // l -2.9 -2.91
                lineToRelative(dx = -2.9f, dy = -2.91f)
                // A 1.16 1.16 0 0 0 4.97 3.9z
                arcTo(
                    horizontalEllipseRadius = 1.16f,
                    verticalEllipseRadius = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 4.97f,
                    y1 = 3.9f,
                )
                close()
                // m 13.02 1.94
                moveToRelative(dx = 13.02f, dy = 1.94f)
                // a 0.57 0.57 0 0 0 0.57 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.57f,
                    dy1 = -0.58f,
                )
                // A 0.57 0.57 0 0 0 14.32 1
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 14.32f,
                    y1 = 1.0f,
                )
                // a 0.57 0.57 0 0 0 -0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = 0.58f,
                )
                // a 0.57 0.57 0 0 0 0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.58f,
                    dy1 = 0.58f,
                )
                // M 3.89 9.68
                moveTo(x = 3.89f, y = 9.68f)
                // a 1.2 1.2 0 0 1 0.67 -1.04
                arcToRelative(
                    a = 1.2f,
                    b = 1.2f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.67f,
                    dy1 = -1.04f,
                )
                // l 1.54 1.53
                lineToRelative(dx = 1.54f, dy = 1.53f)
                // a 1.16 1.16 0 0 1 -2.2 -0.49
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -2.2f,
                    dy1 = -0.49f,
                )
                // m 4.06 4.64
                moveToRelative(dx = 4.06f, dy = 4.64f)
                // a 1.7 1.7 0 0 1 0.82 -1.48
                arcToRelative(
                    a = 1.7f,
                    b = 1.7f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.82f,
                    dy1 = -1.48f,
                )
                // l 2.4 2.4
                lineToRelative(dx = 2.4f, dy = 2.4f)
                // a 1.73 1.73 0 0 1 -3.22 -0.92
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.22f,
                    dy1 = -0.92f,
                )
                // m 5.88 3.58
                moveToRelative(dx = 5.88f, dy = 3.58f)
                // a 1.16 1.16 0 1 0 1.54 1.54z
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 1.54f,
                    dy1 = 1.54f,
                )
                close()
                // m -9.94 -3.58
                moveToRelative(dx = -9.94f, dy = -3.58f)
                // a 1.16 1.16 0 0 1 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 1 1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 1 -1.16 1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = 1.15f,
                )
                // a 1.16 1.16 0 0 1 -1.16 -1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = -1.15f,
                )
                // m 1.16 3.47
                moveToRelative(dx = 1.16f, dy = 3.47f)
                // a 1.16 1.16 0 0 0 -1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 0 1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 0 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 0 -1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.16f,
                    dy1 = -1.16f,
                )
                // M 1 9.69
                moveTo(x = 1.0f, y = 9.69f)
                // A 0.57 0.57 0 0 1 1.58 9.1
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 1.58f,
                    y1 = 9.1f,
                )
                // a 0.57 0.57 0 0 1 0.58 0.57
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.58f,
                    dy1 = 0.57f,
                )
                // a 0.57 0.57 0 0 1 -0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.58f,
                    dy1 = 0.58f,
                )
                // A 0.57 0.57 0 0 1 1 9.68
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 1.0f,
                    y1 = 9.68f,
                )
                // m 21.42 0.57
                moveToRelative(dx = 21.42f, dy = 0.57f)
                // A 0.57 0.57 0 0 0 23 9.68
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 23.0f,
                    y1 = 9.68f,
                )
                // a 0.57 0.57 0 0 0 -0.58 -0.57
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = -0.57f,
                )
                // a 0.57 0.57 0 0 0 -0.58 0.57
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = 0.57f,
                )
                // a 0.57 0.57 0 0 0 0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.58f,
                    dy1 = 0.58f,
                )
                // m -6.95 -5.2
                moveToRelative(dx = -6.95f, dy = -5.2f)
                // a 1.16 1.16 0 0 1 -1.15 1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.15f,
                    dy1 = 1.15f,
                )
                // a 1.16 1.16 0 0 1 -1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 1 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 1 1.15 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.15f,
                    dy1 = 1.16f,
                )
                // M 1 14.31
                moveTo(x = 1.0f, y = 14.31f)
                // a 0.57 0.57 0 0 1 0.58 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.58f,
                    dy1 = -0.58f,
                )
                // a 0.57 0.57 0 0 1 0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.58f,
                    dy1 = 0.58f,
                )
                // a 0.57 0.57 0 0 1 -0.58 0.57
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.58f,
                    dy1 = 0.57f,
                )
                // A 0.57 0.57 0 0 1 1 14.32
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 1.0f,
                    y1 = 14.32f,
                )
                // m 8.68 7.52
                moveToRelative(dx = 8.68f, dy = 7.52f)
                // a 0.57 0.57 0 0 0 -0.57 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.57f,
                    dy1 = 0.58f,
                )
                // A 0.57 0.57 0 0 0 9.68 23
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 9.68f,
                    y1 = 23.0f,
                )
                // a 0.57 0.57 0 0 0 0.58 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.58f,
                    dy1 = -0.58f,
                )
                // a 0.57 0.57 0 0 0 -0.58 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = -0.58f,
                )
                // m 0.58 -20.26
                moveToRelative(dx = 0.58f, dy = -20.26f)
                // a 0.57 0.57 0 0 1 -0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.58f,
                    dy1 = 0.58f,
                )
                // a 0.57 0.57 0 0 1 -0.57 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.57f,
                    dy1 = -0.58f,
                )
                // A 0.57 0.57 0 0 1 9.68 1
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 9.68f,
                    y1 = 1.0f,
                )
                // a 0.57 0.57 0 0 1 0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.58f,
                    dy1 = 0.58f,
                )
                // M 9.68 6.2
                moveTo(x = 9.68f, y = 6.2f)
                // a 1.16 1.16 0 0 0 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // A 1.16 1.16 0 0 0 9.68 3.9
                arcTo(
                    horizontalEllipseRadius = 1.16f,
                    verticalEllipseRadius = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 9.68f,
                    y1 = 3.9f,
                )
                // a 1.16 1.16 0 0 0 -1.15 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.15f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 0 1.15 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.15f,
                    dy1 = 1.16f,
                )
                // m 8.11 8.1
                moveToRelative(dx = 8.11f, dy = 8.1f)
                // a 1.16 1.16 0 0 1 1.16 -1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = -1.15f,
                )
                // a 1.16 1.16 0 0 1 1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 1 -1.16 1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = 1.15f,
                )
                // a 1.16 1.16 0 0 1 -1.16 -1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = -1.15f,
                )
                // m 1.16 -5.78
                moveToRelative(dx = 1.16f, dy = -5.78f)
                // a 1.16 1.16 0 0 0 -1.16 1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.16f,
                    dy1 = 1.15f,
                )
                // a 1.16 1.16 0 0 0 1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 0 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 0 -1.16 -1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.16f,
                    dy1 = -1.15f,
                )
                // m -1.16 -3.48
                moveToRelative(dx = -1.16f, dy = -3.48f)
                // a 1.16 1.16 0 0 1 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 1 1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 1 -1.16 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 1 -1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = -1.16f,
                )
                // m 4.63 8.69
                moveToRelative(dx = 4.63f, dy = 8.69f)
                // a 0.57 0.57 0 0 0 -0.58 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = 0.58f,
                )
                // a 0.57 0.57 0 0 0 0.58 0.57
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.58f,
                    dy1 = 0.57f,
                )
                // a 0.57 0.57 0 0 0 0.58 -0.57
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.58f,
                    dy1 = -0.57f,
                )
                // a 0.57 0.57 0 0 0 -0.58 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.58f,
                    dy1 = -0.58f,
                )
                // m -8.68 8.68
                moveToRelative(dx = -8.68f, dy = 8.68f)
                // a 0.57 0.57 0 0 1 0.58 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.58f,
                    dy1 = -0.58f,
                )
                // a 0.57 0.57 0 0 1 0.57 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.57f,
                    dy1 = 0.58f,
                )
                // a 0.57 0.57 0 0 1 -0.57 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.57f,
                    dy1 = 0.58f,
                )
                // a 0.57 0.57 0 0 1 -0.58 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -0.58f,
                    dy1 = -0.58f,
                )
                // M 9.68 17.8
                moveTo(x = 9.68f, y = 17.8f)
                // a 1.16 1.16 0 0 0 -1.15 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.15f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 0 1.15 1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.15f,
                    dy1 = 1.16f,
                )
                // a 1.16 1.16 0 0 0 1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 0 -1.16 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.16f,
                    dy1 = -1.16f,
                )
                // m 2.9 -8.1
                moveToRelative(dx = 2.9f, dy = -8.1f)
                // a 1.73 1.73 0 1 1 3.47 -0.01
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 3.47f,
                    dy1 = -0.01f,
                )
                // a 1.73 1.73 0 0 1 -3.47 0
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.47f,
                    dy1 = 0.0f,
                )
            }
        }.build().also { _blurOffLine = it }
    }

@Suppress("ObjectPropertyName")
private var _blurOffLine: ImageVector? = null
