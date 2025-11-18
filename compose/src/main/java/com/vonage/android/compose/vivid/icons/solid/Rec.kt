package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Rec: ImageVector
    get() {
        val current = _recSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Rec",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M12 6 a6 6 0 1 0 0 12 6 6 0 0 0 0 -12
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 12 6
                moveTo(x = 12.0f, y = 6.0f)
                // a 6 6 0 1 0 0 12
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = 12.0f,
                )
                // a 6 6 0 0 0 0 -12
                arcToRelative(
                    a = 6.0f,
                    b = 6.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 0.0f,
                    dy1 = -12.0f,
                )
            }
            // M24 12 a12 12 0 1 1 -24 0 12 12 0 0 1 24 0 m-2 0 a10 10 0 1 1 -20 0 10 10 0 0 1 20 0
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 24 12
                moveTo(x = 24.0f, y = 12.0f)
                // a 12 12 0 1 1 -24 0
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -24.0f,
                    dy1 = 0.0f,
                )
                // a 12 12 0 0 1 24 0
                arcToRelative(
                    a = 12.0f,
                    b = 12.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 24.0f,
                    dy1 = 0.0f,
                )
                // m -2 0
                moveToRelative(dx = -2.0f, dy = 0.0f)
                // a 10 10 0 1 1 -20 0
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -20.0f,
                    dy1 = 0.0f,
                )
                // a 10 10 0 0 1 20 0
                arcToRelative(
                    a = 10.0f,
                    b = 10.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 20.0f,
                    dy1 = 0.0f,
                )
            }
        }.build().also { _recSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _recSolid: ImageVector? = null
