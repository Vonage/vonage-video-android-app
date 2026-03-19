package com.vonage.android.screen.components.audio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.audio.AudioPlayerState
import com.vonage.android.audio.AudioPlayerViewModel
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid

@Composable
fun TestSpeaker(
    modifier: Modifier = Modifier,
    viewModel: AudioPlayerViewModel = hiltViewModel(),
) {
    val audioPlayerState by viewModel.state.collectAsStateWithLifecycle()

    var animatedWithFraction = remember { Animatable(0f) }
    val defaultButtonClipShape = VonageVideoTheme.shapes.medium
    val color = MaterialTheme.colorScheme.primary
    val density = LocalDensity.current

    when (audioPlayerState) {
        is AudioPlayerState.Playing -> {
            LaunchedEffect(Unit) {
                animatedWithFraction = Animatable(0f)
                animatedWithFraction.animateTo(
                    targetValue = 1f, // Animate to 1f (100% of width)
                    animationSpec = tween(
                        durationMillis = (audioPlayerState as AudioPlayerState.Playing).durationMs,
                        easing = LinearEasing,
                    )
                )
            }
        }

        else -> {}
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stop()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {
                    viewModel.onSpeakerTestToggle()
                })
                .border(
                    width = 1.dp,
                    color = VonageVideoTheme.colors.surface,
                    shape = VonageVideoTheme.shapes.medium,
                )
                .conditional(
                    condition = audioPlayerState is AudioPlayerState.Playing,
                    ifTrue = {
                        drawBehind {
                            val currentSize = this.size
                            val outline = defaultButtonClipShape
                                .createOutline(currentSize, layoutDirection, density)
                            val buttonShapePath = Path().apply { addOutline(outline) }

                            val progressRectWidth = currentSize.width * animatedWithFraction.value
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
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(24.dp),
                imageVector = VividIcons.Solid.AudioMid,
                tint = VonageVideoTheme.colors.secondary,
                contentDescription = null,
            )
            when (audioPlayerState) {
                is AudioPlayerState.Idle -> {
                    Text(
                        text = stringResource(R.string.test_speakers_start),
                        color = VonageVideoTheme.colors.secondary,
                        style = VonageVideoTheme.typography.bodyExtended,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                is AudioPlayerState.Playing -> {
                    Text(
                        text = stringResource(R.string.test_speakers_stop),
                        color = VonageVideoTheme.colors.secondary,
                        style = VonageVideoTheme.typography.bodyExtended,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun TestSpeakerPreview() {
    VonageVideoTheme {
        TestSpeaker()
    }
}
