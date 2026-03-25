package com.vonage.android.screen.room.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.kotlin.model.PublisherState
import kotlinx.coroutines.flow.MutableStateFlow

private const val OVERLAY_ZINDEX = 11F

@Composable
fun SpeakingWhileMutedOverlay(
    publisher: PublisherState?,
    modifier: Modifier = Modifier,
) {
    val isSpeakingWhileMuted by remember(publisher) {
        publisher?.isSpeakingWhileMuted ?: MutableStateFlow(false)
    }.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = isSpeakingWhileMuted,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            SpeakingWhileMutedSnackbar()
        }
    }
}

@Composable
private fun SpeakingWhileMutedSnackbar() {
    Row(
        modifier = Modifier
            .background(
                color = VonageVideoTheme.colors.background.copy(alpha = 0.7f),
                shape = VonageVideoTheme.shapes.medium,
            )
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingSmall,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Icon(
            imageVector = VividIcons.Solid.MicMute,
            contentDescription = null,
            tint = VonageVideoTheme.colors.error,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
        )
        Text(
            text = stringResource(R.string.speaking_while_muted_message),
            color = VonageVideoTheme.colors.textSecondary,
            style = VonageVideoTheme.typography.bodyExtended,
        )
    }
}

@PreviewLightDark
@Composable
internal fun SpeakingWhileMutedSnackbarPreview() {
    VonageVideoTheme {
        SpeakingWhileMutedSnackbar()
    }
}
