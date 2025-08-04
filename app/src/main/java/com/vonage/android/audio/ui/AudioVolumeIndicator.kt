package com.vonage.android.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun AudioVolumeIndicator(
    audioLevels: Float,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    val defaultBarHeight = 0.1f
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .background(VonageVideoTheme.colors.primary, CircleShape)
            .size(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        repeat(3) { index ->
            val audioLevel =
                when (index) {
                    0, 2 -> {
                        audioLevels * 0.6f
                    }

                    else -> {
                        audioLevels * 0.9f
                    }
                }
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(
                        if (audioLevel == 0f) {
                            defaultBarHeight
                        } else {
                            (audioLevel + defaultBarHeight).coerceAtMost(1f)
                        },
                    )
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp),
                    ),
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
            AudioVolumeIndicator(audioLevels = 0f)
            AudioVolumeIndicator(audioLevels = 0.8f)
        }
    }
}
