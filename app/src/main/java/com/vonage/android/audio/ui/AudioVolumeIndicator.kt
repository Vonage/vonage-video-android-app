package com.vonage.android.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

@Suppress("MagicNumber")
@Composable
fun AudioVolumeIndicator(
    audioLevel: Float,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = Color.White,
) {
    val bars by remember(audioLevel) {
        derivedStateOf {
            arrayOf(
                audioLevel * 0.6f,
                audioLevel * 0.85f,
                audioLevel * 0.6f,
            )
        }
    }
    Row(
        modifier = modifier
            .background(VonageVideoTheme.colors.primary, CircleShape)
            .size(size),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        bars.forEach { audioLevel ->
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(audioLevel.coerceIn(0.12f, 0.9f))
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .shadow(elevation = 2.dp, shape = CircleShape),
            )
        }
    }
}

@Preview
@Composable
internal fun ActiveSoundLevelsPreview() {
    VonageVideoTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AudioVolumeIndicator(audioLevel = 0f)
            AudioVolumeIndicator(audioLevel = 1f)
        }
    }
}
