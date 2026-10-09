package com.vonage.android.kotlin

import android.content.Context
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.sdk.VonageArchiveListener
import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageSessionListener
import com.vonage.android.kotlin.sdk.VonageSignalListener
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageVideoType
import com.vonage.android.kotlin.signal.SignalPlugin
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Shared infrastructure for [Call] unit tests.
 *
 * Uses a two-dispatcher strategy:
 * - [StandardTestDispatcher] for Call's internal CoroutineScope (prevents infinite loops
 *   from sample()/debounce() operators when advancing virtual time).
 * - [UnconfinedTestDispatcher] for tests & Dispatchers.Main (ensures callbackFlow body
 *   runs eagerly so listeners are captured before test assertions).
 *
 * [runCurrent] is used instead of advanceUntilIdle to avoid processing perpetual delay ticks
 * from ActiveSpeakerTracker's sample() and activeSpeaker's debounce().
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class CallTestBase {

    val testScheduler = TestCoroutineScheduler()
    // Separate scheduler for Call's internal CoroutineScope so that runTest cleanup
    // does not attempt to process the perpetual sample()/debounce() delay loops.
    val callScheduler = TestCoroutineScheduler()
    val callDispatcher = StandardTestDispatcher(callScheduler)
    val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    // Matches Call.ACTIVE_SPEAKER_DEBOUNCE_MILLIS (private in production code)
    val activeSpeakerDebounceMillis = 250L

    lateinit var mockSession: VonageSession
    lateinit var mockPublisherFactory: PublisherFactory
    lateinit var mockContext: Context
    lateinit var mockVonagePublisher: VonagePublisher
    lateinit var mockPublisherState: PublisherState

    var capturedSessionListener: VonageSessionListener? = null
    var capturedSignalListener: VonageSignalListener? = null
    var capturedArchiveListener: VonageArchiveListener? = null

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        capturedSessionListener = null
        capturedSignalListener = null
        capturedArchiveListener = null

        mockContext = mockk(relaxed = true)
        mockVonagePublisher = mockk(relaxed = true)
        mockPublisherState = mockk(relaxed = true) {
            every { id } returns Call.PUBLISHER_ID
            every { vonagePublisher } returns mockVonagePublisher
            every { connectionId } returns "conn-local"
            every { isPublisher } returns true
            every { creationTime } returns 1000L
        }
        mockPublisherFactory = mockk(relaxed = true) {
            every { createPublisherState(any()) } returns mockPublisherState
        }
        mockSession = mockk(relaxed = true) {
            every { setSessionListener(any()) } answers { capturedSessionListener = firstArg() }
            every { setSignalListener(any()) } answers { capturedSignalListener = firstArg() }
            every { setArchiveListener(any()) } answers { capturedArchiveListener = firstArg() }
            every { connect(any()) } just Runs
            every { publish(any()) } just Runs
            every { unpublish(any()) } just Runs
            every { disconnect() } just Runs
            every { pause() } just Runs
            every { resume() } just Runs
            every { sendSignal(any(), any()) } just Runs
        }
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    fun createCall(
        token: String = "test-token",
        signalPlugins: List<SignalPlugin> = emptyList(),
    ): Call = Call(
        token = token,
        session = mockSession,
        publisherFactory = mockPublisherFactory,
        signalPlugins = signalPlugins,
        coroutineDispatcher = callDispatcher,
    )

    fun createCallWithTracker(tracker: ActiveSpeakerTracker): Call = Call(
        token = "test-token",
        session = mockSession,
        publisherFactory = mockPublisherFactory,
        signalPlugins = emptyList(),
        coroutineDispatcher = callDispatcher,
        activeSpeakerTrackerOverride = tracker,
    )

    fun createVonageStream(
        streamId: String,
        name: String,
        videoType: VonageVideoType = VonageVideoType.CAMERA,
        hasVideo: Boolean = true,
        hasAudio: Boolean = true,
        creationTime: Long = System.currentTimeMillis(),
    ): VonageStream = VonageStream(
        streamId = streamId,
        name = name,
        connection = VonageConnection(connectionId = "conn-$streamId"),
        creationTime = creationTime,
        videoType = videoType,
        hasVideo = hasVideo,
        hasAudio = hasAudio,
    )

    /**
     * Triggers [VonageSessionListener.onConnected] and waits for [Call.publishToSession]
     * (which internally uses [Dispatchers.Default]) to complete, then processes any remaining
     * queued work on [callDispatcher].
     */
    @Suppress("MagicNumber")
    fun triggerConnectedAndWaitForPublisher() {
        capturedSessionListener!!.onConnected()
        Thread.sleep(200)
        callScheduler.runCurrent()
    }

    fun runCurrent() = callScheduler.runCurrent()
}
