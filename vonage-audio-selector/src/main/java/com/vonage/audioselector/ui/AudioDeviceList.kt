package com.vonage.audioselector.ui

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
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.Call
import com.vonage.android.compose.vivid.icons.solid.Headset2
import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.R

@Composable
fun AudioDeviceList(
    availableDevices: List<AudioDeviceSelector.AudioDevice>,
    activeDevice: AudioDeviceSelector.AudioDevice?,
    selectDevice: (AudioDeviceSelector.AudioDevice) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        modifier = modifier.padding(bottom = VonageVideoTheme.dimens.paddingLarge),
        contentPadding = PaddingValues(VonageVideoTheme.dimens.paddingSmall),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall),
    ) {
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        start = VonageVideoTheme.dimens.paddingSmall,
                        bottom = VonageVideoTheme.dimens.paddingSmall,
                    ),
                text = "R.string.waiting_room_available_audio_outputs",
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
    audioDevice: AudioDeviceSelector.AudioDevice,
    selectDevice: (AudioDeviceSelector.AudioDevice) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val defaultColor = VonageVideoTheme.colors.secondary

    Column(
        modifier = modifier
            .height(VonageVideoTheme.dimens.avatarSizeXLarge)
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
            .padding(
                vertical = VonageVideoTheme.dimens.paddingSmall,
                horizontal = VonageVideoTheme.dimens.paddingDefault
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = VonageVideoTheme.dimens.spaceSmall,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        Icon(
            imageVector = audioDevice.type.toImageVector(),
            contentDescription = null,
            tint = defaultColor,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )

        Text(
            text = audioDevice.toLabel(),
            color = defaultColor,
            style = VonageVideoTheme.typography.bodyBase,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AudioDeviceSelector.AudioDevice.toLabel(): String =
    when (this.type) {
        AudioDeviceSelector.AudioDeviceType.EARPIECE -> stringResource(R.string.audio_device_selector_earpiece_audio_device)
        AudioDeviceSelector.AudioDeviceType.BLUETOOTH -> stringResource(R.string.audio_device_selector_bluetooth_audio_device)
        AudioDeviceSelector.AudioDeviceType.SPEAKER -> stringResource(R.string.audio_device_selector_speaker_audio_device)
        AudioDeviceSelector.AudioDeviceType.WIRED_HEADSET -> stringResource(R.string.audio_device_selector_headset_audio_device)
    }

@Composable
fun AudioDeviceSelector.AudioDeviceType.toImageVector(): ImageVector =
    when (this) {
        AudioDeviceSelector.AudioDeviceType.EARPIECE -> VividIcons.Solid.Call
        AudioDeviceSelector.AudioDeviceType.BLUETOOTH -> Icons.Default.Bluetooth
        AudioDeviceSelector.AudioDeviceType.SPEAKER -> VividIcons.Solid.AudioMid
        AudioDeviceSelector.AudioDeviceType.WIRED_HEADSET -> VividIcons.Solid.Headset2
    }

@PreviewLightDark
@Composable
internal fun AudioDeviceListPreview() {
    VonageVideoTheme {
        AudioDeviceList(
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            availableDevices = listOf(
                AudioDeviceSelector.AudioDevice(1, AudioDeviceSelector.AudioDeviceType.BLUETOOTH),
                AudioDeviceSelector.AudioDevice(2, AudioDeviceSelector.AudioDeviceType.EARPIECE),
                AudioDeviceSelector.AudioDevice(3, AudioDeviceSelector.AudioDeviceType.SPEAKER),
                AudioDeviceSelector.AudioDevice(
                    4,
                    AudioDeviceSelector.AudioDeviceType.WIRED_HEADSET
                ),
            ),
            activeDevice = AudioDeviceSelector.AudioDevice(
                3,
                AudioDeviceSelector.AudioDeviceType.SPEAKER
            ),
            selectDevice = {},
        )
    }
}
