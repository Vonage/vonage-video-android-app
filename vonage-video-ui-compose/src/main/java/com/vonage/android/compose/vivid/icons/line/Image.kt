@file:Suppress("MaxLineLength", "MagicNumber")
package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.Image: ImageVector
    get() {
        val current = _imageLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.Image",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            group(
                // M 0 0 h 24 v 24 h -24z
                clipPathData = PathData {
                    // M 0 0
                    moveTo(x = 0.0f, y = 0.0f)
                    // h 24
                    horizontalLineToRelative(dx = 24.0f)
                    // v 24
                    verticalLineToRelative(dy = 24.0f)
                    // h -24z
                    horizontalLineToRelative(dx = -24.0f)
                    close()
                },
            ) {
                // M12 6.75 a2.25 2.25 0 1 1 -4.5 0 2.25 2.25 0 0 1 4.5 0
                path(
                    fill = SolidColor(Color.Black),
                ) {
                    // M 12 6.75
                    moveTo(x = 12.0f, y = 6.75f)
                    // a 2.25 2.25 0 1 1 -4.5 0
                    arcToRelative(
                        a = 2.25f,
                        b = 2.25f,
                        theta = 0.0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = -4.5f,
                        dy1 = 0.0f,
                    )
                    // a 2.25 2.25 0 0 1 4.5 0
                    arcToRelative(
                        a = 2.25f,
                        b = 2.25f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 4.5f,
                        dy1 = 0.0f,
                    )
                }
                // M0 21.75 C0 22.99 1 24 2.25 24 h19.5 c1.24 0 2.25 -1 2.25 -2.25 V2.25 C24 1.01 23 0 21.75 0 H2.25 C1.01 0 0 1 0 2.25z M2.25 1.5 A.75 .75 0 0 0 1.5 2.25 v14.69 l4.72 -4.72 a.75 .75 0 0 1 1.06 0 l3.9 3.9 5.48 -6.84 a.75 .75 0 0 1 1.12 -.06 l4.72 4.72 V2.25 a.75 .75 0 0 0 -.75 -.75z M22.5 21.75 v-5.69 l-5.19 -5.19 -5.47 6.85 a.75 .75 0 0 1 -1.12 .06 l-3.97 -3.97 -5.25 5.25 v2.69 a.75 .75 0 0 0 .75 .75 h19.5 a.75 .75 0 0 0 .75 -.75
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    pathFillType = PathFillType.EvenOdd,
                ) {
                    // M 0 21.75
                    moveTo(x = 0.0f, y = 21.75f)
                    // C 0 22.99 1 24 2.25 24
                    curveTo(
                        x1 = 0.0f,
                        y1 = 22.99f,
                        x2 = 1.0f,
                        y2 = 24.0f,
                        x3 = 2.25f,
                        y3 = 24.0f,
                    )
                    // h 19.5
                    horizontalLineToRelative(dx = 19.5f)
                    // c 1.24 0 2.25 -1 2.25 -2.25
                    curveToRelative(
                        dx1 = 1.24f,
                        dy1 = 0.0f,
                        dx2 = 2.25f,
                        dy2 = -1.0f,
                        dx3 = 2.25f,
                        dy3 = -2.25f,
                    )
                    // V 2.25
                    verticalLineTo(y = 2.25f)
                    // C 24 1.01 23 0 21.75 0
                    curveTo(
                        x1 = 24.0f,
                        y1 = 1.01f,
                        x2 = 23.0f,
                        y2 = 0.0f,
                        x3 = 21.75f,
                        y3 = 0.0f,
                    )
                    // H 2.25
                    horizontalLineTo(x = 2.25f)
                    // C 1.01 0 0 1 0 2.25z
                    curveTo(
                        x1 = 1.01f,
                        y1 = 0.0f,
                        x2 = 0.0f,
                        y2 = 1.0f,
                        x3 = 0.0f,
                        y3 = 2.25f,
                    )
                    close()
                    // M 2.25 1.5
                    moveTo(x = 2.25f, y = 1.5f)
                    // A 0.75 0.75 0 0 0 1.5 2.25
                    arcTo(
                        horizontalEllipseRadius = 0.75f,
                        verticalEllipseRadius = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        x1 = 1.5f,
                        y1 = 2.25f,
                    )
                    // v 14.69
                    verticalLineToRelative(dy = 14.69f)
                    // l 4.72 -4.72
                    lineToRelative(dx = 4.72f, dy = -4.72f)
                    // a 0.75 0.75 0 0 1 1.06 0
                    arcToRelative(
                        a = 0.75f,
                        b = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.06f,
                        dy1 = 0.0f,
                    )
                    // l 3.9 3.9
                    lineToRelative(dx = 3.9f, dy = 3.9f)
                    // l 5.48 -6.84
                    lineToRelative(dx = 5.48f, dy = -6.84f)
                    // a 0.75 0.75 0 0 1 1.12 -0.06
                    arcToRelative(
                        a = 0.75f,
                        b = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.12f,
                        dy1 = -0.06f,
                    )
                    // l 4.72 4.72
                    lineToRelative(dx = 4.72f, dy = 4.72f)
                    // V 2.25
                    verticalLineTo(y = 2.25f)
                    // a 0.75 0.75 0 0 0 -0.75 -0.75z
                    arcToRelative(
                        a = 0.75f,
                        b = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.75f,
                        dy1 = -0.75f,
                    )
                    close()
                    // M 22.5 21.75
                    moveTo(x = 22.5f, y = 21.75f)
                    // v -5.69
                    verticalLineToRelative(dy = -5.69f)
                    // l -5.19 -5.19
                    lineToRelative(dx = -5.19f, dy = -5.19f)
                    // l -5.47 6.85
                    lineToRelative(dx = -5.47f, dy = 6.85f)
                    // a 0.75 0.75 0 0 1 -1.12 0.06
                    arcToRelative(
                        a = 0.75f,
                        b = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.12f,
                        dy1 = 0.06f,
                    )
                    // l -3.97 -3.97
                    lineToRelative(dx = -3.97f, dy = -3.97f)
                    // l -5.25 5.25
                    lineToRelative(dx = -5.25f, dy = 5.25f)
                    // v 2.69
                    verticalLineToRelative(dy = 2.69f)
                    // a 0.75 0.75 0 0 0 0.75 0.75
                    arcToRelative(
                        a = 0.75f,
                        b = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.75f,
                        dy1 = 0.75f,
                    )
                    // h 19.5
                    horizontalLineToRelative(dx = 19.5f)
                    // a 0.75 0.75 0 0 0 0.75 -0.75
                    arcToRelative(
                        a = 0.75f,
                        b = 0.75f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = 0.75f,
                        dy1 = -0.75f,
                    )
                }
            }
        }.build().also { _imageLine = it }
    }

@Suppress("ObjectPropertyName")
private var _imageLine: ImageVector? = null
