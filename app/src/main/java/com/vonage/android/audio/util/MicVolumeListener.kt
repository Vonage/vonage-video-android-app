package com.vonage.android.audio.util

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.sqrt

/**
 * Utility class to listen directly to the device microphone volume.
 * It's used in waiting room to show the volume indicator,
 * the conference room it's listening to the SDK AudioLevelListener
 */
@SuppressLint("MissingPermission")
class MicVolumeListener @Inject constructor() {

    private val bufferSize by lazy {
        AudioRecord.getMinBufferSize(
            SAMPLE_RATE_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
    }

    private val audioRecord: AudioRecord by lazy {
        AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize,
        )
    }

    fun start() {
        audioRecord.startRecording()
    }

    fun volume(samplingMillis: Long = 80): Flow<Float> = flow {
        val buffer = ShortArray(bufferSize)
        var bufferReadSize: Int

        while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            // READ_NON_BLOCKING can cause problems in Samsung Devices
            bufferReadSize = audioRecord.read(
                buffer, 0, bufferSize, AudioRecord.READ_BLOCKING
            )
            if (bufferReadSize <= 0) continue
            val rms = normalizeAudioLevel(buffer, bufferReadSize)
            Log.d(TAG, "mic volume RMS $rms")
            emit(rms.coerceIn(0f, 1f))
            delay(samplingMillis)
        }
    }

    fun stop() {
        audioRecord.stop()
    }

    private fun normalizeAudioLevel(buffer: ShortArray, length: Int): Float {
        if (length <= 0) return 0f

        var sum = 0L
        for (i in 0 until length) {
            sum += (buffer[i] * buffer[i]).toLong()
        }

        val rms = sqrt(sum.toDouble() / length).toFloat()
        return (rms / Short.MAX_VALUE).coerceIn(0f, 1f) * SCALE
    }

    companion object {
        const val TAG = "MicVolumeListener"
        const val SCALE = 10
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val SAMPLE_RATE_HZ = 44100
    }
}
