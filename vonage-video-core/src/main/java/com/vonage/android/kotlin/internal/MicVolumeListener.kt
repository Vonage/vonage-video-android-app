package com.vonage.android.kotlin.internal

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.sqrt

/**
 * Utility class to listen directly to the device microphone volume.
 * It's used in waiting room to show the volume indicator,
 * the conference room it's listening to the SDK AudioLevelListener
 */
@SuppressLint("MissingPermission")
class MicVolumeListener {

    private val bufferSize by lazy {
        AudioRecord.getMinBufferSize(
            SAMPLE_RATE_HZ,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
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

    /**
     * Starts listening to microphone volume and emits normalized audio levels.
     *
     * Creates a flow that continuously reads from the microphone and calculates
     * RMS (Root Mean Square) values, normalized to 0.0-1.0 range.
     *
     * @param samplingMillis Delay between volume samples in milliseconds (default 60ms)
     * @return Flow emitting normalized audio level values (0.0 to 1.0)
     */
    fun start(samplingMillis: Long = 60): Flow<Float> = flow {
        audioRecord.startRecording()

        val buffer = ShortArray(bufferSize)
        var bufferReadSize: Int

        while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            // READ_NON_BLOCKING can cause problems in Samsung Devices
            bufferReadSize = audioRecord.read(
                buffer, 0, bufferSize, AudioRecord.READ_BLOCKING
            )
            if (bufferReadSize <= 0) continue
            val rms = normalizeAudioLevel(buffer, bufferReadSize)
            vonageLogger.d(TAG, "mic volume RMS $rms")
            emit(rms.coerceIn(0f, 1f))
            delay(samplingMillis)
        }
    }

    /**
     * Stops listening to microphone volume.
     *
     * Stops the AudioRecord and ends the volume monitoring flow.
     */
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