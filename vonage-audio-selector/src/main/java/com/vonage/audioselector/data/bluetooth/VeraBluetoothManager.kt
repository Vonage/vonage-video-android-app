package com.vonage.audioselector.data.bluetooth

import android.Manifest.permission
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.AudioManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages Bluetooth audio connections and state.
 * Tracks Bluetooth SCO connections and wired headset presence via broadcast receivers.
 */
internal class VeraBluetoothManager(
    private val context: Context,
    private val audioManager: AudioManager,
    bluetoothManager: BluetoothManager,
) {

    private val _bluetoothStates = MutableStateFlow<BluetoothState>(BluetoothState.Disconnected)
    val bluetoothStates: StateFlow<BluetoothState> = _bluetoothStates.asStateFlow()

    sealed interface BluetoothState {
        data object Connected : BluetoothState
        data object AudioConnected : BluetoothState
        data object Disconnected : BluetoothState
    }

    var bluetoothState: BluetoothState = BluetoothState.Disconnected

    enum class WiredState {
        Plugged,
        UnPlugged
    }

    var wiredState: WiredState = WiredState.UnPlugged

    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private var bluetoothProfile: BluetoothProfile? = null

    private var isBluetoothHeadsetReceiverRegistered = false
    private var isWiredHeadsetReceiverRegistered = false

    private val wiredHeadsetBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "headsetBroadcastReceiver.onReceive()")
            intent.getIntExtra(HEADSET_PLUG_STATE_KEY, HEADSET_UNPLUGGED).let { state ->
                if (state == HEADSET_PLUGGED) {
                    Log.d(TAG, "headsetBroadcastReceiver.onReceive(): Headphones connected")
                    wiredState = WiredState.Plugged
                } else {
                    Log.d(TAG, "headsetBroadcastReceiver.onReceive(): Headphones disconnected")
                    wiredState = WiredState.UnPlugged
                }
            }
        }
    }

    private val bluetoothBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && (action == BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED
                        || action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            ) {
                intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1).let { state ->
                    when (state) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            Log.d(TAG, "onReceive(): BluetoothHeadset.STATE_CONNECTED")
                            bluetoothState = BluetoothState.Connected
                            _bluetoothStates.value = BluetoothState.Connected
                        }

                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d(TAG, "onReceive(): BluetoothHeadset.STATE_DISCONNECTED")
                            bluetoothState = BluetoothState.Disconnected
                            _bluetoothStates.value = BluetoothState.Disconnected
                        }

                        BluetoothHeadset.STATE_AUDIO_CONNECTED -> {
                            Log.d(TAG, "onReceive(): BluetoothHeadset.STATE_AUDIO_CONNECTED")
                            bluetoothState = BluetoothState.AudioConnected
                            _bluetoothStates.value = BluetoothState.AudioConnected
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private val bluetoothProfileServiceListener: ServiceListener = object : ServiceListener {
        override fun onServiceConnected(type: Int, profile: BluetoothProfile) {
            Log.d(TAG, "BluetoothProfile.ServiceListener.onServiceConnected()")
            if (type == BluetoothProfile.HEADSET) {
                bluetoothProfile = profile
                val devices = profile.connectedDevices
                devices.forEach { device ->
                    Log.d(TAG, "Bluetooth ${device.name} connected")
                }
                if (devices.isNotEmpty()) {
                    bluetoothState = BluetoothState.Connected
                    _bluetoothStates.value = BluetoothState.Connected
                }
            }
        }

        override fun onServiceDisconnected(type: Int) {
            Log.d(TAG, "BluetoothProfile.ServiceListener.onServiceDisconnected()")
        }
    }

    init {
        if (checkPermission()) {
            bluetoothAdapter.getProfileProxy(
                context,
                bluetoothProfileServiceListener,
                BluetoothProfile.HEADSET,
            )
        }
    }

    fun startBluetooth() {
        Log.d(TAG, "startBluetooth called")
        audioManager.setBluetoothScoOn(true)
        audioManager.startBluetoothSco()
    }

    fun stopBluetooth() {
        Log.d(TAG, "stopBluetooth called")
        audioManager.setBluetoothScoOn(false)
        audioManager.stopBluetoothSco()
    }

    fun onStart() {
        Log.d(TAG, "onStart")
        if (audioManager.isBluetoothScoAvailableOffCall) {
            registerBluetoothReceiver()
            registerHeadsetReceiver()
        }
    }

    fun onStop() {
        Log.d(TAG, "onStop")
        unregisterBluetoothReceiver()
        unregisterHeadsetReceiver()
        closeBluetoothProfile()
    }

    private fun registerBluetoothReceiver() {
        Log.d(TAG, "registerBluetoothReceiver() called")
        if (isBluetoothHeadsetReceiverRegistered) {
            return
        }
        val filter = IntentFilter()
            .apply {
                addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)
            }
        context.registerReceiver(bluetoothBroadcastReceiver, filter)
        isBluetoothHeadsetReceiverRegistered = true
    }

    private fun unregisterBluetoothReceiver() {
        Log.d(TAG, "unregisterBtReceiver() called")
        if (!isBluetoothHeadsetReceiverRegistered) {
            return
        }
        context.unregisterReceiver(bluetoothBroadcastReceiver)
        isBluetoothHeadsetReceiverRegistered = false
    }

    private fun registerHeadsetReceiver() {
        Log.d(TAG, "registerHeadsetReceiver() called")
        if (isWiredHeadsetReceiverRegistered) {
            return
        }
        context.registerReceiver(wiredHeadsetBroadcastReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
        isWiredHeadsetReceiverRegistered = true
    }

    private fun unregisterHeadsetReceiver() {
        Log.d(TAG, "unregisterHeadsetReceiver() called")
        if (!isWiredHeadsetReceiverRegistered) {
            return
        }
        context.unregisterReceiver(wiredHeadsetBroadcastReceiver)
        isWiredHeadsetReceiverRegistered = false
    }

    private fun closeBluetoothProfile() {
        Log.d(TAG, "disable bluetooth events")
        bluetoothProfile?.let {
            bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, it)
        }
        // Force a shutdown of bluetooth: when a call comes in, the handler is not invoked by system.
        val intent = Intent(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        intent.putExtra(BluetoothHeadset.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED)
        bluetoothBroadcastReceiver.onReceive(context, intent)
    }

    private fun checkPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                context.checkSelfPermission(permission.BLUETOOTH_CONNECT) == PERMISSION_GRANTED

    private companion object {
        const val TAG = "VeraBluetoothManager"

        const val HEADSET_PLUG_STATE_KEY = "state"
        const val HEADSET_UNPLUGGED = 0
        const val HEADSET_PLUGGED = 1
    }
}
