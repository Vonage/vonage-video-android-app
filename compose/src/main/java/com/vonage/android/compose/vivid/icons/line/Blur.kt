package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.Blur: ImageVector
    get() {
        val current = _blurLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.Blur",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M5.05 13.16 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.15 1.16 1.16 0 0 0 1.16 -1.15 1.16 1.16 0 0 0 -1.16 -1.16 m0 4.63 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.16 m0 -9.26 A1.16 1.16 0 0 0 3.9 9.68 a1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.15 M1.58 9.1 A.57 .57 0 0 0 1 9.68 a.57 .57 0 0 0 .58 .58 .57 .57 0 0 0 .58 -.58 .57 .57 0 0 0 -.58 -.57 m3.47 -5.22 A1.16 1.16 0 0 0 3.9 5.05 a1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 A1.16 1.16 0 0 0 5.05 3.9 m17.37 6.37 A.57 .57 0 0 0 23 9.68 a.57 .57 0 0 0 -.58 -.57 .57 .57 0 0 0 -.58 .57 .57 .57 0 0 0 .58 .58 m-8.1 -4.05 a1.16 1.16 0 0 0 1.15 -1.16 1.16 1.16 0 0 0 -1.15 -1.16 1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.16 m0 -4.05 a.57 .57 0 0 0 .57 -.58 A.57 .57 0 0 0 14.32 1 a.57 .57 0 0 0 -.58 .58 .57 .57 0 0 0 .58 .58 M1.58 13.74 A.57 .57 0 0 0 1 14.32 a.57 .57 0 0 0 .58 .57 .57 .57 0 0 0 .58 -.57 .57 .57 0 0 0 -.58 -.58 m8.1 8.1 a.57 .57 0 0 0 -.57 .58 A.57 .57 0 0 0 9.68 23 a.57 .57 0 0 0 .58 -.58 .57 .57 0 0 0 -.58 -.58 m0 -19.68 a.57 .57 0 0 0 .58 -.58 A.57 .57 0 0 0 9.68 1 a.57 .57 0 0 0 -.57 .58 .57 .57 0 0 0 .57 .58 m0 4.05 a1.16 1.16 0 0 0 1.16 -1.16 A1.16 1.16 0 0 0 9.68 3.9 a1.16 1.16 0 0 0 -1.15 1.16 1.16 1.16 0 0 0 1.15 1.16 m0 6.37 a1.73 1.73 0 1 0 0 3.47 1.73 1.73 0 0 0 0 -3.47 m9.27 .58 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.15 1.16 1.16 0 0 0 1.16 -1.15 1.16 1.16 0 0 0 -1.16 -1.16 m0 4.63 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.16 m0 -9.26 a1.16 1.16 0 0 0 -1.16 1.15 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.15 m0 -4.64 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.16 m3.47 9.85 a.57 .57 0 0 0 -.58 .58 .57 .57 0 0 0 .58 .57 .57 .57 0 0 0 .58 -.57 .57 .57 0 0 0 -.58 -.58 m-8.1 4.05 a1.16 1.16 0 0 0 -1.16 1.16 1.16 1.16 0 0 0 1.16 1.16 1.16 1.16 0 0 0 1.15 -1.16 1.16 1.16 0 0 0 -1.15 -1.16 m0 4.05 a.57 .57 0 0 0 -.58 .58 .57 .57 0 0 0 .58 .58 .57 .57 0 0 0 .57 -.58 .57 .57 0 0 0 -.57 -.58 M9.68 7.94 a1.73 1.73 0 1 0 0 3.48 1.73 1.73 0 0 0 0 -3.47 m0 9.85 a1.16 1.16 0 0 0 -1.15 1.16 1.16 1.16 0 0 0 1.15 1.16 1.16 1.16 0 0 0 1.16 -1.16 1.16 1.16 0 0 0 -1.16 -1.16 m4.64 -5.21 a1.73 1.73 0 1 0 0 3.47 1.73 1.73 0 0 0 0 -3.47 m0 -4.63 a1.73 1.73 0 1 0 0 3.47 1.73 1.73 0 0 0 0 -3.47
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 5.05 13.16
                moveTo(x = 5.05f, y = 13.16f)
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
                // a 1.16 1.16 0 0 0 1.16 1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = 1.15f,
                )
                // a 1.16 1.16 0 0 0 1.16 -1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = -1.15f,
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
                // m 0 4.63
                moveToRelative(dx = 0.0f, dy = 4.63f)
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
                // m 0 -9.26
                moveToRelative(dx = 0.0f, dy = -9.26f)
                // A 1.16 1.16 0 0 0 3.9 9.68
                arcTo(
                    horizontalEllipseRadius = 1.16f,
                    verticalEllipseRadius = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 3.9f,
                    y1 = 9.68f,
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
                // M 1.58 9.1
                moveTo(x = 1.58f, y = 9.1f)
                // A 0.57 0.57 0 0 0 1 9.68
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 1.0f,
                    y1 = 9.68f,
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
                // m 3.47 -5.22
                moveToRelative(dx = 3.47f, dy = -5.22f)
                // A 1.16 1.16 0 0 0 3.9 5.05
                arcTo(
                    horizontalEllipseRadius = 1.16f,
                    verticalEllipseRadius = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 3.9f,
                    y1 = 5.05f,
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
                // A 1.16 1.16 0 0 0 5.05 3.9
                arcTo(
                    horizontalEllipseRadius = 1.16f,
                    verticalEllipseRadius = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 5.05f,
                    y1 = 3.9f,
                )
                // m 17.37 6.37
                moveToRelative(dx = 17.37f, dy = 6.37f)
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
                // m -8.1 -4.05
                moveToRelative(dx = -8.1f, dy = -4.05f)
                // a 1.16 1.16 0 0 0 1.15 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.15f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 0 -1.15 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.15f,
                    dy1 = -1.16f,
                )
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
                // m 0 -4.05
                moveToRelative(dx = 0.0f, dy = -4.05f)
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
                // M 1.58 13.74
                moveTo(x = 1.58f, y = 13.74f)
                // A 0.57 0.57 0 0 0 1 14.32
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 1.0f,
                    y1 = 14.32f,
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
                // m 8.1 8.1
                moveToRelative(dx = 8.1f, dy = 8.1f)
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
                // m 0 -19.68
                moveToRelative(dx = 0.0f, dy = -19.68f)
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
                // A 0.57 0.57 0 0 0 9.68 1
                arcTo(
                    horizontalEllipseRadius = 0.57f,
                    verticalEllipseRadius = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 9.68f,
                    y1 = 1.0f,
                )
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
                // a 0.57 0.57 0 0 0 0.57 0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.57f,
                    dy1 = 0.58f,
                )
                // m 0 4.05
                moveToRelative(dx = 0.0f, dy = 4.05f)
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
                // m 0 6.37
                moveToRelative(dx = 0.0f, dy = 6.37f)
                // a 1.73 1.73 0 1 0 0 3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.47f,
                )
                // a 1.73 1.73 0 0 0 0 -3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.47f,
                )
                // m 9.27 0.58
                moveToRelative(dx = 9.27f, dy = 0.58f)
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
                // a 1.16 1.16 0 0 0 1.16 1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = 1.15f,
                )
                // a 1.16 1.16 0 0 0 1.16 -1.15
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.16f,
                    dy1 = -1.15f,
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
                // m 0 4.63
                moveToRelative(dx = 0.0f, dy = 4.63f)
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
                // m 0 -9.26
                moveToRelative(dx = 0.0f, dy = -9.26f)
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
                // m 0 -4.64
                moveToRelative(dx = 0.0f, dy = -4.64f)
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
                // m 3.47 9.85
                moveToRelative(dx = 3.47f, dy = 9.85f)
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
                // m -8.1 4.05
                moveToRelative(dx = -8.1f, dy = 4.05f)
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
                // a 1.16 1.16 0 0 0 1.15 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.15f,
                    dy1 = -1.16f,
                )
                // a 1.16 1.16 0 0 0 -1.15 -1.16
                arcToRelative(
                    a = 1.16f,
                    b = 1.16f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.15f,
                    dy1 = -1.16f,
                )
                // m 0 4.05
                moveToRelative(dx = 0.0f, dy = 4.05f)
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
                // a 0.57 0.57 0 0 0 -0.57 -0.58
                arcToRelative(
                    a = 0.57f,
                    b = 0.57f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -0.57f,
                    dy1 = -0.58f,
                )
                // M 9.68 7.94
                moveTo(x = 9.68f, y = 7.94f)
                // a 1.73 1.73 0 1 0 0 3.48
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.48f,
                )
                // a 1.73 1.73 0 0 0 0 -3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.47f,
                )
                // m 0 9.85
                moveToRelative(dx = 0.0f, dy = 9.85f)
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
                // m 4.64 -5.21
                moveToRelative(dx = 4.64f, dy = -5.21f)
                // a 1.73 1.73 0 1 0 0 3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.47f,
                )
                // a 1.73 1.73 0 0 0 0 -3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.47f,
                )
                // m 0 -4.63
                moveToRelative(dx = 0.0f, dy = -4.63f)
                // a 1.73 1.73 0 1 0 0 3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.47f,
                )
                // a 1.73 1.73 0 0 0 0 -3.47
                arcToRelative(
                    a = 1.73f,
                    b = 1.73f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.47f,
                )
            }
        }.build().also { _blurLine = it }
    }

@Suppress("ObjectPropertyName")
private var _blurLine: ImageVector? = null
