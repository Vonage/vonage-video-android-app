package com.vonage.android.captions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.zIndex
import com.vonage.android.captions.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CaptionLine
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val OVERLAY_ZINDEX = 10F

@Composable
fun CaptionsOverlay(
    captionLines: ImmutableList<CaptionLine>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (captionLines.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .background(
                        color = VonageVideoTheme.colors.surface,
                        shape = VonageVideoTheme.shapes.medium,
                    )
                    .align(Alignment.BottomCenter)
                    .padding(VonageVideoTheme.dimens.paddingSmall),
                verticalArrangement = spacedBy(VonageVideoTheme.dimens.paddingSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                captionLines.forEach { line ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                val displayName = if (line.isMe) {
                                    stringResource(R.string.captions_self_name)
                                } else line.subscriberName
                                append("$displayName: ")
                            }
                            append(line.text)
                        },
                        style = VonageVideoTheme.typography.bodyBase,
                        color = VonageVideoTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun CaptionsOverlayPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background),
        ) {
            CaptionsOverlay(
                captionLines = persistentListOf(
                    CaptionLine(streamId = "1", subscriberName = "Alice", isMe = false, text = "Hello, how are you?"),
                    CaptionLine(streamId = "2", subscriberName = "Bob", isMe = false, text = "I'm doing great, thanks!"),
                ),
            )
        }
    }
}
