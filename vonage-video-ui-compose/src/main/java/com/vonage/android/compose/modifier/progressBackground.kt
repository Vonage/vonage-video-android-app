package com.vonage.android.compose.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun Modifier.progressBackground(
    progress: Float,
    density: Density = LocalDensity.current,
    clipShape: Shape = VonageVideoTheme.shapes.medium,
    color: Color = VonageVideoTheme.colors.primary,
): Modifier = drawBehind {
    val currentSize = this.size
    val outline = clipShape
        .createOutline(currentSize, layoutDirection, density)
    val buttonShapePath = Path().apply { addOutline(outline) }

    val progressRectWidth = currentSize.width * progress
    clipPath(buttonShapePath) {
        drawRect(
            color = color,
            size = Size(
                width = progressRectWidth,
                height = currentSize.height,
            ),
        )
    }
}
