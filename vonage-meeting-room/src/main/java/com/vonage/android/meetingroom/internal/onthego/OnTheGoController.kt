package com.vonage.android.meetingroom.internal.onthego

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal fun interface TimeoutHandle {
    fun cancel()
}

internal fun interface TimeoutScheduler {
    fun schedule(delayMs: Long, action: () -> Unit): TimeoutHandle
}

internal fun mainThreadTimeoutScheduler(): TimeoutScheduler = TimeoutScheduler { delayMs, action ->
    val handler = Handler(Looper.getMainLooper())
    val runnable = Runnable { action() }
    handler.postDelayed(runnable, delayMs)
    TimeoutHandle { handler.removeCallbacks(runnable) }
}

/**
 * State machine for the audio-first On-the-Go call mode.
 *
 * The host pushes call state through [update] and supplies [OnTheGoCallActions].
 * Entering turns the camera off and mutes the mic. Exiting restores the camera
 * only when it was on at entry and is still off. The mic is never restored.
 * Decline or exit suppresses further suggestions until the next connect.
 */
internal class OnTheGoController(
    val configuration: OnTheGoConfiguration,
    private val preferences: OnTheGoPreferences,
    private val motionSource: MotionActivitySource?,
    actions: OnTheGoCallActions,
    private val detector: OnTheGoMotionDetector = OnTheGoMotionDetector(),
    private val scheduleTimeout: TimeoutScheduler = mainThreadTimeoutScheduler(),
) {
    private val _state = MutableStateFlow(OnTheGoState.IDLE)
    val state: StateFlow<OnTheGoState> = _state.asStateFlow()

    private val _snapshot = MutableStateFlow(OnTheGoCallSnapshot.disconnected)
    val snapshot: StateFlow<OnTheGoCallSnapshot> = _snapshot.asStateFlow()

    private val _sharedContentPresented = MutableStateFlow(false)
    val isSharedContentPresented: StateFlow<Boolean> = _sharedContentPresented.asStateFlow()

    var isSuggestionSuppressedForThisCall: Boolean = false
        private set

    var actions: OnTheGoCallActions = actions

    private val preferenceListener: (Boolean) -> Unit = { reevaluateMotionDetection() }
    private var timeoutHandle: TimeoutHandle? = null
    private var motionRunning = false
    private var cameraWasEnabledOnEntry = false

    init {
        detector.onSustainedMotion = { handleSustainedMotion() }
        preferences.addListener(preferenceListener)
        reevaluateMotionDetection()
    }

    fun update(snapshot: OnTheGoCallSnapshot) {
        val wasConnected = _snapshot.value.isConnected
        _snapshot.value = snapshot
        when {
            wasConnected && !snapshot.isConnected -> handleDisconnect()
            !wasConnected && snapshot.isConnected -> isSuggestionSuppressedForThisCall = false
        }
        if (_sharedContentPresented.value && snapshot.screenShare == null) {
            _sharedContentPresented.value = false
        }
        reevaluateMotionDetection()
    }

    fun enterManually() {
        enter()
    }

    fun acceptSuggestion() {
        enter()
    }

    fun declineSuggestion() {
        cancelTimeout()
        _state.value = OnTheGoState.IDLE
        isSuggestionSuppressedForThisCall = true
        reevaluateMotionDetection()
    }

    fun exit() {
        if (_state.value != OnTheGoState.ACTIVE) return
        restoreCameraIfNeeded()
        cameraWasEnabledOnEntry = false
        _sharedContentPresented.value = false
        _state.value = OnTheGoState.IDLE
        isSuggestionSuppressedForThisCall = true
        reevaluateMotionDetection()
    }

    fun toggleMic() {
        actions.toggleMic()
    }

    fun presentSharedContent() {
        if (_snapshot.value.screenShare == null) return
        _sharedContentPresented.value = true
    }

    fun dismissSharedContent() {
        _sharedContentPresented.value = false
    }

    fun leaveCall() {
        actions.endCall()
    }

    fun dispose() {
        preferences.removeListener(preferenceListener)
        preferences.close()
        cancelTimeout()
        stopMotion()
    }

    private fun handleSustainedMotion() {
        val suggest = shouldDetectMotion() && _snapshot.value.isCameraEnabled
        if (!suggest) return
        detector.reset()
        _state.value = OnTheGoState.SUGGESTED
        timeoutHandle = scheduleTimeout.schedule(configuration.suggestionTimeoutMs) {
            declineSuggestion()
        }
        reevaluateMotionDetection()
    }

    private fun enter() {
        val current = _snapshot.value
        if (!current.isConnected) return
        cancelTimeout()
        cameraWasEnabledOnEntry = current.isCameraEnabled
        if (current.isCameraEnabled) actions.toggleCamera()
        if (current.isMicEnabled) actions.toggleMic()
        _state.value = OnTheGoState.ACTIVE
        reevaluateMotionDetection()
    }

    private fun restoreCameraIfNeeded() {
        if (cameraWasEnabledOnEntry && !_snapshot.value.isCameraEnabled) {
            actions.toggleCamera()
        }
    }

    private fun handleDisconnect() {
        cancelTimeout()
        detector.reset()
        cameraWasEnabledOnEntry = false
        _sharedContentPresented.value = false
        isSuggestionSuppressedForThisCall = false
        _state.value = OnTheGoState.IDLE
    }

    private fun reevaluateMotionDetection() {
        val shouldRun = shouldDetectMotion()
        if (shouldRun && !motionRunning) {
            startMotion()
        } else if (!shouldRun && motionRunning) {
            stopMotion()
        }
    }

    private fun shouldDetectMotion(): Boolean {
        if (!configurationGatesOpen()) return false
        if (!callIsReadyForSuggestion()) return false
        return motionSource?.isAvailable == true
    }

    private fun configurationGatesOpen(): Boolean {
        val featureAllowsSuggestion = configuration.isEnabled && configuration.autoSuggest
        val userAllowsSuggestion = preferences.suggestWhenMoving && !isSuggestionSuppressedForThisCall
        return featureAllowsSuggestion && userAllowsSuggestion
    }

    private fun callIsReadyForSuggestion(): Boolean {
        return _snapshot.value.isConnected && _state.value == OnTheGoState.IDLE
    }

    private fun startMotion() {
        val source = motionSource ?: return
        motionRunning = true
        source.start { sample -> detector.ingest(sample) }
    }

    private fun stopMotion() {
        motionRunning = false
        detector.reset()
        motionSource?.stop()
    }

    private fun cancelTimeout() {
        timeoutHandle?.cancel()
        timeoutHandle = null
    }
}
