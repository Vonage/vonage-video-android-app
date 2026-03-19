package com.vonage.android.screen.components.audio

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.vonage.android.compose.modifier.progressBackground
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid

@Composable
fun TestSpeaker(
    viewModel: AudioPlayerViewModel = hiltViewModel(),
) {
    val audioPlayerState by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stop()
        }
    }

    TestSpeakerContent(
        audioPlayerState = audioPlayerState,
        onSpeakerTestToggle = { viewModel.onSpeakerTestToggle() },
    )
}

@Composable
private fun TestSpeakerContent(
    audioPlayerState: AudioPlayerState,
    onSpeakerTestToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedWithFraction = remember { Animatable(0f) }

    LaunchedEffect(audioPlayerState) {
        when (audioPlayerState) {
            is AudioPlayerState.Idle -> {
                animatedWithFraction.snapTo(0F)
            }

            is AudioPlayerState.Playing -> {
                animatedWithFraction.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = audioPlayerState.durationMs,
                        easing = LinearEasing,
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingSmall,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onSpeakerTestToggle)
                .border(
                    width = 1.dp,
                    color = VonageVideoTheme.colors.surface,
                    shape = VonageVideoTheme.shapes.medium,
                )
                .conditional(
                    condition = audioPlayerState is AudioPlayerState.Playing,
                    ifTrue = {
                        progressBackground(
                            progress = animatedWithFraction.value,
                        )
                    },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall)
        ) {
            Icon(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(VonageVideoTheme.dimens.iconSizeDefault),
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
        Column(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
        ) {
            TestSpeakerContent(
                audioPlayerState = AudioPlayerState.Idle,
                onSpeakerTestToggle = {},
            )
            TestSpeakerContent(
                audioPlayerState = AudioPlayerState.Playing(
                    durationMs = 100
                ),
                onSpeakerTestToggle = {},
            )
        }
    }
}
