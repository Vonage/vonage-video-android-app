package com.vonage.android.archiving.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.archiving.ArchiveType
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.VideoActive
import com.vonage.android.compose.vivid.icons.line.VoicemailTranscript

@Composable
fun ArchiveIcon(
    modifier: Modifier = Modifier,
    type: ArchiveType = ArchiveType.RECORDING,
) {
    Icon(
        modifier = modifier
            .size(VonageVideoTheme.dimens.iconSizeDefault),
        imageVector = when (type) {
            ArchiveType.RECORDING -> VividIcons.Line.VideoActive
            ArchiveType.TRANSCRIPTION -> VividIcons.Line.VoicemailTranscript
        },
        contentDescription = null,
        tint = VonageVideoTheme.colors.secondary,
    )
}
