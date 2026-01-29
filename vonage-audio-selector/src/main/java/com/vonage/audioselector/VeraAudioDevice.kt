package com.vonage.audioselector

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.os.Process
import android.util.Log
import androidx.annotation.RequiresPermission
import com.opentok.android.BaseAudioDevice
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.ReadOnlyBufferException
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.Volatile

/**
 * Custom audio device implementation for Vonage Video SDK.
 *
 * Provides low-level audio capture and rendering with acoustic echo cancellation
 * and noise suppression. Manages separate threads for capturing (recording) and
 * rendering (playback) audio streams.
 *
 * Based on OpenTok Advanced Audio Driver sample but simplified to remove Bluetooth
 * and audio output routing management.
 *
 * Features:
 * - Acoustic echo cancellation when available
 * - Noise suppression when available
 * - Dual-thread architecture for capture and render
 * - Delay estimation for audio sync
 * - Dynamic buffer sizing based on device capabilities
 *
 * @param context Android context for accessing AudioManager
 *
 * @see <a href="https://github.com/opentok/opentok-android-sdk-samples/blob/main/Advanced-Audio-Driver-Kotlin/app/src/main/java/com/tokbox/sample/advancedaudiodriver/AdvancedAudioDevice.java">Original implementation</a>
 */
@Suppress("MagicNumber", "SwallowedException", "TooGenericExceptionThrown", "LoopWithTooManyJumpStatements")
class VeraAudioDevice(
    context: Context,
) : BaseAudioDevice() {

    private var audioTrack: AudioTrack? = null
    private var audioRecord: AudioRecord? = null

    /** Capture and render buffers */
    private var playBuffer: ByteBuffer? = null
    private var recBuffer: ByteBuffer? = null
    private var tempBufPlay: ByteArray
    private var tempBufRec: ByteArray

    private val rendererLock = ReentrantLock(true)
    private val renderEvent: Condition = rendererLock.newCondition()

    @Volatile
    private var isRendering = false

    @Volatile
    private var shutdownRenderThread = false

    private val captureLock = ReentrantLock(true)
    private val captureEvent: Condition = captureLock.newCondition()

    @Volatile
    private var isCapturing = false

    @Volatile
    private var shutdownCaptureThread = false

    private val captureSettings: AudioSettings
    private val rendererSettings: AudioSettings
    private var noiseSuppressor: NoiseSuppressor? = null
    private var echoCanceler: AcousticEchoCanceler? = null

    /** Capturing delay estimation in milliseconds */
    private var estimatedCaptureDelay = 0

    /** Rendering delay estimation - tracks buffered samples */
    private var bufferedPlaySamples = 0
    private var playPosition = 0
    private var estimatedRenderDelay = 0

    private val audioManager: AudioManager

    private var outputSamplingRate: Int = DEFAULT_SAMPLE_RATE
    private val captureSamplingRate: Int = DEFAULT_SAMPLE_RATE
    private var samplesPerBuffer: Int = DEFAULT_SAMPLES_PER_BUFFER

    private var isPaused: Boolean

    private val captureThread = Runnable {
        val samplesToRec = captureSamplingRate / 100
        var samplesRead: Int

        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        } catch (e: SecurityException) {
            Log.e(TAG, "Process.setThreadPriority(): " + e.message)
        }
        while (!shutdownCaptureThread) {
            captureLock.lock()
            try {
                if (!isCapturing) {
                    captureEvent.await()
                    continue
                } else {
                    if (audioRecord == null) {
                        continue
                    }
                    val lengthInBytes: Int = (samplesToRec shl 1) * NUM_CHANNELS_CAPTURING
                    val readBytes = audioRecord!!.read(tempBufRec, 0, lengthInBytes)
                    if (readBytes >= 0) {
                        recBuffer!!.rewind()
                        recBuffer!!.put(tempBufRec)
                        samplesRead = (readBytes shr 1) / NUM_CHANNELS_CAPTURING
                    } else {
                        when (readBytes) {
                            AudioRecord.ERROR_BAD_VALUE ->
                                throw RuntimeException("captureThread(): AudioRecord.ERROR_BAD_VALUE")

                            AudioRecord.ERROR_INVALID_OPERATION ->
                                throw RuntimeException("captureThread(): AudioRecord.ERROR_INVALID_OPERATION")

                            AudioRecord.ERROR -> throw RuntimeException("captureThread(): AudioRecord.ERROR or default")
                            else -> throw RuntimeException("captureThread(): AudioRecord.ERROR or default")
                        }
                    }
                }
            } catch (e: BufferOverflowException) {
                throw RuntimeException(e.message)
            } catch (e: ReadOnlyBufferException) {
                throw RuntimeException(e.message)
            } finally {
                captureLock.unlock()
            }
            audioBus.writeCaptureData(recBuffer, samplesRead)
            estimatedCaptureDelay = samplesRead * 1000 / captureSamplingRate
        }
    }

    private val renderThread = Runnable {
        val samplesToPlay = samplesPerBuffer
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
        } catch (e: SecurityException) {
            Log.e(TAG, "Process.setThreadPriority(): " + e.message)
        }
        while (!shutdownRenderThread) {
            rendererLock.lock()
            try {
                if (!isRendering) {
                    renderEvent.await()
                } else {
                    rendererLock.unlock()
                    // Don't lock during audioBus calls
                    playBuffer?.clear()
                    val samplesRead = audioBus.readRenderData(playBuffer, samplesToPlay)
                    rendererLock.lock()
                    // Check if still playing after reacquiring lock
                    if (audioTrack == null || !isRendering) {
                        continue
                    }
                    val bytesRead: Int = (samplesRead shl 1) * NUM_CHANNELS_RENDERING
                    playBuffer?.get(tempBufPlay, 0, bytesRead)

                    val bytesWritten = audioTrack!!.write(tempBufPlay, 0, bytesRead)

                    if (bytesWritten > 0) {
                        // Increase by number of written samples
                        bufferedPlaySamples += (bytesWritten shr 1) / NUM_CHANNELS_RENDERING

                        // Decrease by number of played samples
                        val pos = audioTrack!!.playbackHeadPosition

                        if (pos < playPosition) {
                            // Wrap or reset by driver
                            playPosition = 0
                        }

                        bufferedPlaySamples -= (pos - playPosition)
                        playPosition = pos

                        // Calculate estimated delay based on buffered samples
                        estimatedRenderDelay = bufferedPlaySamples * 1000 / outputSamplingRate
                    } else {
                        when (bytesWritten) {
                            AudioTrack.ERROR_BAD_VALUE ->
                                throw RuntimeException("renderThread(): AudioTrack.ERROR_BAD_VALUE")

                            AudioTrack.ERROR_INVALID_OPERATION ->
                                throw RuntimeException("renderThread(): AudioTrack.ERROR_INVALID_OPERATION")

                            AudioTrack.ERROR -> throw RuntimeException("renderThread(): AudioTrack.ERROR or default")
                            else -> throw RuntimeException("renderThread(): AudioTrack.ERROR or default")
                        }
                    }
                }
            } catch (e: InterruptedException) {
                throw RuntimeException(e.message)
            } finally {
                rendererLock.unlock()
            }
        }
    }

    init {
        recBuffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE.coerceAtLeast(0))
        tempBufRec = ByteArray(DEFAULT_BUFFER_SIZE)
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        var outputBufferSize: Int = DEFAULT_BUFFER_SIZE

        try {
            outputSamplingRate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()
        } finally {
            if (outputSamplingRate == 0) {
                outputSamplingRate = DEFAULT_SAMPLE_RATE
            }
        }
        try {
            samplesPerBuffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER).toInt()
            outputBufferSize = (SAMPLE_SIZE_IN_BYTES * samplesPerBuffer * NUM_CHANNELS_RENDERING)
        } catch (numberFormatException: NumberFormatException) {
            Log.e(TAG, "VeraAudioDevice(): " + numberFormatException.message)
        } finally {
            if (outputBufferSize == 0) {
                outputBufferSize = DEFAULT_BUFFER_SIZE
                samplesPerBuffer = DEFAULT_SAMPLES_PER_BUFFER
            }
        }

        playBuffer = ByteBuffer.allocateDirect(outputBufferSize.coerceAtLeast(0))

        tempBufPlay = ByteArray(outputBufferSize)

        captureSettings = AudioSettings(captureSamplingRate, NUM_CHANNELS_CAPTURING)
        rendererSettings = AudioSettings(outputSamplingRate, NUM_CHANNELS_RENDERING)

        isPaused = false
    }

    /**
     * Initializes the audio capturer (microphone).
     *
     * Creates AudioRecord instance with noise suppression and echo cancellation
     * if available on the device. Starts the capture thread for processing audio.
     *
     * @return True if initialization succeeded
     * @throws RuntimeException if AudioRecord cannot be initialized
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun initCapturer(): Boolean {
        val minRecBufSize = AudioRecord.getMinBufferSize(
            captureSettings.getSampleRate(),
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        // Double size for safety margin
        val recBufSize = minRecBufSize * 2

        noiseSuppressor?.release()
        noiseSuppressor = null

        echoCanceler?.release()
        echoCanceler = null

        audioRecord?.release()
        audioRecord = null

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            captureSettings.getSampleRate(),
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            recBufSize
        )

        if (NoiseSuppressor.isAvailable()) {
            audioRecord?.let { noiseSuppressor = NoiseSuppressor.create(it.audioSessionId) }
        }

        if (AcousticEchoCanceler.isAvailable()) {
            audioRecord?.let { echoCanceler = AcousticEchoCanceler.create(it.audioSessionId) }
        }

        // Verify AudioRecord is ready to use
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "Audio capture could not be initialized")
            throw RuntimeException("Audio capture could not be initialized")
        }

        shutdownCaptureThread = false
        Thread(captureThread).start()
        return true
    }

    //region Capturer
    /**
     * Starts audio capture from the microphone.
     *
     * @return True if capture started successfully
     */
    override fun startCapturer(): Boolean {
        Log.d(TAG, "Start capturer")
        checkNotNull(audioRecord) { "startCapturer(): startRecording() called on an uninitialized AudioRecord" }
        audioRecord?.startRecording()
        captureLock.lock()
        isCapturing = true
        captureEvent.signal()
        captureLock.unlock()
        return true
    }

    /**
     * Stops audio capture from the microphone.
     *
     * @return True if capture stopped successfully
     */
    override fun stopCapturer(): Boolean {
        Log.d(TAG, "Stop capturer")
        checkNotNull(audioRecord) { "stopCapturer(): stop() called on an uninitialized AudioRecord" }
        captureLock.lock()
        if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord?.stop()
        }
        isCapturing = false
        captureLock.unlock()
        return true
    }

    /**
     * Destroys the audio capturer and releases all resources.
     *
     * Releases echo canceler, noise suppressor, and AudioRecord instance.
     *
     * @return True if destruction succeeded
     */
    override fun destroyCapturer(): Boolean {
        Log.d(TAG, "Destroy capturer")
        captureLock.lock()
        // Release echo canceler
        echoCanceler?.release()
        echoCanceler = null
        // Release noise suppressor
        noiseSuppressor?.release()
        noiseSuppressor = null

        audioRecord?.release()
        audioRecord = null
        shutdownCaptureThread = true
        captureEvent.signal()
        captureLock.unlock()
        return true
    }
    //endregion

    //region Renderer
    /**
     * Initializes the audio renderer (speaker).
     *
     * Creates AudioTrack instance and starts the render thread for audio playback.
     *
     * @return True if initialization succeeded
     */
    override fun initRenderer(): Boolean {
        Log.d(TAG, "Init Renderer")
        createAudioTrack()
        bufferedPlaySamples = 0
        shutdownRenderThread = false
        Thread(renderThread).start()
        return true
    }

    /**
     * Starts audio rendering to the speaker.
     *
     * @return True if rendering started successfully
     */
    override fun startRenderer(): Boolean {
        Log.d(TAG, "Start Renderer")
        checkNotNull(audioTrack) { "startRenderer(): play() called on uninitialized AudioTrack" }
        audioTrack?.play()
        rendererLock.lock()
        isRendering = true
        renderEvent.signal()
        rendererLock.unlock()
        return true
    }

    /**
     * Stops audio rendering to the speaker.
     *
     * @return True if rendering stopped successfully
     */
    override fun stopRenderer(): Boolean {
        Log.d(TAG, "Stop Renderer")
        checkNotNull(audioTrack) { "stopRenderer(): stop() called on uninitialized AudioTrack" }
        rendererLock.lock()
        if (audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack?.stop()
        }
        audioTrack?.flush()
        isRendering = false
        rendererLock.unlock()
        return true
    }

    /**
     * Destroys the audio renderer and releases all resources.
     *
     * @return True if destruction succeeded
     */
    override fun destroyRenderer(): Boolean {
        Log.d(TAG, "Destroy Renderer")
        destroyAudioTrack()
        return true
    }
    //endregion

    /**
     * Gets the estimated capture delay in milliseconds.
     *
     * @return Estimated delay for audio capture
     */
    override fun getEstimatedCaptureDelay(): Int = estimatedCaptureDelay

    /**
     * Gets the estimated render delay in milliseconds.
     *
     * @return Estimated delay for audio rendering
     */
    override fun getEstimatedRenderDelay(): Int = estimatedRenderDelay

    /**
     * Gets the audio settings for capture.
     *
     * @return AudioSettings containing sample rate and channel configuration
     */
    override fun getCaptureSettings(): AudioSettings = captureSettings

    /**
     * Gets the audio settings for rendering.
     *
     * @return AudioSettings containing sample rate and channel configuration
     */
    override fun getRenderSettings(): AudioSettings = rendererSettings

    /**
     * Called when the app is paused.
     */
    override fun onPause() {
        Log.d(TAG, "onPause() called")
        isPaused = true
    }

    /**
     * Called when the app is resumed.
     */
    override fun onResume() {
        Log.d(TAG, "onResume() called")
        if (!isPaused) {
            return
        }
        isPaused = false
    }

    private fun createAudioTrack() {
        // get the minimum buffer size that can be used
        val minPlayBufSize = AudioTrack.getMinBufferSize(
            rendererSettings.getSampleRate(),
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        if (audioTrack != null) {
            audioTrack?.release()
            audioTrack = null
        }

        try {
            audioTrack = AudioTrack(
                AudioManager.STREAM_VOICE_CALL,
                rendererSettings.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                if (minPlayBufSize >= 6000) minPlayBufSize else minPlayBufSize * 2,
                AudioTrack.MODE_STREAM
            )
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(e.message)
        }

        // Check that the audioRecord is ready to be used.
        if (audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
            throw RuntimeException("Audio renderer not initialized " + rendererSettings.getSampleRate())
        }
    }

    private fun destroyAudioTrack() {
        rendererLock.lock()
        audioTrack?.release()
        audioTrack = null
        shutdownRenderThread = true
        renderEvent.signal()
        rendererLock.unlock()
    }

    companion object {
        const val TAG = "VeraAudioDevice"

        const val NUM_CHANNELS_CAPTURING = 1
        const val NUM_CHANNELS_RENDERING = 1
        const val STEREO_CHANNELS = 2
        const val DEFAULT_SAMPLE_RATE = 44100
        const val SAMPLE_SIZE_IN_BYTES = 2
        const val DEFAULT_SAMPLES_PER_BUFFER: Int = (DEFAULT_SAMPLE_RATE / 1000) * 10 // 10ms
        const val DEFAULT_BUFFER_SIZE: Int = SAMPLE_SIZE_IN_BYTES * DEFAULT_SAMPLES_PER_BUFFER * STEREO_CHANNELS
    }
}