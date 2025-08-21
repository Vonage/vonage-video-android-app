package com.vonage.android.kotlin.internal

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
import java.nio.ByteBuffer
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.Volatile

class VeraAudioDevice(
    private val context: Context,
    private val audioFocusRequester: AudioFocusRequester = AudioFocusRequester,
    private val veraBluetoothManager: VeraBluetoothManager = VeraBluetoothManager(context),
) : BaseAudioDevice() {

    private var audioTrack: AudioTrack? = null
    private var audioRecord: AudioRecord? = null

    // Capture & render buffers
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

    // Capturing delay estimation
    private var estimatedCaptureDelay = 0

    // Rendering delay estimation
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
        } catch (e: Exception) {
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
                            AudioRecord.ERROR_BAD_VALUE -> throw RuntimeException("captureThread(): AudioRecord.ERROR_BAD_VALUE")
                            AudioRecord.ERROR_INVALID_OPERATION -> throw RuntimeException("captureThread(): AudioRecord.ERROR_INVALID_OPERATION")
                            AudioRecord.ERROR -> throw RuntimeException("captureThread(): AudioRecord.ERROR or default")
                            else -> throw RuntimeException("captureThread(): AudioRecord.ERROR or default")
                        }
                    }
                }
            } catch (e: Exception) {
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
        } catch (e: Exception) {
            Log.e(TAG, "Process.setThreadPriority(): " + e.message)
        }
        while (!shutdownRenderThread) {
            rendererLock.lock()
            try {
                if (!isRendering) {
                    renderEvent.await()
                } else {
                    rendererLock.unlock()
                    // Don't lock on audioBus calls
                    playBuffer?.clear()
                    val samplesRead = audioBus.readRenderData(playBuffer, samplesToPlay)
                    rendererLock.lock()
                    // After acquiring the lock again we must check if we are still playing
                    if (audioTrack == null || !isRendering) {
                        continue
                    }
                    val bytesRead: Int = (samplesRead shl 1) * NUM_CHANNELS_RENDERING
                    playBuffer?.get(tempBufPlay, 0, bytesRead)

                    val bytesWritten = audioTrack!!.write(tempBufPlay, 0, bytesRead)

                    if (bytesWritten > 0) {
                        // increase by number of written samples
                        bufferedPlaySamples += (bytesWritten shr 1) / NUM_CHANNELS_RENDERING

                        // decrease by number of played samples
                        val pos = audioTrack!!.playbackHeadPosition

                        if (pos < playPosition) {
                            // wrap or reset by driver
                            playPosition = 0
                        }

                        bufferedPlaySamples -= (pos - playPosition)
                        playPosition = pos

                        // we calculate the estimated delay based on the buffered samples
                        estimatedRenderDelay = bufferedPlaySamples * 1000 / outputSamplingRate
                    } else {
                        when (bytesWritten) {
                            AudioTrack.ERROR_BAD_VALUE -> throw RuntimeException("renderThread(): AudioTrack.ERROR_BAD_VALUE")
                            AudioTrack.ERROR_INVALID_OPERATION -> throw RuntimeException("renderThread(): AudioTrack.ERROR_INVALID_OPERATION")
                            AudioTrack.ERROR -> throw RuntimeException("renderThread(): AudioTrack.ERROR or default")
                            else -> throw RuntimeException("renderThread(): AudioTrack.ERROR or default")
                        }
                    }
                }
            } catch (e: Exception) {
                throw RuntimeException(e.message)
            } finally {
                rendererLock.unlock()
            }
        }
    }

    init {
        try {
            recBuffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }
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
            Log.e(TAG, "DefaultAudioDevice(): " + numberFormatException.message)
        } finally {
            if (outputBufferSize == 0) {
                outputBufferSize = DEFAULT_BUFFER_SIZE
                samplesPerBuffer = DEFAULT_SAMPLES_PER_BUFFER
            }
        }

        try {
            playBuffer = ByteBuffer.allocateDirect(outputBufferSize)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }

        tempBufPlay = ByteArray(outputBufferSize)

        captureSettings = AudioSettings(captureSamplingRate, NUM_CHANNELS_CAPTURING)
        rendererSettings = AudioSettings(outputSamplingRate, NUM_CHANNELS_RENDERING)

        isPaused = false
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun initCapturer(): Boolean {
        val minRecBufSize = AudioRecord.getMinBufferSize(
            captureSettings.getSampleRate(),
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        // double size to be more safe
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

        // Check that the audioRecord is ready to be used.
        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "Audio capture could not be initialized")
            throw RuntimeException("Audio capture could not be initialized")
        }

        shutdownCaptureThread = false
        Thread(captureThread).start()
        return true
    }

    //region Capturer
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

    override fun destroyCapturer(): Boolean {
        Log.d(TAG, "Destroy capturer")
        captureLock.lock()
        // release echo canceler
        echoCanceler?.release()
        echoCanceler = null
        // release noise suppressor
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
    override fun initRenderer(): Boolean {
        Log.d(TAG, "Init Renderer")
        if (audioFocusRequester.request(audioManager)) {
            Log.d(TAG, "Audio Focus request GRANTED !")
        } else {
            Log.e(TAG, "Audio Focus request DENIED !")
        }

        createAudioTrack()

        bufferedPlaySamples = 0
        shutdownRenderThread = false
        Thread(renderThread).start()
        return true
    }

    override fun startRenderer(): Boolean {
        Log.d(TAG, "Start Renderer")
        veraBluetoothManager.onInitRenderer()

        checkNotNull(audioTrack) { "startRenderer(): play() called on uninitialized AudioTrack" }
        audioTrack?.play()
        rendererLock.lock()
        isRendering = true
        renderEvent.signal()
        rendererLock.unlock()
        return true
    }

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

        veraBluetoothManager.onStopRenderer()
        return true
    }

    override fun destroyRenderer(): Boolean {
        Log.d(TAG, "Destroy Renderer")
        destroyAudioTrack()
        veraBluetoothManager.onDestroyRenderer()
        return true
    }
    //endregion

    override fun getEstimatedCaptureDelay(): Int = estimatedCaptureDelay

    override fun getEstimatedRenderDelay(): Int = estimatedRenderDelay

    override fun getCaptureSettings(): AudioSettings = captureSettings

    override fun getRenderSettings(): AudioSettings = rendererSettings

    override fun getBluetoothState(): BluetoothState? = null

    override fun setOutputMode(mode: OutputMode?): Boolean {
        Log.d(TAG, "OutputMode set to : $mode")
        return true
    }

    @Synchronized
    override fun onPause() {
        veraBluetoothManager.onPause()
        isPaused = true
    }

    @Synchronized
    override fun onResume() {
        Log.d(TAG, "onResume() called")
        if (!isPaused) {
            return
        }
        veraBluetoothManager.onResume()
        isPaused = false
    }

    /**
     * Force stop Bluetooth and prevent automatic restart
     * Called when user explicitly selects non-Bluetooth audio device
     */
    fun forceStopBluetooth() {
        veraBluetoothManager.forceStopBluetooth()
    }

    /**
     * Re-enable Bluetooth management and start it
     * Called when user explicitly selects Bluetooth device
     */
    fun enableBluetoothManagement() {
        veraBluetoothManager.enableBluetoothManagement()
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
            // use AudioTrack.Builder
            audioTrack = AudioTrack(
                AudioManager.STREAM_VOICE_CALL,
                rendererSettings.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                if (minPlayBufSize >= 6000) minPlayBufSize else minPlayBufSize * 2,
                AudioTrack.MODE_STREAM
            )
        } catch (e: Exception) {
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
