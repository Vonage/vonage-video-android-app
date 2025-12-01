package com.vonage.android.screen.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.AudioMid
import com.vonage.android.compose.vivid.icons.line.CameraSwitch

@Composable
fun DeviceSelectionPanel(
    onMicDeviceSelect: () -> Unit,
    onCameraDeviceSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(VonageVideoTheme.dimens.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DeviceSelector(
            modifier = Modifier.weight(1f),
            icon = VividIcons.Line.AudioMid,
            label = stringResource(R.string.waiting_room_audio_output),
            onClick = onMicDeviceSelect,
        )

        DeviceSelector(
            modifier = Modifier.weight(1f),
            icon = VividIcons.Line.CameraSwitch,
            label = stringResource(R.string.waiting_room_switch_camera),
            onClick = onCameraDeviceSelect,
        )
    }
}

@Composable
private fun DeviceSelector(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = VonageVideoTheme.colors.onSurface,
                shape = VonageVideoTheme.shapes.medium,
            )
            .clip(VonageVideoTheme.shapes.medium)
            .clickable { onClick() }
            .padding(
                vertical = VonageVideoTheme.dimens.paddingSmall,
                horizontal = VonageVideoTheme.dimens.paddingDefault,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VonageVideoTheme.colors.tertiary,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
        Text(
            text = label,
            style = VonageVideoTheme.typography.bodyExtended,
            color = VonageVideoTheme.colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewLightDark
@Composable
internal fun DeviceSelectorPreview() {
    VonageVideoTheme {
        DeviceSelectionPanel(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(8.dp),
            onMicDeviceSelect = { },
            onCameraDeviceSelect = { },
        )
    }
}
