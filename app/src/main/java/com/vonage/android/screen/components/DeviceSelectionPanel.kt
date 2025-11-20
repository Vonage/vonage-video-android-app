package com.vonage.android.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.AudioMid
import com.vonage.android.compose.vivid.icons.line.CameraSwitch
import com.vonage.android.compose.vivid.icons.solid.MoreVertical

@Composable
fun DeviceSelectionPanel(
    onMicDeviceSelect: () -> Unit,
    onCameraDeviceSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DeviceSelector(
            icon = VividIcons.Line.AudioMid,
            label = stringResource(R.string.waiting_room_audio_output),
            onClick = onMicDeviceSelect,
            showIndicator = true,
        )

        DeviceSelector(
            icon = VividIcons.Line.CameraSwitch,
            label = stringResource(R.string.waiting_room_switch_camera),
            onClick = onCameraDeviceSelect,
        )
    }
}

@Composable
fun DeviceSelector(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showIndicator: Boolean = false,
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = VonageVideoTheme.shapes.medium,
            )
            .clip(VonageVideoTheme.shapes.medium)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
        )
        if (showIndicator) {
            Icon(
                imageVector = VividIcons.Solid.MoreVertical,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp),
            )
        }
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
