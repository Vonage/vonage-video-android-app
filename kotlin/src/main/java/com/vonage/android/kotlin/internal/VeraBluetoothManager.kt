package com.vonage.android.kotlin.internal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.STATE_CONNECTED
import android.bluetooth.BluetoothProfile.STATE_DISCONNECTED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log

class VeraBluetoothManager(
    private val context: Context,
) {
    private val bluetoothManager: BluetoothManager
    private val audioManager: AudioManager
    private val bluetoothAdapter: BluetoothAdapter?
    private var bluetoothProfile: BluetoothProfile?
    private val bluetoothLock = Any()

    private var isBluetoothHeadSetReceiverRegistered = false
    private var isHeadsetReceiverRegistered = false

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothProfile = null
    }

    private val wiredHeadsetBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "headsetBroadcastReceiver.onReceive()")
            intent.getIntExtra(HEADSET_PLUG_STATE_KEY, HEADSET_UNPLUGGED).let { state ->
                if (state == HEADSET_PLUGGED) {
                    Log.d(TAG, "headsetBroadcastReceiver.onReceive(): Headphones connected")
                    audioManager.setSpeakerphoneOn(false)
                    audioManager.setBluetoothScoOn(false)
                } else {
                    Log.d(TAG, "headsetBroadcastReceiver.onReceive(): Headphones disconnected")
                    // fallback to earpiece
                    audioManager.setSpeakerphoneOn(false)
                }
            }
        }
    }

    private val bluetoothBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action != null && action == BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) {
                intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1).let { state ->
                    when (state) {
                        STATE_CONNECTED -> {
                            Log.d(TAG, "bluetoothBroadcastReceiver.onReceive(): BluetoothHeadset.STATE_CONNECTED")
                            Handler(Looper.getMainLooper())
                                .postDelayed(Runnable { startBluetooth() }, DEFAULT_BLUETOOTH_SCO_START_DELAY)
                        }

                        STATE_DISCONNECTED -> {
                            Log.d(TAG, "bluetoothBroadcastReceiver.onReceive(): BluetoothHeadset.STATE_DISCONNECTED")
                            stopBluetooth()
                        }

                        else -> {}
                    }
                }
            } else if (null != action && action == AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED) {
                val state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)
                when (state) {
                    AudioManager.SCO_AUDIO_STATE_CONNECTED -> {
                        Log.d(TAG, "bluetoothBroadcastReceiver.onReceive(): AudioManager.SCO_AUDIO_STATE_CONNECTED")
                    }

                    AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> {
                        Log.d(TAG, "bluetoothBroadcastReceiver.onReceive(): AudioManager.SCO_AUDIO_STATE_DISCONNECTED")
                        // restoreAudioAfterBluetoothDisconnect()
                    }

                    else -> {}
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private val bluetoothProfileServiceListener: BluetoothProfile.ServiceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(type: Int, profile: BluetoothProfile) {
            Log.d(TAG, "BluetoothProfile.ServiceListener.onServiceConnected()")
            if (type == BluetoothProfile.HEADSET) {
                bluetoothProfile = profile
                val devices = profile.connectedDevices

                devices.forEach { device ->
                    Log.d(TAG, "Bluetooth ${device.name} connected")
                }

                if (!devices.isEmpty() && profile.getConnectionState(devices[0]) == STATE_CONNECTED) {
                    // Force a init of bluetooth: the handler will not send a connected event if a
                    // device is already connected at the time of proxy connection request.
                    val intent = Intent(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
                        .apply {
                            putExtra(BluetoothHeadset.EXTRA_STATE, STATE_CONNECTED)
                        }
                    bluetoothBroadcastReceiver.onReceive(context, intent)
                }
            }
        }

        override fun onServiceDisconnected(type: Int) {
            Log.d(TAG, "BluetoothProfile.ServiceListener.onServiceDisconnected()")
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

    /**
     * Force stop Bluetooth and prevent automatic restart
     * Used when user explicitly selects non-Bluetooth audio device
     */
    fun forceStopBluetooth() {
        Log.d(TAG, "forceStopBluetooth called - user selected non-Bluetooth device")
        stopBluetooth()
        // Unregister receivers temporarily to prevent automatic restart
        unregisterBtReceiver()
    }

    /**
     * Re-enable Bluetooth management after it was force stopped
     * Used when user explicitly selects Bluetooth device
     */
    fun enableBluetoothManagement() {
        Log.d(TAG, "enableBluetoothManagement called - user selected Bluetooth device")
        registerBtReceiver()
        startBluetooth()
        forceInvokeConnectBluetooth()
    }

    private fun forceInvokeConnectBluetooth() {
        Log.d(TAG, "forceConnectBluetooth() called")
        // Force reconnection of bluetooth in the event of a phone call.
        synchronized(bluetoothLock) {
            bluetoothAdapter?.getProfileProxy(
                context,
                bluetoothProfileServiceListener,
                BluetoothProfile.HEADSET
            )
        }
    }

    private fun disableBluetoothEvents() {
        Log.d(TAG, "disable bluetooth events")
        if (bluetoothProfile != null && bluetoothAdapter != null) {
            bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothProfile)
        }

        unregisterBtReceiver()

        // Force a shutdown of bluetooth: when a call comes in, the handler is not invoked by system.
        val intent = Intent(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
        intent.putExtra(BluetoothHeadset.EXTRA_STATE, STATE_DISCONNECTED)
        bluetoothBroadcastReceiver.onReceive(context, intent)
    }

    private fun registerBtReceiver() {
        Log.d(TAG, "registerBtReceiver() called .. isBluetoothHeadSetReceiverRegistered = $isBluetoothHeadSetReceiverRegistered")
        if (isBluetoothHeadSetReceiverRegistered) {
            return
        }
        val filter = IntentFilter()
            .apply {
                addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
                addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)
            }
        context.registerReceiver(bluetoothBroadcastReceiver, filter)
        isBluetoothHeadSetReceiverRegistered = true
    }

    private fun unregisterBtReceiver() {
        Log.d(TAG, "unregisterBtReceiver() called .. bluetoothHeadSetReceiverRegistered = $isBluetoothHeadSetReceiverRegistered")
        if (!isBluetoothHeadSetReceiverRegistered) {
            return
        }
        context.unregisterReceiver(bluetoothBroadcastReceiver)
        isBluetoothHeadSetReceiverRegistered = false
    }

    private fun registerHeadsetReceiver() {
        Log.d(TAG, "registerHeadsetReceiver() called ... isHeadsetReceiverRegistered = $isHeadsetReceiverRegistered")

        if (isHeadsetReceiverRegistered) {
            return
        }

        context.registerReceiver(wiredHeadsetBroadcastReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
        isHeadsetReceiverRegistered = true
    }

    private fun unregisterHeadsetReceiver() {
        Log.d(TAG, "unregisterHeadsetReceiver() called ... isHeadsetReceiverRegistered = $isHeadsetReceiverRegistered")
        if (!isHeadsetReceiverRegistered) {
            return
        }
        context.unregisterReceiver(wiredHeadsetBroadcastReceiver)
        isHeadsetReceiverRegistered = false
    }

    fun onPause() {
        Log.d(TAG, "onPause")
        unregisterBtReceiver()
        unregisterHeadsetReceiver()
    }

    fun onResume() {
        Log.d(TAG, "onResume")
        registerBtReceiver()
        registerHeadsetReceiver()
    }

    fun onStopRenderer() {
        unregisterHeadsetReceiver()
        unregisterBtReceiver()
    }

    fun onDestroyRenderer() {
        disableBluetoothEvents()
        unregisterHeadsetReceiver()
        audioManager.setSpeakerphoneOn(false)
    }

    fun onInitRenderer() {
        if (audioManager.isBluetoothScoAvailableOffCall) {
            registerBtReceiver()
            registerHeadsetReceiver()
        }
    }

    companion object {
        const val TAG = "VeraBluetoothManager"
        const val DEFAULT_BLUETOOTH_SCO_START_DELAY = 2000L

        const val HEADSET_PLUG_STATE_KEY = "state"
        const val HEADSET_UNPLUGGED = 0
        const val HEADSET_PLUGGED = 1
    }
}
