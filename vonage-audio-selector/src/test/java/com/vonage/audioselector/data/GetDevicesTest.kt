package com.vonage.audioselector.data

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import com.vonage.audioselector.AudioDeviceSelector.AudioDevice
import com.vonage.audioselector.AudioDeviceSelector.AudioDeviceType
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager.BluetoothState
import com.vonage.audioselector.data.bluetooth.VeraBluetoothManager.WiredState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class GetDevicesTest {

    private val context: Context = mockk(relaxed = true)
    private val bluetoothManager: VeraBluetoothManager = mockk()
    private val audioManager: AudioManager = mockk(relaxed = true)
    private val sut = GetDevices(
        context = context,
        bluetoothManager = bluetoothManager,
        audioManager = audioManager,
    )

    @ParameterizedTest
    @MethodSource("deviceConditions")
    fun `should return device list`(
        bluetoothState: BluetoothState,
        wiredState: WiredState,
        hasEarpiece: Boolean,
        expectedDeviceList: List<AudioDevice>,
    ) {
        every { bluetoothManager.bluetoothStates } returns MutableStateFlow(bluetoothState)
        every { bluetoothManager.wiredState } returns wiredState
        every { context.packageManager } returns mockk<PackageManager> {
            every { hasSystemFeature(PackageManager.FEATURE_TELEPHONY) } returns hasEarpiece
        }

        val devices = sut.invoke()

        assertEquals(
            expectedDeviceList,
            devices,
        )
    }

    companion object {
        @JvmStatic
        fun deviceConditions() = listOf(
            // bluetooth connected, wired unplugged and earpiece
            of(
                BluetoothState.Connected, WiredState.UnPlugged, true, listOf(
                    AudioDevice(type = AudioDeviceType.BLUETOOTH),
                    AudioDevice(type = AudioDeviceType.SPEAKER),
                    AudioDevice(type = AudioDeviceType.EARPIECE),
                )
            ),
            // bluetooth disconnected, wired unplugged and earpiece
            of(
                BluetoothState.Disconnected, WiredState.UnPlugged, true, listOf(
                    AudioDevice(type = AudioDeviceType.SPEAKER),
                    AudioDevice(type = AudioDeviceType.EARPIECE),
                )
            ),
            // bluetooth connected, wired plugged — earpiece suppressed by wired headset
            of(
                BluetoothState.Connected, WiredState.Plugged, true, listOf(
                    AudioDevice(type = AudioDeviceType.BLUETOOTH),
                    AudioDevice(type = AudioDeviceType.WIRED_HEADSET),
                    AudioDevice(type = AudioDeviceType.SPEAKER),
                )
            ),
            // bluetooth disconnected, wired plugged — earpiece suppressed by wired headset
            of(
                BluetoothState.Disconnected, WiredState.Plugged, true, listOf(
                    AudioDevice(type = AudioDeviceType.WIRED_HEADSET),
                    AudioDevice(type = AudioDeviceType.SPEAKER),
                )
            ),
        )
    }
}
