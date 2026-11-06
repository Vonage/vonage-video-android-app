@file:Suppress("MaxLineLength")

package com.vonage.android.compose.vivid.icons.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Line.VoicemailTranscript: ImageVector
    get() {
        val current = _voicemailTranscriptLine
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Line.VoicemailTranscript",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M5.25 7.5 A.75 .75 0 0 1 6 8.25 v4.5 a.75 .75 0 0 1 -1.5 0 v-4.5 A.75 .75 0 0 1 5.25 7.5 m5.25 -.75 a.75 .75 0 0 0 -1.5 0 v7.5 a.75 .75 0 0 0 1.5 0z M14.25 9 A.75 .75 0 0 1 15 9.75 v1.5 a.75 .75 0 0 1 -1.5 0 v-1.5 A.75 .75 0 0 1 14.25 9 m5.25 -.75 a.75 .75 0 0 0 -1.5 0 v4.5 a.75 .75 0 0 0 1.5 0z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 5.25 7.5
                moveTo(x = 5.25f, y = 7.5f)
                // A 0.75 0.75 0 0 1 6 8.25
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 6.0f,
                    y1 = 8.25f,
                )
                // v 4.5
                verticalLineToRelative(dy = 4.5f)
                // a 0.75 0.75 0 0 1 -1.5 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.5f,
                    dy1 = 0.0f,
                )
                // v -4.5
                verticalLineToRelative(dy = -4.5f)
                // A 0.75 0.75 0 0 1 5.25 7.5
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 5.25f,
                    y1 = 7.5f,
                )
                // m 5.25 -0.75
                moveToRelative(dx = 5.25f, dy = -0.75f)
                // a 0.75 0.75 0 0 0 -1.5 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.5f,
                    dy1 = 0.0f,
                )
                // v 7.5
                verticalLineToRelative(dy = 7.5f)
                // a 0.75 0.75 0 0 0 1.5 0z
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.5f,
                    dy1 = 0.0f,
                )
                close()
                // M 14.25 9
                moveTo(x = 14.25f, y = 9.0f)
                // A 0.75 0.75 0 0 1 15 9.75
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 15.0f,
                    y1 = 9.75f,
                )
                // v 1.5
                verticalLineToRelative(dy = 1.5f)
                // a 0.75 0.75 0 0 1 -1.5 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.5f,
                    dy1 = 0.0f,
                )
                // v -1.5
                verticalLineToRelative(dy = -1.5f)
                // A 0.75 0.75 0 0 1 14.25 9
                arcTo(
                    horizontalEllipseRadius = 0.75f,
                    verticalEllipseRadius = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 14.25f,
                    y1 = 9.0f,
                )
                // m 5.25 -0.75
                moveToRelative(dx = 5.25f, dy = -0.75f)
                // a 0.75 0.75 0 0 0 -1.5 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.5f,
                    dy1 = 0.0f,
                )
                // v 4.5
                verticalLineToRelative(dy = 4.5f)
                // a 0.75 0.75 0 0 0 1.5 0z
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.5f,
                    dy1 = 0.0f,
                )
                close()
            }
            // M.88 2.38 A3 3 0 0 1 3 1.5 h18 a3 3 0 0 1 3 3 v12 a3 3 0 0 1 -3 3 h-4.9 l-3.52 4.23 a.75 .75 0 0 1 -1.16 0 L7.9 19.5 H3 a3 3 0 0 1 -3 -3 v-12 a3 3 0 0 1 .88 -2.12 M3 3 a1.5 1.5 0 0 0 -1.5 1.5 v12 A1.5 1.5 0 0 0 3 18 h5.25 q.34 0 .58 .27 l3.17 3.8 3.17 -3.8 A.8 .8 0 0 1 15.75 18 H21 a1.5 1.5 0 0 0 1.5 -1.5 v-12 A1.5 1.5 0 0 0 21 3z
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                // M 0.88 2.38
                moveTo(x = 0.88f, y = 2.38f)
                // A 3 3 0 0 1 3 1.5
                arcTo(
                    horizontalEllipseRadius = 3.0f,
                    verticalEllipseRadius = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 3.0f,
                    y1 = 1.5f,
                )
                // h 18
                horizontalLineToRelative(dx = 18.0f)
                // a 3 3 0 0 1 3 3
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 3.0f,
                    dy1 = 3.0f,
                )
                // v 12
                verticalLineToRelative(dy = 12.0f)
                // a 3 3 0 0 1 -3 3
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = 3.0f,
                )
                // h -4.9
                horizontalLineToRelative(dx = -4.9f)
                // l -3.52 4.23
                lineToRelative(dx = -3.52f, dy = 4.23f)
                // a 0.75 0.75 0 0 1 -1.16 0
                arcToRelative(
                    a = 0.75f,
                    b = 0.75f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -1.16f,
                    dy1 = 0.0f,
                )
                // L 7.9 19.5
                lineTo(x = 7.9f, y = 19.5f)
                // H 3
                horizontalLineTo(x = 3.0f)
                // a 3 3 0 0 1 -3 -3
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = -3.0f,
                    dy1 = -3.0f,
                )
                // v -12
                verticalLineToRelative(dy = -12.0f)
                // a 3 3 0 0 1 0.88 -2.12
                arcToRelative(
                    a = 3.0f,
                    b = 3.0f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    dx1 = 0.88f,
                    dy1 = -2.12f,
                )
                // M 3 3
                moveTo(x = 3.0f, y = 3.0f)
                // a 1.5 1.5 0 0 0 -1.5 1.5
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = -1.5f,
                    dy1 = 1.5f,
                )
                // v 12
                verticalLineToRelative(dy = 12.0f)
                // A 1.5 1.5 0 0 0 3 18
                arcTo(
                    horizontalEllipseRadius = 1.5f,
                    verticalEllipseRadius = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 3.0f,
                    y1 = 18.0f,
                )
                // h 5.25
                horizontalLineToRelative(dx = 5.25f)
                // q 0.34 0 0.58 0.27
                quadToRelative(
                    dx1 = 0.34f,
                    dy1 = 0.0f,
                    dx2 = 0.58f,
                    dy2 = 0.27f,
                )
                // l 3.17 3.8
                lineToRelative(dx = 3.17f, dy = 3.8f)
                // l 3.17 -3.8
                lineToRelative(dx = 3.17f, dy = -3.8f)
                // A 0.8 0.8 0 0 1 15.75 18
                arcTo(
                    horizontalEllipseRadius = 0.8f,
                    verticalEllipseRadius = 0.8f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 15.75f,
                    y1 = 18.0f,
                )
                // H 21
                horizontalLineTo(x = 21.0f)
                // a 1.5 1.5 0 0 0 1.5 -1.5
                arcToRelative(
                    a = 1.5f,
                    b = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.5f,
                    dy1 = -1.5f,
                )
                // v -12
                verticalLineToRelative(dy = -12.0f)
                // A 1.5 1.5 0 0 0 21 3z
                arcTo(
                    horizontalEllipseRadius = 1.5f,
                    verticalEllipseRadius = 1.5f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 21.0f,
                    y1 = 3.0f,
                )
                close()
            }
        }.build().also { _voicemailTranscriptLine = it }
    }

@Suppress("ObjectPropertyName")
private var _voicemailTranscriptLine: ImageVector? = null
