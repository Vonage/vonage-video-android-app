package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Rec
import com.vonage.android.compose.vivid.icons.solid.VoicemailTranscript

/**
 * Translucent pill with an icon and a short message, used for in-call notifications such as
 * "Recording started" or "Transcription stopped".
 */
@Composable
fun NotificationToast(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Red,
) {
    Row(
        modifier = modifier
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
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
        )
        Text(
            text = text,
            color = VonageVideoTheme.colors.textSecondary,
            style = VonageVideoTheme.typography.bodyExtended,
        )
    }
}

@PreviewLightDark
@Composable
internal fun RecordingNotificationToastPreview() {
    VonageVideoTheme {
        NotificationToast(icon = VividIcons.Solid.Rec, text = "Recording started")
    }
}

@PreviewLightDark
@Composable
internal fun TranscriptionNotificationToastPreview() {
    VonageVideoTheme {
        NotificationToast(icon = VividIcons.Solid.VoicemailTranscript, text = "Transcription started")
    }
}
