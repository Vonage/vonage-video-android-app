@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Blur: ImageVector
    get() {
        val current = _blurSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Blur",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M8.5 4 a1.5 1.5 0 1 0 0 -3 1.5 1.5 0 0 0 0 3 M11 8.5 a2.5 2.5 0 1 1 -5 0 2.5 2.5 0 0 1 5 0 m7 0 a2.5 2.5 0 1 1 -5 0 2.5 2.5 0 0 1 5 0 M8.5 18 a2.5 2.5 0 1 0 0 -5 2.5 2.5 0 0 0 0 5 m9.5 -2.5 a2.5 2.5 0 1 1 -5 0 2.5 2.5 0 0 1 5 0 M2.5 10 a1.5 1.5 0 1 0 0 -3 1.5 1.5 0 0 0 0 3 M4 15.5 a1.5 1.5 0 1 1 -3 0 1.5 1.5 0 0 1 3 0 M21.5 10 a1.5 1.5 0 1 0 0 -3 1.5 1.5 0 0 0 0 3 m1.5 5.5 a1.5 1.5 0 1 1 -3 0 1.5 1.5 0 0 1 3 0 m-6 -13 a1.5 1.5 0 1 1 -3 0 1.5 1.5 0 0 1 3 0 M8.5 23 a1.5 1.5 0 1 0 0 -3 1.5 1.5 0 0 0 0 3 m8.5 -1.5 a1.5 1.5 0 1 1 -3 0 1.5 1.5 0 0 1 3 0
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 8.5 4
                moveTo(x = 8.5f, y = 4.0f)
                // a 1.5 1.5 0 1 0 0 -3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.0f,
                )
                // a 1.5 1.5 0 0 0 0 3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.0f,
                )
                // M 11 8.5
                moveTo(x = 11.0f, y = 8.5f)
                // a 2.5 2.5 0 1 1 -5 0
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -5.0f,
                    dy1 = 0.0f,
                )
                // a 2.5 2.5 0 0 1 5 0
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 5.0f,
                    dy1 = 0.0f,
                )
                // m 7 0
                moveToRelative(dx = 7.0f, dy = 0.0f)
                // a 2.5 2.5 0 1 1 -5 0
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -5.0f,
                    dy1 = 0.0f,
                )
                // a 2.5 2.5 0 0 1 5 0
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 5.0f,
                    dy1 = 0.0f,
                )
                // M 8.5 18
                moveTo(x = 8.5f, y = 18.0f)
                // a 2.5 2.5 0 1 0 0 -5
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -5.0f,
                )
                // a 2.5 2.5 0 0 0 0 5
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 5.0f,
                )
                // m 9.5 -2.5
                moveToRelative(dx = 9.5f, dy = -2.5f)
                // a 2.5 2.5 0 1 1 -5 0
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -5.0f,
                    dy1 = 0.0f,
                )
                // a 2.5 2.5 0 0 1 5 0
                arcToRelative(
                    a = 2.5f,
                    b = 2.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 5.0f,
                    dy1 = 0.0f,
                )
                // M 2.5 10
                moveTo(x = 2.5f, y = 10.0f)
                // a 1.5 1.5 0 1 0 0 -3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.0f,
                )
                // a 1.5 1.5 0 0 0 0 3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.0f,
                )
                // M 4 15.5
                moveTo(x = 4.0f, y = 15.5f)
                // a 1.5 1.5 0 1 1 -3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 0.0f,
                )
                // a 1.5 1.5 0 0 1 3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 0.0f,
                )
                // M 21.5 10
                moveTo(x = 21.5f, y = 10.0f)
                // a 1.5 1.5 0 1 0 0 -3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.0f,
                )
                // a 1.5 1.5 0 0 0 0 3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.0f,
                )
                // m 1.5 5.5
                moveToRelative(dx = 1.5f, dy = 5.5f)
                // a 1.5 1.5 0 1 1 -3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 0.0f,
                )
                // a 1.5 1.5 0 0 1 3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 0.0f,
                )
                // m -6 -13
                moveToRelative(dx = -6.0f, dy = -13.0f)
                // a 1.5 1.5 0 1 1 -3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 0.0f,
                )
                // a 1.5 1.5 0 0 1 3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 0.0f,
                )
                // M 8.5 23
                moveTo(x = 8.5f, y = 23.0f)
                // a 1.5 1.5 0 1 0 0 -3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -3.0f,
                )
                // a 1.5 1.5 0 0 0 0 3
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 3.0f,
                )
                // m 8.5 -1.5
                moveToRelative(dx = 8.5f, dy = -1.5f)
                // a 1.5 1.5 0 1 1 -3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 0.0f,
                )
                // a 1.5 1.5 0 0 1 3 0
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 0.0f,
                )
            }
        }.build().also { _blurSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _blurSolid: ImageVector? = null
