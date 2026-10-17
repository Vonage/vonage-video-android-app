package com.vonage.android.meetingroom.internal.onthego

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class OnTheGoMotionDetectorTest {

    @Test
    fun given_movingSamplesSpanTheThreshold_THEN_firesOnce() {
        val detector = OnTheGoMotionDetector(thresholdMs = THRESHOLD)
        var fired = 0
        detector.onSustainedMotion = { fired += 1 }

        detector.ingest(moving(at = 0))
        detector.ingest(moving(at = THRESHOLD))
        detector.ingest(moving(at = THRESHOLD + THRESHOLD))

        assertEquals(1, fired)
        assertTrue(detector.hasFired)
    }

    @Test
    fun given_lowConfidenceOrStillness_THEN_runResets() {
        val detector = OnTheGoMotionDetector(thresholdMs = THRESHOLD)
        var fired = 0
        detector.onSustainedMotion = { fired += 1 }

        detector.ingest(moving(at = 0))
        detector.ingest(MotionSample(MotionSample.Kind.WALKING, MotionSample.Confidence.LOW, timestampMs = THRESHOLD))
        detector.ingest(moving(at = THRESHOLD + THRESHOLD))
        detector.ingest(still(at = 0))
        detector.ingest(moving(at = THRESHOLD - 1))

        assertEquals(0, fired)
    }

    @Test
    fun given_reset_THEN_canFireAgain() {
        val detector = OnTheGoMotionDetector(thresholdMs = THRESHOLD)
        var fired = 0
        detector.onSustainedMotion = { fired += 1 }
        detector.ingest(moving(at = 0))
        detector.ingest(moving(at = THRESHOLD))
        detector.reset()
        detector.ingest(moving(at = 0))
        detector.ingest(moving(at = THRESHOLD))

        assertEquals(2, fired)
    }

    private fun moving(at: Long) = MotionSample(MotionSample.Kind.WALKING, MotionSample.Confidence.HIGH, at)

    private fun still(at: Long) = MotionSample(MotionSample.Kind.STATIONARY, MotionSample.Confidence.HIGH, at)

    private companion object {
        const val THRESHOLD = 20_000L
    }
}

class OnTheGoCallSnapshotTest {

    @Test
    fun given_screenShareAndLocalUser_THEN_countAndSpeakerExcludeThem() {
        val snapshot = onTheGoSnapshot(
            isConnected = true,
            isMicEnabled = true,
            isCameraEnabled = true,
            participants = listOf(
                OnTheGoParticipantFacts("local", "Me", isPublisher = true, isScreenShare = false, isTalking = true),
                OnTheGoParticipantFacts("share", "Screen", isPublisher = false, isScreenShare = true, isTalking = true),
                OnTheGoParticipantFacts("bob", "Bob", isPublisher = false, isScreenShare = false, isTalking = true),
                OnTheGoParticipantFacts("cara", "Cara", isPublisher = false, isScreenShare = false, isTalking = false),
            ),
        )

        assertEquals(2, snapshot.participantCount)
        assertEquals("Bob", snapshot.activeSpeakerName)
        assertEquals("share", snapshot.screenShare?.id)
    }

    @Test
    fun given_onlyTheLocalShare_THEN_sharedContentIsHidden() {
        val snapshot = onTheGoSnapshot(
            isConnected = true,
            isMicEnabled = false,
            isCameraEnabled = false,
            participants = listOf(
                OnTheGoParticipantFacts("local-share", "Me", isPublisher = true, isScreenShare = true, isTalking = false),
            ),
        )

        assertEquals(0, snapshot.participantCount)
        assertNull(snapshot.activeSpeakerName)
        assertNull(snapshot.screenShare)
    }

    @Test
    fun given_shareSheetClosed_THEN_remoteCameraVideoStaysUnsubscribed() {
        assertFalse(
            subscribesOnTheGoVideo(isScreenShare = false, participantId = "bob", visibleShareId = null),
        )
        assertFalse(
            subscribesOnTheGoVideo(isScreenShare = true, participantId = "share", visibleShareId = null),
        )
        assertTrue(
            subscribesOnTheGoVideo(isScreenShare = true, participantId = "share", visibleShareId = "share"),
        )
    }
}

class MotionWindowClassifierTest {

    @Test
    fun given_aQuietWindow_THEN_sampleIsStationary() {
        val classifier = MotionWindowClassifier(windowMs = WINDOW)
        repeat(SAMPLES) { index ->
            classifier.onMagnitude(timeMs = index * STEP, magnitude = 0.1f)
        }
        val sample = classifier.onMagnitude(timeMs = WINDOW, magnitude = 0.1f)

        assertEquals(MotionSample.Kind.STATIONARY, sample?.kind)
        assertFalse(sample?.isMoving == true)
    }

