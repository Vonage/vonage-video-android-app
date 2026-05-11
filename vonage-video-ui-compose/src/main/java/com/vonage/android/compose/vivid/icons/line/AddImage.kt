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

public val VividIcons.Line.AddImage: ImageVector
    get() {
        val current = _addImageLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.AddImage",
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
                // M15 3.51 c0 .37 -.3 .66 -.66 .66 H1.98 a.66 .66 0 0 0 -.66 .66 v12.95 l4.16 -4.16 a.66 .66 0 0 1 .94 0 l3.44 3.44 4.83 -6.03 a.66 .66 0 0 1 .98 -.06 l4.16 4.16 V9.66 a.66 .66 0 0 1 1.32 0 v12.36 c0 1.1 -.89 1.98 -1.98 1.98 H1.98 A2 2 0 0 1 0 22.02 V4.83 c0 -1.1 .89 -1.98 1.98 -1.98 h12.36 c.36 0 .66 .3 .66 .66 m-4.57 14.95 a.66 .66 0 0 1 -.98 .06 l-3.5 -3.5 -4.63 4.63 v2.37 c0 .36 .3 .66 .66 .66 h17.19 c.36 0 .66 -.3 .66 -.66 V17 l-4.57 -4.57z
                path(
                    fill = SolidColor(Color.Black),
                    pathFillType = PathFillType.EvenOdd,
                ) {
                    // M 15 3.51
                    moveTo(x = 15.0f, y = 3.51f)
                    // c 0 0.37 -0.3 0.66 -0.66 0.66
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.37f,
                        dx2 = -0.3f,
                        dy2 = 0.66f,
                        dx3 = -0.66f,
                        dy3 = 0.66f,
                    )
                    // H 1.98
                    horizontalLineTo(x = 1.98f)
                    // a 0.66 0.66 0 0 0 -0.66 0.66
                    arcToRelative(
                        a = 0.66f,
                        b = 0.66f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        dx1 = -0.66f,
                        dy1 = 0.66f,
                    )
                    // v 12.95
                    verticalLineToRelative(dy = 12.95f)
                    // l 4.16 -4.16
                    lineToRelative(dx = 4.16f, dy = -4.16f)
                    // a 0.66 0.66 0 0 1 0.94 0
                    arcToRelative(
                        a = 0.66f,
                        b = 0.66f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.94f,
                        dy1 = 0.0f,
                    )
                    // l 3.44 3.44
                    lineToRelative(dx = 3.44f, dy = 3.44f)
                    // l 4.83 -6.03
                    lineToRelative(dx = 4.83f, dy = -6.03f)
                    // a 0.66 0.66 0 0 1 0.98 -0.06
                    arcToRelative(
                        a = 0.66f,
                        b = 0.66f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.98f,
                        dy1 = -0.06f,
                    )
                    // l 4.16 4.16
                    lineToRelative(dx = 4.16f, dy = 4.16f)
                    // V 9.66
                    verticalLineTo(y = 9.66f)
                    // a 0.66 0.66 0 0 1 1.32 0
                    arcToRelative(
                        a = 0.66f,
                        b = 0.66f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 1.32f,
                        dy1 = 0.0f,
                    )
                    // v 12.36
                    verticalLineToRelative(dy = 12.36f)
                    // c 0 1.1 -0.89 1.98 -1.98 1.98
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 1.1f,
                        dx2 = -0.89f,
                        dy2 = 1.98f,
                        dx3 = -1.98f,
                        dy3 = 1.98f,
                    )
                    // H 1.98
                    horizontalLineTo(x = 1.98f)
                    // A 2 2 0 0 1 0 22.02
                    arcTo(
                        horizontalEllipseRadius = 2.0f,
                        verticalEllipseRadius = 2.0f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 0.0f,
                        y1 = 22.02f,
                    )
                    // V 4.83
                    verticalLineTo(y = 4.83f)
                    // c 0 -1.1 0.89 -1.98 1.98 -1.98
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = -1.1f,
                        dx2 = 0.89f,
                        dy2 = -1.98f,
                        dx3 = 1.98f,
                        dy3 = -1.98f,
                    )
                    // h 12.36
                    horizontalLineToRelative(dx = 12.36f)
                    // c 0.36 0 0.66 0.3 0.66 0.66
                    curveToRelative(
                        dx1 = 0.36f,
                        dy1 = 0.0f,
                        dx2 = 0.66f,
                        dy2 = 0.3f,
                        dx3 = 0.66f,
                        dy3 = 0.66f,
                    )
                    // m -4.57 14.95
                    moveToRelative(dx = -4.57f, dy = 14.95f)
                    // a 0.66 0.66 0 0 1 -0.98 0.06
                    arcToRelative(
                        a = 0.66f,
                        b = 0.66f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -0.98f,
                        dy1 = 0.06f,
                    )
                    // l -3.5 -3.5
                    lineToRelative(dx = -3.5f, dy = -3.5f)
                    // l -4.63 4.63
                    lineToRelative(dx = -4.63f, dy = 4.63f)
                    // v 2.37
                    verticalLineToRelative(dy = 2.37f)
                    // c 0 0.36 0.3 0.66 0.66 0.66
                    curveToRelative(
                        dx1 = 0.0f,
                        dy1 = 0.36f,
                        dx2 = 0.3f,
                        dy2 = 0.66f,
                        dx3 = 0.66f,
                        dy3 = 0.66f,
                    )
                    // h 17.19
                    horizontalLineToRelative(dx = 17.19f)
                    // c 0.36 0 0.66 -0.3 0.66 -0.66
                    curveToRelative(
                        dx1 = 0.36f,
                        dy1 = 0.0f,
                        dx2 = 0.66f,
                        dy2 = -0.3f,
                        dx3 = 0.66f,
                        dy3 = -0.66f,
                    )
                    // V 17
                    verticalLineTo(y = 17.0f)
                    // l -4.57 -4.57z
                    lineToRelative(dx = -4.57f, dy = -4.57f)
                    close()
                }
                // M8.6 6.82 a1.98 1.98 0 1 1 0 3.96 1.98 1.98 0 0 1 0 -3.96 M20.5 0 a.65 .65 0 0 1 .65 .65 v2.2 h2.2 a.65 .65 0 0 1 0 1.3 h-2.2 v2.2 a.65 .65 0 0 1 -1.3 0 v-2.2 h-2.2 a.65 .65 0 0 1 0 -1.3 h2.2 v-2.2 A.65 .65 0 0 1 20.5 0
                path(
                    fill = SolidColor(Color(0xFF000000)),
                ) {
                    // M 8.6 6.82
                    moveTo(x = 8.6f, y = 6.82f)
                    // a 1.98 1.98 0 1 1 0 3.96
                    arcToRelative(
                        a = 1.98f,
                        b = 1.98f,
                        theta = 0.0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        dx1 = 0.0f,
                        dy1 = 3.96f,
                    )
                    // a 1.98 1.98 0 0 1 0 -3.96
                    arcToRelative(
                        a = 1.98f,
                        b = 1.98f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.0f,
                        dy1 = -3.96f,
                    )
                    // M 20.5 0
                    moveTo(x = 20.5f, y = 0.0f)
                    // a 0.65 0.65 0 0 1 0.65 0.65
                    arcToRelative(
                        a = 0.65f,
                        b = 0.65f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.65f,
                        dy1 = 0.65f,
                    )
                    // v 2.2
                    verticalLineToRelative(dy = 2.2f)
                    // h 2.2
                    horizontalLineToRelative(dx = 2.2f)
                    // a 0.65 0.65 0 0 1 0 1.3
                    arcToRelative(
                        a = 0.65f,
                        b = 0.65f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.0f,
                        dy1 = 1.3f,
                    )
                    // h -2.2
                    horizontalLineToRelative(dx = -2.2f)
                    // v 2.2
                    verticalLineToRelative(dy = 2.2f)
                    // a 0.65 0.65 0 0 1 -1.3 0
                    arcToRelative(
                        a = 0.65f,
                        b = 0.65f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = -1.3f,
                        dy1 = 0.0f,
                    )
                    // v -2.2
                    verticalLineToRelative(dy = -2.2f)
                    // h -2.2
                    horizontalLineToRelative(dx = -2.2f)
                    // a 0.65 0.65 0 0 1 0 -1.3
                    arcToRelative(
                        a = 0.65f,
                        b = 0.65f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        dx1 = 0.0f,
                        dy1 = -1.3f,
                    )
                    // h 2.2
                    horizontalLineToRelative(dx = 2.2f)
                    // v -2.2
                    verticalLineToRelative(dy = -2.2f)
                    // A 0.65 0.65 0 0 1 20.5 0
                    arcTo(
                        horizontalEllipseRadius = 0.65f,
                        verticalEllipseRadius = 0.65f,
                        theta = 0.0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        x1 = 20.5f,
                        y1 = 0.0f,
                    )
                }
            }
        }.build().also { _addImageLine = it }
    }

@Suppress("ObjectPropertyName")
private var _addImageLine: ImageVector? = null
