package com.vonage.audioselector.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import app.cash.turbine.test
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VeraBluetoothManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val context: Context = mockk()
    private val audioManager: AudioManager = mockk(relaxed = true)
    private val bluetoothAdapter: BluetoothAdapter = mockk(relaxed = true)
    private val bluetoothManager: BluetoothManager = mockk(relaxed = true)
    private val bluetoothProfile: BluetoothProfile = mockk(relaxed = true)
    private val bluetoothDevice: BluetoothDevice = mockk(relaxed = true)

    private lateinit var sut: VeraBluetoothManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { bluetoothManager.adapter } returns bluetoothAdapter
        every { context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) } returns PackageManager.PERMISSION_GRANTED
        every { bluetoothAdapter.getProfileProxy(any(), any(), any()) } returns true
        every { context.getSystemService(Context.AUDIO_SERVICE) } returns audioManager
        every { context.getSystemService(Context.BLUETOOTH_SERVICE) } returns bluetoothManager
        every { context.registerReceiver(any(), any()) } returns null
        every { context.unregisterReceiver(any()) } just runs
    }

    @Test
    fun `initial state is Disconnected`() {
        sut = createBluetoothManager()

        assertEquals(VeraBluetoothManager.BluetoothState.Disconnected, sut.bluetoothState)
    }

    @Test
    fun `initial wired state is UnPlugged`() {
        sut = createBluetoothManager()

        assertEquals(VeraBluetoothManager.WiredState.UnPlugged, sut.wiredState)
    }

    @Test
    fun `startBluetooth enables bluetooth SCO`() {
        sut = createBluetoothManager()

        sut.startBluetooth()

        verify { audioManager.setBluetoothScoOn(true) }
        verify { audioManager.startBluetoothSco() }
    }

    @Test
    fun `stopBluetooth disables bluetooth SCO`() {
        sut = createBluetoothManager()

        sut.stopBluetooth()

        verify { audioManager.setBluetoothScoOn(false) }
        verify { audioManager.stopBluetoothSco() }
    }

    @Test
    fun `onStart registers receivers when bluetooth SCO is available`() {
        every { audioManager.isBluetoothScoAvailableOffCall } returns true
        sut = createBluetoothManager()

        sut.onStart()

        verify { context.registerReceiver(any(), any()) }
    }

    @Test
    fun `onStart does not register receivers when bluetooth SCO is not available`() {
        every { audioManager.isBluetoothScoAvailableOffCall } returns false
        sut = createBluetoothManager()

        sut.onStart()

        verify(exactly = 0) { context.registerReceiver(any(), any()) }
    }

    @Test
    fun `onStop unregisters receivers`() {
        every { audioManager.isBluetoothScoAvailableOffCall } returns true
        sut = createBluetoothManager()
        sut.onStart()

        sut.onStop()

        verify(exactly = 2) { context.unregisterReceiver(any()) }
    }

    @Test
    fun `profile service listener sets state to Connected when device is connected`() =
        runTest(testDispatcher) {
            val serviceListenerSlot = slot<BluetoothProfile.ServiceListener>()
            every {
                bluetoothAdapter.getProfileProxy(
                    any(),
                    capture(serviceListenerSlot),
                    any()
                )
            } returns true
            every { bluetoothProfile.connectedDevices } returns listOf(bluetoothDevice)
            every { bluetoothDevice.name } returns "Test Headset"

            sut = createBluetoothManager()

            sut.bluetoothStates.test {
                assertEquals(
                    VeraBluetoothManager.BluetoothState.Disconnected,
                    awaitItem()
                ) // initial state

                serviceListenerSlot.captured.onServiceConnected(
                    BluetoothProfile.HEADSET,
                    bluetoothProfile
                )
                testDispatcher.scheduler.advanceUntilIdle()

                assertEquals(VeraBluetoothManager.BluetoothState.Connected, awaitItem())
                assertEquals(VeraBluetoothManager.BluetoothState.Connected, sut.bluetoothState)
            }
        }

    @Test
    fun `profile service listener maintains Disconnected state when no devices connected`() {
        val serviceListenerSlot = slot<BluetoothProfile.ServiceListener>()
        every {
            bluetoothAdapter.getProfileProxy(
                any(),
                capture(serviceListenerSlot),
                any()
            )
        } returns true
        every { bluetoothProfile.connectedDevices } returns emptyList()

        sut = createBluetoothManager()

        serviceListenerSlot.captured.onServiceConnected(BluetoothProfile.HEADSET, bluetoothProfile)

        assertEquals(VeraBluetoothManager.BluetoothState.Disconnected, sut.bluetoothState)
    }

    @Test
    fun `onStart does not register bluetooth receiver twice`() {
        every { audioManager.isBluetoothScoAvailableOffCall } returns true
        sut = createBluetoothManager()

        sut.onStart()
        sut.onStart()

        // Should register twice (bluetooth and wired), not four times
        verify(exactly = 2) { context.registerReceiver(any(), any()) }
    }

    @Test
    fun `onStop does not unregister receivers if not registered`() {
        sut = createBluetoothManager()

        sut.onStop()

        verify(exactly = 0) { context.unregisterReceiver(any()) }
    }

    @Test
    fun `closeBluetoothProfile calls closeProfileProxy`() {
        every { audioManager.isBluetoothScoAvailableOffCall } returns true
        every { bluetoothAdapter.closeProfileProxy(any(), any()) } just runs

        val serviceListenerSlot = slot<BluetoothProfile.ServiceListener>()
        every {
            bluetoothAdapter.getProfileProxy(
                any(),
                capture(serviceListenerSlot),
                any()
            )
        } returns true
        every { bluetoothProfile.connectedDevices } returns emptyList()

        sut = createBluetoothManager()

        // Simulate service connection to set the bluetoothProfile
        serviceListenerSlot.captured.onServiceConnected(BluetoothProfile.HEADSET, bluetoothProfile)

        sut.onStart()
        sut.onStop()

        // Verify that closeProfileProxy was called with the profile
        verify { bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothProfile) }
    }

    private fun createBluetoothManager() =
        VeraBluetoothManager(
            context = context,
            audioManager = audioManager,
            bluetoothManager = bluetoothManager
        )
}