    @Test
    fun given_enoughStrongSamples_THEN_sampleIsWalking() {
        val classifier = MotionWindowClassifier(windowMs = WINDOW)
        repeat(SAMPLES) { index ->
            classifier.onMagnitude(timeMs = index * STEP, magnitude = 2f)
        }
        val sample = classifier.onMagnitude(timeMs = WINDOW, magnitude = 2f)

        assertEquals(MotionSample.Kind.WALKING, sample?.kind)
        assertEquals(MotionSample.Confidence.MEDIUM, sample?.confidence)
        assertTrue(sample?.isMoving == true)
    }

    @Test
    fun given_accelerometerIncludesGravity_THEN_firstReadingIsIgnoredAndSteadyGravityIsStill() {
        val extractor = LinearAccelerationExtractor()

        assertEquals(0f, extractor.linearMagnitude(x = 0f, y = 0f, z = GRAVITY, includesGravity = true))
        val settled = extractor.linearMagnitude(x = 0f, y = 0f, z = GRAVITY, includesGravity = true)

        assertTrue(settled < MOVING_FLOOR)
    }

    @Test
    fun given_linearAcceleration_THEN_magnitudeIsPassedThrough() {
        val extractor = LinearAccelerationExtractor()
        val magnitude = extractor.linearMagnitude(x = 3f, y = 4f, z = 0f, includesGravity = false)

        assertEquals(sqrt(3f * 3f + 4f * 4f), magnitude)
    }

    private companion object {
        const val WINDOW = 1_000L
        const val STEP = 100L
        const val SAMPLES = 10
        const val GRAVITY = 9.8f
        const val MOVING_FLOOR = 0.5f
    }
}

class OnTheGoControllerTest {

    @Test
    fun given_sustainedMotionWithCameraOn_THEN_suggestsAndStopsDetection() {
        val harness = Harness()
        harness.connect()

        assertTrue(harness.motion.running)
        harness.emitMoving(at = 0)
        harness.emitMoving(at = THRESHOLD)

        assertEquals(OnTheGoState.SUGGESTED, harness.controller.state.value)
        assertFalse(harness.motion.running)
    }

    @Test
    fun given_cameraOff_THEN_motionDoesNotSuggest() {
        val harness = Harness()
        harness.connect(camera = false)
        harness.emitMoving(at = 0)
        harness.emitMoving(at = THRESHOLD)

        assertEquals(OnTheGoState.IDLE, harness.controller.state.value)
    }

    @Test
    fun given_suggestionTimesOut_THEN_declinesForThisCall() {
        val harness = Harness()
        harness.connect()
        harness.emitMoving(at = 0)
        harness.emitMoving(at = THRESHOLD)

        harness.scheduler.fire()

        assertEquals(OnTheGoState.IDLE, harness.controller.state.value)
        assertTrue(harness.controller.isSuggestionSuppressedForThisCall)
        assertFalse(harness.motion.running)
    }

    @Test
    fun given_accept_THEN_cameraTurnsOffAndMicMutes() {
        val harness = Harness()
        harness.connect()
        harness.controller.acceptSuggestion()

        assertEquals(listOf("camera", "mic"), harness.events)
        assertEquals(OnTheGoState.ACTIVE, harness.controller.state.value)
    }

    @Test
    fun given_micAndCameraAlreadyOff_THEN_enterDoesNotToggleThem() {
        val harness = Harness()
        harness.connect(mic = false, camera = false)
        harness.controller.enterManually()

        assertTrue(harness.events.isEmpty())
        assertEquals(OnTheGoState.ACTIVE, harness.controller.state.value)
    }

    @Test
    fun given_exitAfterEntryWithCameraOn_THEN_restoresCameraAndLeavesMicMuted() {
        val harness = Harness()
        harness.connect()
        harness.controller.enterManually()
        harness.connect(mic = false, camera = false)

        harness.controller.exit()

        assertEquals(listOf("camera", "mic", "camera"), harness.events)
        assertEquals(OnTheGoState.IDLE, harness.controller.state.value)
        assertTrue(harness.controller.isSuggestionSuppressedForThisCall)
    }

    @Test
    fun given_cameraTurnedBackOnWhileActive_THEN_exitLeavesItOn() {
        val harness = Harness()
        harness.connect()
        harness.controller.enterManually()
        harness.connect(mic = false, camera = true)

        harness.controller.exit()

        assertEquals(listOf("camera", "mic"), harness.events)
    }

    @Test
    fun given_autoSuggestOff_THEN_manualEntryStillWorks() {
        val harness = Harness(configuration = OnTheGoConfiguration(autoSuggest = false))
        harness.connect()

        assertFalse(harness.motion.running)
        harness.controller.enterManually()

        assertEquals(OnTheGoState.ACTIVE, harness.controller.state.value)
    }

    @Test
    fun given_preferenceTurnsOff_THEN_detectionStopsBeforeTheSetterReturns() {
        val harness = Harness()
        harness.connect()
        assertTrue(harness.motion.running)

        harness.preferences.suggestWhenMoving = false

        assertFalse(harness.motion.running)
    }

