@file:Suppress("MaxLineLength")
package com.vonage.android.compose.vivid.icons.solid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.vivid.icons.VividIcons

public val VividIcons.Solid.VoicemailTranscript: ImageVector
    get() {
        val current = _voicemailTranscriptSolid
        if (current != null) return current

        return ImageVector.Builder(
            name = "VividIcons.Solid.VoicemailTranscript",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            // M22.5 0 h-21 A1.42 1.42 0 0 0 0 1.5 V18 a1.42 1.42 0 0 0 1.5 1.5 h6 L12 24 l4.5 -4.5 h6 A1.42 1.42 0 0 0 24 18 V1.5 A1.42 1.42 0 0 0 22.5 0 m-15 13.5 h-3 V6 h3z m6 1.5 h-3 V4.5 h3z m6 -3 h-3 V7.5 h3z
            path(
                fill = SolidColor(Color.Black),
            ) {
                // M 22.5 0
                moveTo(x = 22.5f, y = 0.0f)
                // h -21
                horizontalLineToRelative(dx = -21.0f)
                // A 1.42 1.42 0 0 0 0 1.5
                arcTo(
                    horizontalEllipseRadius = 1.42f,
                    verticalEllipseRadius = 1.42f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 0.0f,
                    y1 = 1.5f,
                )
                // V 18
                verticalLineTo(y = 18.0f)
                // a 1.42 1.42 0 0 0 1.5 1.5
                arcToRelative(
                    a = 1.42f,
                    b = 1.42f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    dx1 = 1.5f,
                    dy1 = 1.5f,
                )
                // h 6
                horizontalLineToRelative(dx = 6.0f)
                // L 12 24
                lineTo(x = 12.0f, y = 24.0f)
                // l 4.5 -4.5
                lineToRelative(dx = 4.5f, dy = -4.5f)
                // h 6
                horizontalLineToRelative(dx = 6.0f)
                // A 1.42 1.42 0 0 0 24 18
                arcTo(
                    horizontalEllipseRadius = 1.42f,
                    verticalEllipseRadius = 1.42f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 24.0f,
                    y1 = 18.0f,
                )
                // V 1.5
                verticalLineTo(y = 1.5f)
                // A 1.42 1.42 0 0 0 22.5 0
                arcTo(
                    horizontalEllipseRadius = 1.42f,
                    verticalEllipseRadius = 1.42f,
                    theta = 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 22.5f,
                    y1 = 0.0f,
                )
                // m -15 13.5
                moveToRelative(dx = -15.0f, dy = 13.5f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // V 6
                verticalLineTo(y = 6.0f)
                // h 3z
                horizontalLineToRelative(dx = 3.0f)
                close()
                // m 6 1.5
                moveToRelative(dx = 6.0f, dy = 1.5f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // V 4.5
                verticalLineTo(y = 4.5f)
                // h 3z
                horizontalLineToRelative(dx = 3.0f)
                close()
                // m 6 -3
                moveToRelative(dx = 6.0f, dy = -3.0f)
                // h -3
                horizontalLineToRelative(dx = -3.0f)
                // V 7.5
                verticalLineTo(y = 7.5f)
                // h 3z
                horizontalLineToRelative(dx = 3.0f)
                close()
            }
        }.build().also { _voicemailTranscriptSolid = it }
    }

@Suppress("ObjectPropertyName")
private var _voicemailTranscriptSolid: ImageVector? = null
