package com.vonage.android.screen.room.components.emoji

import android.util.Log
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.signal.EmojiReaction
import kotlin.random.Random

@Composable
fun EmojiReactionOverlay(
    reactions: List<EmojiReaction>,
    modifier: Modifier = Modifier,
) {
    Log.d("EmojiReaction", "EmojiReactionOverlay called with ${reactions.size} reactions")

    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .zIndex(9F)
            .fillMaxSize()
            .onSizeChanged {
                size = it
            },
        contentAlignment = Alignment.BottomStart,
    ) {
        reactions.forEach { reaction ->
            Log.d("EmojiReaction", "Rendering reaction: ${reaction.emoji} with id: ${reaction.id}")
            key(reaction.id) {
                EmojiAnimationItem(
                    emoji = reaction.emoji,
                    size = size,
                )
            }
        }
    }
}

private const val ANIMATION_DURATION = 5000

@Composable
private fun EmojiAnimationItem(
    emoji: String,
    size: IntSize,
) {
    var moved by remember { mutableStateOf(false) }
    val startX by remember { mutableIntStateOf(Random.nextInt(0, 500)) }

    val offset by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(startX, 0)
        } else {
            IntOffset(startX, size.height)
        },
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = EaseOutCubic,
        ),
        label = "offset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (moved) {
            1f
        } else {
            0f
        },
        animationSpec = keyframes {
            durationMillis = ANIMATION_DURATION
            0f at 0
            1f at 200
            1f at 2000
            0f at ANIMATION_DURATION
        },
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        moved = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { offset }
            .graphicsLayer {
                this.alpha = alpha
            },
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "You",
                    color = Color.White,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview
@Composable
internal fun EmojiAnimationItemPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            EmojiAnimationItem(
                emoji = "🙈",
                size = IntSize(300, 300),
            )
        }
    }
}
