package com.vonage.android.reactions.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.reactions.EMOJI_LIFETIME_MILLIS
import com.vonage.android.reactions.R
import com.vonage.android.kotlin.model.EmojiReaction
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.launch

private const val ANIMATION_DURATION = EMOJI_LIFETIME_MILLIS.toInt()
private const val OVERLAY_ZINDEX = 9F

@Composable
fun EmojiReactionOverlay(
    call: CallFacade,
    modifier: Modifier = Modifier,
) {
    val reactions by call.emojiSignalState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomStart,
    ) {
        reactions?.reactions?.forEach { reaction ->
            key(reaction.id) {
                EmojiAnimationItem(emojiReaction = reaction)
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun EmojiAnimationItem(
    emojiReaction: EmojiReaction,
) {
    val startX = remember { Random.nextInt(0, 500) }
    val progress = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = ANIMATION_DURATION, easing = EaseOutCubic),
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = ANIMATION_DURATION
                    0f at 0
                    1f at 200
                    1f at 2000
                    0f at ANIMATION_DURATION
                },
            )
        }
    }

    Box(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    val y = ((1f - progress.value) * constraints.maxHeight).roundToInt()
                    placeable.placeRelative(startX, y)
                }
            }
            .fillMaxSize()
            .graphicsLayer {
                this.alpha = alpha.value
            },
    ) {
        EmojiItem(
            emojiReaction = emojiReaction,
        )
    }
}

@Composable
private fun EmojiItem(
    emojiReaction: EmojiReaction,
) {
    val emojiLabel = if (emojiReaction.isYou) {
        stringResource(R.string.emoji_panel_you)
    } else {
        emojiReaction.sender
    }

    Column(
        modifier = Modifier
            .widthIn(max = 160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emojiReaction.emoji,
            fontSize = 32.sp,
            modifier = Modifier
                .padding(VonageVideoTheme.dimens.paddingSmall)
        )
        if (emojiLabel.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = VonageVideoTheme.shapes.medium,
                    )
                    .padding(VonageVideoTheme.dimens.paddingSmall)
            ) {
                Text(
                    text = emojiLabel,
                    style = VonageVideoTheme.typography.caption,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview
@Composable
internal fun EmojiItemPreview() {
    VonageVideoTheme {
        EmojiItem(
            emojiReaction = EmojiReaction(
                id = 1L,
                emoji = "🙈",
                sender = "John Doe Doe Doe Doe Doe Doe",
                isYou = false,
                startTime = 1231,
            ),
        )
    }
}
