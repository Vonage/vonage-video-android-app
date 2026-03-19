package com.vonage.android.screen.components.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.audioselector.AudioDeviceSelector
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AudioDevicesMenu(
    audioDevicesState: AudioDevicesState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val availableDevices by audioDevicesState.availableDevices.collectAsStateWithLifecycle()
    val activeDevice by audioDevicesState.activeDevice.collectAsStateWithLifecycle()

    Column(
        modifier = modifier,
    ) {
        TestSpeaker()
        AudioDeviceList(
            availableDevices = availableDevices,
            activeDevice = activeDevice,
            selectDevice = { device ->
                onDismissRequest()
                audioDevicesState.selectDevice(device)
            },
        )
    }
}

@PreviewLightDark
@Composable
internal fun AudioDevicesMenuPreview() {
    VonageVideoTheme {
        AudioDevicesMenu(
            modifier = Modifier
                .background(VonageVideoTheme.colors.surface),
            audioDevicesState = AudioDevicesState(
                availableDevices = MutableStateFlow(
                    persistentListOf(
                        AudioDeviceSelector.AudioDevice(
                            1,
                            AudioDeviceSelector.AudioDeviceType.EARPIECE
                        ),
                        AudioDeviceSelector.AudioDevice(
                            2,
                            AudioDeviceSelector.AudioDeviceType.SPEAKER
                        ),
                    )
                ),
                activeDevice = MutableStateFlow(null),
                selectDevice = { _ -> },
            ),
            onDismissRequest = {},

            )
    }
}
