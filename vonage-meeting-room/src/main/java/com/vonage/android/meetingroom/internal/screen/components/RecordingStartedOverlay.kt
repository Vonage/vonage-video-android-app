package com.vonage.android.meetingroom.internal.screen.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.zIndex
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Rec
import com.vonage.android.meetingroom.R
import kotlinx.coroutines.delay

private const val OVERLAY_ZINDEX = 11F
private const val OVERLAY_DURATION_MS = 4000L

@Composable
internal fun RecordingStartedOverlay(
    isRecordingStartedByOthers: Boolean,
    modifier: Modifier = Modifier,
) {
    var showOverlay by remember { mutableStateOf(false) }

    // Show overlay when recording starts, auto-dismiss after duration
    LaunchedEffect(isRecordingStartedByOthers) {
        if (isRecordingStartedByOthers) {
            showOverlay = true
            delay(OVERLAY_DURATION_MS)
            showOverlay = false
        }
    }

    Box(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = showOverlay,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            RecordingStartedSnackbar()
        }
    }
}

@Composable
private fun RecordingStartedSnackbar() {
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
            imageVector = VividIcons.Solid.Rec,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
        )
        Text(
            text = stringResource(R.string.recording_started_notification),
            color = VonageVideoTheme.colors.textSecondary,
            style = VonageVideoTheme.typography.bodyExtended,
        )
    }
}

@PreviewLightDark
@Composable
internal fun RecordingStartedSnackbarPreview() {
    VonageVideoTheme {
        RecordingStartedSnackbar()
    }
}
