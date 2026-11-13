package com.vonage.android.compose.components.audio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import com.vonage.audioselector.AudioDeviceSelector.AudioDeviceType
import com.vonage.audioselector.AudioDevicesState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Audio output picker. [headerContent] renders above the device list
 * (e.g. noise suppression toggle, speaker test).
 */
@Composable
fun AudioDevicesMenu(
    audioDevicesState: AudioDevicesState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    headerContent: @Composable ColumnScope.() -> Unit = {},
) {
    val availableDevices by audioDevicesState.availableDevices.collectAsStateWithLifecycle()
    val activeDevice by audioDevicesState.activeDevice.collectAsStateWithLifecycle()

    Column(
        modifier = modifier,
    ) {
        headerContent()
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
            onDismissRequest = {},
        )
    }
}
