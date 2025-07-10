package com.vonage.android.screen.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.theme.VonageVideoTheme

/**
 * Placeholder implementation
 */
@Composable
fun DeviceSelectionPanel(
    onMicDeviceSelect: () -> Unit,
    onCameraDeviceSelect: () -> Unit,
    onSpeakerDeviceSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DeviceSelector(
            icon = Icons.Default.Mic,
            label = "Mic",
            onClick = onMicDeviceSelect,
        )

        DeviceSelector(
            icon = Icons.Default.Videocam,
            label = "Camera",
            onClick = onCameraDeviceSelect,
        )

        DeviceSelector(
            icon = Icons.Default.Speaker,
            label = "Speaker",
            onClick = onSpeakerDeviceSelect,
        )
    }
}

@Composable
fun DeviceSelector(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
internal fun DeviceSelectorPreview() {
    VonageVideoTheme {
        DeviceSelectionPanel(
            onMicDeviceSelect = { },
            onCameraDeviceSelect = { },
            onSpeakerDeviceSelect = { },
        )
    }
}
