package com.vonage.android.audio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.audio.AudioDeviceSelector.AudioDevice
import com.vonage.android.audio.AudioDeviceSelector.AudioDeviceType
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.Call
import com.vonage.android.compose.vivid.icons.solid.Headset2

@Composable
fun AudioDeviceList(
    availableDevices: List<AudioDevice>,
    activeDevice: AudioDevice?,
    selectDevice: (AudioDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier.padding(bottom = 24.dp),
        contentPadding = PaddingValues(8.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp),
                text = stringResource(R.string.waiting_room_available_audio_outputs),
                color = VonageVideoTheme.colors.textSecondary,
                style = VonageVideoTheme.typography.heading2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        items(
            items = availableDevices,
            key = { audioDevice -> audioDevice.id },
        ) { audioDevice ->
            val isSelected = audioDevice.id == activeDevice?.id
            AudioDeviceCell(
                audioDevice = audioDevice,
                selectDevice = selectDevice,
                isSelected = isSelected,
            )
        }
    }
}

@Composable
private fun AudioDeviceCell(
    audioDevice: AudioDevice,
    selectDevice: (AudioDevice) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val defaultColor = VonageVideoTheme.colors.onSurface
    val selectedColor = VonageVideoTheme.colors.surface

    Column(
        modifier = modifier
            .height(96.dp)
            .clickable { selectDevice(audioDevice) }
            .conditional(
                isSelected,
                ifTrue = {
                    background(
                        color = VonageVideoTheme.colors.primary,
                        shape = VonageVideoTheme.shapes.medium,
                    )
                },
                ifFalse = {
                    border(
                        width = VonageVideoTheme.dimens.borderWidthThin,
                        color = VonageVideoTheme.colors.surface,
                        shape = VonageVideoTheme.shapes.medium,
                    )
                },
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = audioDevice.type.toImageVector(),
            contentDescription = null,
            tint = if (isSelected) selectedColor else defaultColor,
            modifier = Modifier.size(24.dp),
        )

        Text(
            text = audioDevice.toLabel(),
            color = if (isSelected) selectedColor else defaultColor,
            style = VonageVideoTheme.typography.bodyBase,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AudioDevice.toLabel(): String =
    when (this.type) {
        AudioDeviceType.EARPIECE -> stringResource(R.string.audio_device_selector_earpiece_audio_device)
        AudioDeviceType.BLUETOOTH -> stringResource(R.string.audio_device_selector_bluetooth_audio_device)
        AudioDeviceType.SPEAKER -> stringResource(R.string.audio_device_selector_speaker_audio_device)
        AudioDeviceType.WIRED_HEADSET -> stringResource(R.string.audio_device_selector_headset_audio_device)
    }

@Composable
internal fun AudioDeviceType.toImageVector(): ImageVector =
    when (this) {
        AudioDeviceType.EARPIECE -> VividIcons.Solid.Call
        AudioDeviceType.BLUETOOTH -> Icons.Default.Bluetooth
        AudioDeviceType.SPEAKER -> VividIcons.Solid.AudioMid
        AudioDeviceType.WIRED_HEADSET -> VividIcons.Solid.Headset2
    }

@PreviewLightDark
@Composable
internal fun AudioDeviceListPreview() {
    VonageVideoTheme {
        AudioDeviceList(
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            availableDevices = listOf(
                AudioDevice(1, AudioDeviceType.BLUETOOTH),
                AudioDevice(2, AudioDeviceType.EARPIECE),
                AudioDevice(3, AudioDeviceType.SPEAKER),
                AudioDevice(4, AudioDeviceType.WIRED_HEADSET),
            ),
            activeDevice = AudioDevice(3, AudioDeviceType.SPEAKER),
            selectDevice = {},
        )
    }
}