    @Test
    fun given_disconnect_THEN_modeResets() {
        val harness = Harness()
        harness.connect(participants = listOf(share()))
        harness.controller.enterManually()
        harness.controller.presentSharedContent()

        harness.controller.update(OnTheGoCallSnapshot.disconnected)

        assertEquals(OnTheGoState.IDLE, harness.controller.state.value)
        assertFalse(harness.controller.isSharedContentPresented.value)
        assertFalse(harness.controller.isSuggestionSuppressedForThisCall)
    }

    @Test
    fun given_declineThenANewCall_THEN_suggestionIsAllowedAgain() {
        val harness = Harness()
        harness.connect()
        harness.emitMoving(at = 0)
        harness.emitMoving(at = THRESHOLD)
        harness.controller.declineSuggestion()
        harness.controller.update(OnTheGoCallSnapshot.disconnected)

        harness.connect()

        assertFalse(harness.controller.isSuggestionSuppressedForThisCall)
        assertTrue(harness.motion.running)
    }

    @Test
    fun given_notConnected_THEN_enterDoesNothing() {
        val harness = Harness()
        harness.controller.enterManually()

        assertTrue(harness.events.isEmpty())
        assertEquals(OnTheGoState.IDLE, harness.controller.state.value)
    }

    @Test
    fun given_shareDisappears_THEN_sheetCloses() {
        val harness = Harness()
        harness.connect(participants = listOf(share()))
        harness.controller.presentSharedContent()
        harness.connect()

        assertFalse(harness.controller.isSharedContentPresented.value)
    }

    @Test
    fun given_featureDisabled_THEN_motionNeverStarts() {
        val harness = Harness(configuration = OnTheGoConfiguration(isEnabled = false))
        harness.connect()
        harness.emitMoving(at = 0)
        harness.emitMoving(at = THRESHOLD)

        assertFalse(harness.motion.running)
        assertEquals(OnTheGoState.IDLE, harness.controller.state.value)
    }

    private class Harness(
        configuration: OnTheGoConfiguration = OnTheGoConfiguration(),
        val preferences: InMemoryPreferences = InMemoryPreferences(),
        val motion: FakeMotionSource = FakeMotionSource(),
        detector: OnTheGoMotionDetector = OnTheGoMotionDetector(thresholdMs = THRESHOLD),
    ) {
        val events = mutableListOf<String>()
        val scheduler = ManualScheduler()
        val controller = OnTheGoController(
            configuration = configuration,
            preferences = preferences,
            motionSource = motion,
            actions = OnTheGoCallActions(
                toggleMic = { events += "mic" },
                toggleCamera = { events += "camera" },
                endCall = { events += "end" },
            ),
            detector = detector,
            scheduleTimeout = scheduler,
        )

        fun connect(
            mic: Boolean = true,
            camera: Boolean = true,
            participants: List<OnTheGoParticipantFacts> = emptyList(),
        ) {
            controller.update(
                onTheGoSnapshot(
                    isConnected = true,
                    isMicEnabled = mic,
                    isCameraEnabled = camera,
                    participants = participants,
                ),
            )
        }

        fun emitMoving(at: Long) {
            motion.emit(MotionSample(MotionSample.Kind.WALKING, MotionSample.Confidence.HIGH, at))
        }
    }

    private class InMemoryPreferences(initial: Boolean = true) : OnTheGoPreferences {
        private val listeners = linkedSetOf<(Boolean) -> Unit>()
        override var suggestWhenMoving: Boolean = initial
            set(value) {
                field = value
                listeners.toList().forEach { listener -> listener(value) }
            }

        override fun addListener(listener: (Boolean) -> Unit) {
            listeners += listener
        }

        override fun removeListener(listener: (Boolean) -> Unit) {
            listeners -= listener
        }
    }

    private class FakeMotionSource : MotionActivitySource {
        override val isAvailable: Boolean = true
        var running: Boolean = false
        private var handler: ((MotionSample) -> Unit)? = null

        override fun start(handler: (MotionSample) -> Unit) {
            running = true
            this.handler = handler
        }

        override fun stop() {
            running = false
            this.handler = null
        }

        fun emit(sample: MotionSample) {
            handler?.invoke(sample)
        }
    }

    private class ManualScheduler : TimeoutScheduler {
        private var action: (() -> Unit)? = null

        override fun schedule(delayMs: Long, action: () -> Unit): TimeoutHandle {
            this.action = action
            return TimeoutHandle { this.action = null }
        }

        fun fire() {
            val pending = action
            action = null
            pending?.invoke()
        }
    }

    private fun share() = OnTheGoParticipantFacts(
        id = "share",
        name = "Screen",
        isPublisher = false,
        isScreenShare = true,
        isTalking = false,
    )

    private companion object {
        const val THRESHOLD = 20_000L
    }
}
