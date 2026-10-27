package com.vonage.android.compose.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.VoicemailTranscript

/** Shown while a post-call transcription is running, next to the recording indicator. */
@Composable
fun TranscriptionIndicator(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Icon(
        modifier = modifier,
        imageVector = VividIcons.Solid.VoicemailTranscript,
        tint = Color.Red,
        contentDescription = contentDescription,
    )
}

@PreviewLightDark
@Composable
internal fun TranscriptionIndicatorPreview() {
    VonageVideoTheme {
        TranscriptionIndicator()
    }
}
