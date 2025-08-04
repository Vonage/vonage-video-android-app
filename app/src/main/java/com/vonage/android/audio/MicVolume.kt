package com.vonage.android.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.RECORDSTATE_RECORDING
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

class MicVolume() {

    @SuppressLint("MissingPermission")
    fun volume(samplingMillis: Long = 60): Flow<Float> = callbackFlow {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE,
        )
        audioRecord.startRecording()

        val buffer = ShortArray(BUFFER_SIZE)
        var bufferReadSize: Int

        audioRecord.setRecordPositionUpdateListener(object : AudioRecord.OnRecordPositionUpdateListener {
            override fun onMarkerReached(recorder: AudioRecord?) {
                Log.d("MicVolume", "onMarkerReached -------------")
            }

            override fun onPeriodicNotification(recorder: AudioRecord?) {
                Log.d("MicVolume", "onPeriodicNotification -------------")
            }
        })

        while (audioRecord.recordingState == RECORDSTATE_RECORDING) {
            // READ_NON_BLOCKING can cause problems in Samsung Devices
            bufferReadSize = audioRecord.read(buffer, 0, BUFFER_SIZE, AudioRecord.READ_BLOCKING)
            if (bufferReadSize <= 0) continue
            val rms = normalizeAudioLevel(buffer, bufferReadSize)
            Log.d("MicVolume", "mic volume RMS $rms")
            trySend(rms)
            delay(samplingMillis)
        }

        awaitClose {
            Log.d("MicVolume", "recording STOPPED")
            audioRecord.stop()
        }
    }

    private fun normalizeAudioLevel(buffer: ShortArray, length: Int): Float {
        if (length <= 0) return 0f

        var sum = 0L
        for (i in 0 until length) {
            sum += (buffer[i] * buffer[i]).toLong()
        }

        val rms = sqrt(sum.toDouble() / length).toFloat()
        return (rms / Short.MAX_VALUE).coerceIn(0f, 1f) * 10
    }

    companion object {
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val SAMPLE_RATE = 44100
        val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
    }
}
