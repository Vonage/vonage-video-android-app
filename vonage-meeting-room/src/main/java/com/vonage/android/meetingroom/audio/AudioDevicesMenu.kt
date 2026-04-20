package com.vonage.android.meetingroom.audio

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.fx.ui.NoiseSuppressorToggle
import com.vonage.android.meetingroom.R
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import com.vonage.audioselector.AudioDeviceSelector.AudioDeviceType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AudioDevicesMenu(
    audioDevicesState: AudioDevicesState,
    onDismissRequest: () -> Unit,
    onNoiseSuppressorToggle: (Boolean) -> Unit,
    noiseSuppressionEnabled: Boolean,
    modifier: Modifier = Modifier,
    testSpeakerContent: @Composable () -> Unit = {},
) {
    val availableDevices by audioDevicesState.availableDevices.collectAsStateWithLifecycle()
    val activeDevice by audioDevicesState.activeDevice.collectAsStateWithLifecycle()

    Column(
        modifier = modifier,
    ) {
        NoiseSuppressorToggle(
            title = stringResource(R.string.advanced_noise_suppression),
            isChecked = noiseSuppressionEnabled,
            onCheckedChange = onNoiseSuppressorToggle,
        )
        testSpeakerContent()
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
            audioDevicesState = AudioDevicesState(
                availableDevices = MutableStateFlow(
                    persistentListOf(
                        AudioDevice(1, AudioDeviceType.EARPIECE),
                        AudioDevice(2, AudioDeviceType.SPEAKER),
                    )
                ),
                activeDevice = MutableStateFlow(null),
                selectDevice = { _ -> },
            ),
            testSpeakerContent = {},
            onDismissRequest = {},
            onNoiseSuppressorToggle = {},
            noiseSuppressionEnabled = true,
        )
    }
}

