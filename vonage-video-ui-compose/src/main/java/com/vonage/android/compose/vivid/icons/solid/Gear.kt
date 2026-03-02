package com.vonage.android.compose.vivid.icons.solid

import android.graphics.Color.BLACK
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.Gear: ImageVector
    get() {
        val current = _gearSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.Gear",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // m19.95 7.8 1.65 -3.15 -2.1 -2.1 -3.15 1.65 a5 5 0 0 0 -1.65 -.6 L13.5 0 h-3 L9.3 3.45 c-.45 .15 -1.05 .3 -1.5 .6 L4.65 2.4 2.4 4.65 4.05 7.8 c-.3 .45 -.45 1.05 -.6 1.5 L0 10.5 v3 l3.45 1.2 c.15 .6 .45 1.05 .6 1.65 L2.4 19.5 l2.1 2.1 3.15 -1.65 c.45 .3 1.05 .45 1.65 .6 L10.5 24 h3 l1.2 -3.45 c.6 -.15 1.05 -.45 1.65 -.6 l3.15 1.65 2.1 -2.1 -1.65 -3.15 c.3 -.45 .45 -1.05 .6 -1.65 L24 13.5 v-3 l-3.45 -1.2 c-.15 -.45 -.3 -1.05 -.6 -1.5 M12 16.5 A4.4 4.4 0 0 1 7.5 12 c0 -2.55 1.95 -4.5 4.5 -4.5 s4.5 1.95 4.5 4.5 -1.95 4.5 -4.5 4.5
            path(
                fill = SolidColor(Color(BLACK)),
            ) {
                // M 19.95 7.8
                moveTo(x = 19.95f, y = 7.8f)
                // l 1.65 -3.15
                lineToRelative(dx = 1.65f, dy = -3.15f)
                // l -2.1 -2.1
                lineToRelative(dx = -2.1f, dy = -2.1f)
                // l -3.15 1.65
                lineToRelative(dx = -3.15f, dy = 1.65f)
                // a 5 5 0 0 0 -1.65 -0.6
                arcToRelative(
                    a = 5.0f,
                    b = 5.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.65f,
                    dy1 = -0.6f,
                )
                // L 13.5 0
                lineTo(x = 13.5f, y = 0.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // L 9.3 3.45
                lineTo(x = 9.3f, y = 3.45f)
                // c -0.45 0.15 -1.05 0.3 -1.5 0.6
                curveToRelative(
                    dx1 = -0.45f,
                    dy1 = 0.15f,
                    dx2 = -1.05f,
                    dy2 = 0.3f,
                    dx3 = -1.5f,
                    dy3 = 0.6f,
                )
                // L 4.65 2.4
                lineTo(x = 4.65f, y = 2.4f)
                // L 2.4 4.65
                lineTo(x = 2.4f, y = 4.65f)
                // L 4.05 7.8
                lineTo(x = 4.05f, y = 7.8f)
                // c -0.3 0.45 -0.45 1.05 -0.6 1.5
                curveToRelative(
                    dx1 = -0.3f,
                    dy1 = 0.45f,
                    dx2 = -0.45f,
                    dy2 = 1.05f,
                    dx3 = -0.6f,
                    dy3 = 1.5f,
                )
                // L 0 10.5
                lineTo(x = 0.0f, y = 10.5f)
                // v 3
                verticalLineToRelative(dy = 3.0f)
                // l 3.45 1.2
                lineToRelative(dx = 3.45f, dy = 1.2f)
                // c 0.15 0.6 0.45 1.05 0.6 1.65
                curveToRelative(
                    dx1 = 0.15f,
                    dy1 = 0.6f,
                    dx2 = 0.45f,
                    dy2 = 1.05f,
                    dx3 = 0.6f,
                    dy3 = 1.65f,
                )
                // L 2.4 19.5
                lineTo(x = 2.4f, y = 19.5f)
                // l 2.1 2.1
                lineToRelative(dx = 2.1f, dy = 2.1f)
                // l 3.15 -1.65
                lineToRelative(dx = 3.15f, dy = -1.65f)
                // c 0.45 0.3 1.05 0.45 1.65 0.6
                curveToRelative(
                    dx1 = 0.45f,
                    dy1 = 0.3f,
                    dx2 = 1.05f,
                    dy2 = 0.45f,
                    dx3 = 1.65f,
                    dy3 = 0.6f,
                )
                // L 10.5 24
                lineTo(x = 10.5f, y = 24.0f)
                // h 3
                horizontalLineToRelative(dx = 3.0f)
                // l 1.2 -3.45
                lineToRelative(dx = 1.2f, dy = -3.45f)
                // c 0.6 -0.15 1.05 -0.45 1.65 -0.6
                curveToRelative(
                    dx1 = 0.6f,
                    dy1 = -0.15f,
                    dx2 = 1.05f,
                    dy2 = -0.45f,
                    dx3 = 1.65f,
                    dy3 = -0.6f,
                )
                // l 3.15 1.65
                lineToRelative(dx = 3.15f, dy = 1.65f)
                // l 2.1 -2.1
                lineToRelative(dx = 2.1f, dy = -2.1f)
                // l -1.65 -3.15
                lineToRelative(dx = -1.65f, dy = -3.15f)
                // c 0.3 -0.45 0.45 -1.05 0.6 -1.65
                curveToRelative(
                    dx1 = 0.3f,
                    dy1 = -0.45f,
                    dx2 = 0.45f,
                    dy2 = -1.05f,
                    dx3 = 0.6f,
                    dy3 = -1.65f,
                )
                // L 24 13.5
                lineTo(x = 24.0f, y = 13.5f)
                // v -3
                verticalLineToRelative(dy = -3.0f)
                // l -3.45 -1.2
                lineToRelative(dx = -3.45f, dy = -1.2f)
                // c -0.15 -0.45 -0.3 -1.05 -0.6 -1.5
                curveToRelative(
                    dx1 = -0.15f,
                    dy1 = -0.45f,
                    dx2 = -0.3f,
                    dy2 = -1.05f,
                    dx3 = -0.6f,
                    dy3 = -1.5f,
                )
                // M 12 16.5
                moveTo(x = 12.0f, y = 16.5f)
                // A 4.4 4.4 0 0 1 7.5 12
                arcTo(
                    horizontalEllipseRadius = 4.4f,
                    verticalEllipseRadius = 4.4f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 7.5f,
                    y1 = 12.0f,
                )
                // c 0 -2.55 1.95 -4.5 4.5 -4.5
                curveToRelative(
                    dx1 = 0.0f,
                    dy1 = -2.55f,
                    dx2 = 1.95f,
                    dy2 = -4.5f,
                    dx3 = 4.5f,
                    dy3 = -4.5f,
                )
                // s 4.5 1.95 4.5 4.5
                reflectiveCurveToRelative(
                    dx1 = 4.5f,
                    dy1 = 1.95f,
                    dx2 = 4.5f,
                    dy2 = 4.5f,
                )
                // s -1.95 4.5 -4.5 4.5
                reflectiveCurveToRelative(
                    dx1 = -1.95f,
                    dy1 = 4.5f,
                    dx2 = -4.5f,
                    dy2 = 4.5f,
                )
            }
        }.build().also { _gearSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _gearSolid: ImageVector? = null
