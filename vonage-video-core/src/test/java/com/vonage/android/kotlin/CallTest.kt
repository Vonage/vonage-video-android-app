package com.vonage.android.kotlin

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.kotlin.sdk.VonageArchiveListener
import com.vonage.android.kotlin.sdk.VonageCaptionsListener
import com.vonage.android.kotlin.sdk.VonageConnection
import com.vonage.android.kotlin.sdk.VonageError
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageSessionListener
import com.vonage.android.kotlin.sdk.VonageSignalListener
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageVideoType
import com.vonage.android.kotlin.internal.ActiveSpeakerTracker
import com.vonage.android.kotlin.internal.ActiveSpeakerChangedPayload
import com.vonage.android.kotlin.internal.ActiveSpeakerInfo
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.kotlin.signal.RawSignal
import com.vonage.android.kotlin.signal.SignalPlugin
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [Call].
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
class CallTest {

    private val testScheduler = TestCoroutineScheduler()
    // Separate scheduler for Call's internal CoroutineScope so that runTest cleanup
    // does not attempt to process the perpetual sample()/debounce() delay loops.
    private val callScheduler = TestCoroutineScheduler()
    private val callDispatcher = StandardTestDispatcher(callScheduler)
    private val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    private lateinit var mockSession: VonageSession
    private lateinit var mockPublisherFactory: PublisherFactory
    private lateinit var mockContext: Context
    private lateinit var mockVonagePublisher: VonagePublisher
    private lateinit var mockPublisherState: PublisherState

    private var capturedSessionListener: VonageSessionListener? = null
    private var capturedSignalListener: VonageSignalListener? = null
    private var capturedArchiveListener: VonageArchiveListener? = null

    @Before
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

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createCall(
        token: String = "test-token",
        signalPlugins: List<SignalPlugin> = emptyList(),
    ): Call = Call(
        token = token,
        session = mockSession,
        publisherFactory = mockPublisherFactory,
        signalPlugins = signalPlugins,
        coroutineDispatcher = callDispatcher,
    )

    /**
     * Triggers [VonageSessionListener.onConnected] and waits for [Call.publishToSession]
     * (which internally uses [Dispatchers.Default]) to complete, then processes any remaining
     * queued work on [callDispatcher].
     */
    @Suppress("MagicNumber")
    private fun triggerConnectedAndWaitForPublisher() {
        capturedSessionListener!!.onConnected()
        Thread.sleep(200)
        callScheduler.runCurrent()
    }

    private fun runCurrent() = callScheduler.runCurrent()

    // region Session lifecycle

    @Test
    fun `connect should emit Connected event when session connects`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            capturedSessionListener!!.onConnected()
            assertEquals(SessionEvent.Connected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connect should call session connect with token`() = runTest(testDispatcher) {
        val call = createCall(token = "my-token")

        call.connect(mockContext).test {
            verify { mockSession.connect("my-token") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connect should emit Disconnected event when session disconnects`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            capturedSessionListener!!.onDisconnected()
            assertEquals(SessionEvent.Disconnected, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connect should emit Error event on session error`() = runTest(testDispatcher) {
        val call = createCall()
        val error = VonageError(code = 1006, message = "Connection failed")

        call.connect(mockContext).test {
            capturedSessionListener!!.onError(error)
            val event = awaitItem()
            assertTrue(event is SessionEvent.Error)
            assertEquals(error, (event as SessionEvent.Error).error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connect should emit StreamReceived when remote stream arrives`() = runTest(testDispatcher) {
        val call = createCall()
        val stream = createVonageStream("stream-1", "Remote User")
        val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
            every { this@mockk.stream } returns stream
        }
        every { mockSession.subscribe(any(), any()) } returns mockSubscriber

        call.connect(mockContext).test {
            capturedSessionListener!!.onStreamReceived(stream)
            runCurrent()
            assertEquals(SessionEvent.StreamReceived("stream-1"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connect should emit StreamDropped when remote stream is removed`() = runTest(testDispatcher) {
        val call = createCall()
        val stream = createVonageStream("stream-2", "User 2")

        call.connect(mockContext).test {
            capturedSessionListener!!.onStreamDropped(stream)
            runCurrent()
            assertEquals(SessionEvent.StreamDropped("stream-2"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `connect should publish to session on Connected`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            verify { mockPublisherFactory.createPublisherState(mockContext) }
            verify { mockSession.publish(mockVonagePublisher) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pauseSession should delegate to session pause`() = runTest(testDispatcher) {
        val call = createCall()
        call.pauseSession()
        verify { mockSession.pause() }
    }

    @Test
    fun `resumeSession should delegate to session resume`() = runTest(testDispatcher) {
        val call = createCall()
        call.resumeSession()
        verify { mockSession.resume() }
    }

    @Test
    fun `endSession should unpublish, disconnect, and clear listeners`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem() // Connected

            call.endSession()

            verify { mockSession.unpublish(mockVonagePublisher) }
            verify { mockSession.setSessionListener(null) }
            verify { mockSession.setSignalListener(null) }
            verify { mockSession.disconnect() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region Signals

    @Test
    fun `sendChatMessage should route through chat signal plugin`() = runTest(testDispatcher) {
        val chatPlugin = mockk<SignalPlugin>(relaxed = true) {
            every { canHandle("chat") } returns true
            every { sendSignal(any(), any()) } returns RawSignal("chat", "Hello!")
        }
        val call = createCall(signalPlugins = listOf(chatPlugin))

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            call.sendChatMessage("Hello!")

            verify { chatPlugin.sendSignal(any(), "Hello!") }
            verify { mockSession.sendSignal("chat", "Hello!") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sendEmoji should route through reaction signal plugin`() = runTest(testDispatcher) {
        val reactionPlugin = mockk<SignalPlugin>(relaxed = true) {
            every { canHandle("emoji") } returns true
            every { sendSignal(any(), any()) } returns RawSignal("emoji", "👍")
        }
        val call = createCall(signalPlugins = listOf(reactionPlugin))

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            call.sendEmoji("👍")

            verify { reactionPlugin.sendSignal(any(), "👍") }
            verify { mockSession.sendSignal("emoji", "👍") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `incoming signals should be dispatched to matching plugins`() = runTest(testDispatcher) {
        val chatPlugin = mockk<SignalPlugin>(relaxed = true) {
            every { canHandle(any()) } returns true
        }
        val call = createCall(signalPlugins = listOf(chatPlugin))

        call.connect(mockContext).test {
            val remoteConnection = VonageConnection(connectionId = "conn-remote")
            capturedSignalListener!!.onSignalReceived("chat", "Hi there", remoteConnection)

            verify {
                chatPlugin.handleSignal("chat", "Hi there", any(), false)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `incoming signal from self should set isYou to true`() = runTest(testDispatcher) {
        val chatPlugin = mockk<SignalPlugin>(relaxed = true) {
            every { canHandle(any()) } returns true
        }
        val call = createCall(signalPlugins = listOf(chatPlugin))

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            val localConnection = VonageConnection(connectionId = "conn-local")
            capturedSignalListener!!.onSignalReceived("chat", "my message", localConnection)

            verify {
                chatPlugin.handleSignal("chat", "my message", "", true)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `listenUnreadChatMessages should delegate to ChatSignalPlugin`() = runTest(testDispatcher) {
        val chatPlugin = mockk<ChatSignalPlugin>(relaxed = true) {
            every { canHandle(any()) } returns true
        }
        val call = createCall(signalPlugins = listOf(chatPlugin))

        call.listenUnreadChatMessages(true)

        verify { chatPlugin.listenUnread(true) }
    }

    // endregion

    // region Publisher

    @Test
    fun `toggleLocalVideo should delegate to publisher`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            call.toggleLocalVideo()

            verify { mockPublisherState.toggleVideo() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleLocalCamera should delegate to publisher`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            call.toggleLocalCamera()

            verify { mockPublisherState.cycleCamera() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleLocalAudio should delegate to publisher`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            call.toggleLocalAudio()

            verify { mockPublisherState.toggleAudio() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applyLocalVideoEffect should delegate to publisher`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem()

            call.applyLocalVideoEffect(VideoEffect.BlurLow)

            verify { mockPublisherState.applyVideoEffect(VideoEffect.BlurLow) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleLocalVideo without publisher should be no-op`() = runTest(testDispatcher) {
        val call = createCall()

        call.toggleLocalVideo()

        verify(exactly = 0) { mockPublisherState.toggleVideo() }
    }

    // endregion

    // region Pinning

    @Test
    fun `togglePinParticipant twice should unpin`() = runTest(testDispatcher) {
        val call = createCall()

        call.togglePinParticipant("participant-1")
        call.togglePinParticipant("participant-1")

        call.pinnedParticipantIds.test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region Force mute

    @Test
    fun `forceMuteParticipant with unknown id should be no-op`() = runTest(testDispatcher) {
        val call = createCall()
        call.forceMuteParticipant("unknown-id")
        verify(exactly = 0) { mockSession.forceMuteStream(any()) }
    }

    // endregion

    // region Archiving

    @Test
    fun `archiving started event should update archiving state flow`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            capturedArchiveListener!!.onArchiveStarted("archive-1", "MyArchive")

            assertEquals(ArchivingState.Started("archive-1"), call.archivingStateFlow.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `archiving stopped event should update archiving state flow`() = runTest(testDispatcher) {
        val call = createCall()

        call.connect(mockContext).test {
            capturedArchiveListener!!.onArchiveStarted("archive-1", null)
            capturedArchiveListener!!.onArchiveStopped("archive-1")

            assertEquals(ArchivingState.Stopped("archive-1"), call.archivingStateFlow.value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `archiving state should be Idle initially`() = runTest(testDispatcher) {
        val call = createCall()
        assertEquals(ArchivingState.Idle, call.archivingStateFlow.value)
    }

    // endregion

    // region Captions

    @Test
    fun `captions state flow should be empty initially`() = runTest(testDispatcher) {
        val call = createCall()
        assertTrue(call.captionsStateFlow.value.isEmpty())
    }

    @Test
    fun `captionsDelegate should keep line visible immediately after isFinal`() =
        runTest(testDispatcher) {
            val call = createCall()
            val stream = createVonageStream("stream-1", "Alice")
            var capturedCaptionsListener: VonageCaptionsListener? = null
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
                every { setCaptionsListener(any()) } answers { capturedCaptionsListener = firstArg() }
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                capturedSessionListener!!.onStreamReceived(stream)
                runCurrent()
                awaitItem() // StreamReceived

                capturedCaptionsListener!!.onCaption("Alice", "stream-1", "Hello!", isFinal = true)
                runCurrent()

                // Cooldown not elapsed — line must still be visible
                assertEquals(1, call.captionsStateFlow.value.size)
                assertEquals("Hello!", call.captionsStateFlow.value.first().text)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `captionsDelegate should hide line after 2s cooldown`() = runTest(testDispatcher) {
        val call = createCall()
        val stream = createVonageStream("stream-1", "Alice")
        var capturedCaptionsListener: VonageCaptionsListener? = null
        val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
            every { this@mockk.stream } returns stream
            every { setCaptionsListener(any()) } answers { capturedCaptionsListener = firstArg() }
        }
        every { mockSession.subscribe(any(), any()) } returns mockSubscriber

        call.connect(mockContext).test {
            triggerConnectedAndWaitForPublisher()
            awaitItem() // Connected

            capturedSessionListener!!.onStreamReceived(stream)
            runCurrent()
            awaitItem() // StreamReceived

            capturedCaptionsListener!!.onCaption("Alice", "stream-1", "Hello!", isFinal = true)
            runCurrent()

            assertEquals(1, call.captionsStateFlow.value.size)

            // Advance past the 2-second cooldown
            callScheduler.advanceTimeBy(2_001L)

            assertTrue(call.captionsStateFlow.value.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `captionsDelegate should cancel cooldown when new caption arrives for same stream`() =
        runTest(testDispatcher) {
            val call = createCall()
            val stream = createVonageStream("stream-1", "Alice")
            var capturedCaptionsListener: VonageCaptionsListener? = null
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
                every { setCaptionsListener(any()) } answers { capturedCaptionsListener = firstArg() }
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                capturedSessionListener!!.onStreamReceived(stream)
                runCurrent()
                awaitItem() // StreamReceived

                // Final caption starts cooldown
                capturedCaptionsListener!!.onCaption("Alice", "stream-1", "Hello!", isFinal = true)
                runCurrent()

                // New caption arrives before 2s — cancels timer
                capturedCaptionsListener!!.onCaption("Alice", "stream-1", "World!", isFinal = false)
                runCurrent()

                // Advance past the original 2-second window
                callScheduler.advanceTimeBy(2_001L)

                // Line still visible — hide timer was cancelled by the new caption
                assertEquals(1, call.captionsStateFlow.value.size)
                assertEquals("World!", call.captionsStateFlow.value.first().text)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `disableCaptions should cancel all pending timers and clear state immediately`() =
        runTest(testDispatcher) {
            val call = createCall()
            val stream = createVonageStream("stream-1", "Alice")
            var capturedCaptionsListener: VonageCaptionsListener? = null
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
                every { setCaptionsListener(any()) } answers { capturedCaptionsListener = firstArg() }
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                capturedSessionListener!!.onStreamReceived(stream)
                runCurrent()
                awaitItem() // StreamReceived

                capturedCaptionsListener!!.onCaption("Alice", "stream-1", "Hello!", isFinal = true)
                runCurrent()

                assertEquals(1, call.captionsStateFlow.value.size)

                // Disable clears state immediately without waiting for timers
                call.disableCaptions()
                runCurrent()

                assertTrue(call.captionsStateFlow.value.isEmpty())

                // Advance past cooldown — state must remain empty (timer was cancelled)
                callScheduler.advanceTimeBy(2_001L)

                assertTrue(call.captionsStateFlow.value.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }

    // endregion

    // region Subscribers

    @Test
    fun `onStreamReceived should subscribe and add participant`() = runTest(testDispatcher) {
        val call = createCall()
        val stream = createVonageStream("sub-1", "Bob")
        val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
            every { this@mockk.stream } returns stream
        }
        every { mockSession.subscribe(any(), any()) } returns mockSubscriber

        call.connect(mockContext).test {
            capturedSessionListener!!.onStreamReceived(stream)
            runCurrent()

            assertEquals(SessionEvent.StreamReceived("sub-1"), awaitItem())
            verify { mockSession.subscribe(mockContext, stream) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onStreamDropped should remove participant`() = runTest(testDispatcher) {
        val call = createCall()
        val stream = createVonageStream("sub-2", "Alice")
        val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
            every { this@mockk.stream } returns stream
        }
        every { mockSession.subscribe(any(), any()) } returns mockSubscriber

        call.connect(mockContext).test {
            capturedSessionListener!!.onStreamReceived(stream)
            runCurrent()
            awaitItem() // StreamReceived

            capturedSessionListener!!.onStreamDropped(stream)
            runCurrent()

            assertEquals(SessionEvent.StreamDropped("sub-2"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // endregion

    // region Active speaker

    @Test
    fun `activeSpeaker should be null initially`() = runTest(testDispatcher) {
        val call = createCall()
        assertNull(call.activeSpeaker.value)
    }

    // endregion

    // region Initial state

    @Test
    fun `initial participantsStateFlow should be empty`() = runTest(testDispatcher) {
        val call = createCall()
        assertTrue(call.participantsStateFlow.value.isEmpty())
    }

    @Test
    fun `initial signalStateFlow should be null`() = runTest(testDispatcher) {
        val call = createCall()
        assertNull(call.signalStateFlow.value)
    }

    // endregion

    // region Visibility

    @Test
    fun `updateParticipantVisibilityFlow should never call changeVisibility on publisher`() =
        runTest(testDispatcher) {
            val call = createCall()

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                // Snapshot flow that does NOT include PUBLISHER_ID — simulates the publisher
                // tile being off-screen (e.g. scrolled off thumbnail strip in SPEAKER_LAYOUT).
                val visibilityFlow = MutableStateFlow(listOf("some-remote-id"))
                call.updateParticipantVisibilityFlow(visibilityFlow)
                Thread.sleep(200)
                runCurrent()

                // Publisher's changeVisibility must never be called by the visibility system.
                verify(exactly = 0) { mockPublisherState.changeVisibility(any()) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    // endregion

    // region Stream property changes (Bug A)

    @Test
    fun `onStreamPropertyChanged with unknown streamId should be a no-op`() =
        runTest(testDispatcher) {
            val call = createCall()

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                // Firing a property change for a stream that was never subscribed must not throw
                // and must not change any observable state.
                capturedSessionListener!!.onStreamPropertyChanged(
                    streamId = "unknown-stream",
                    hasVideo = false,
                    hasAudio = false,
                )
                runCurrent()

                // Participant count must remain 1 (only the publisher)
                assertEquals(1, call.participantsCount.value)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onStreamPropertyChanged with known streamId should update participant mic state`() =
        runTest(testDispatcher) {
            val call = createCall()
            val stream = createVonageStream("sub-1", "Bob")
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            call.connect(mockContext).test {
                triggerConnectedAndWaitForPublisher()
                awaitItem() // Connected

                // Subscribe to participantsStateFlow so its upstream (sample) becomes active.
                val participantsCollectorJob = launch {
                    call.participantsStateFlow.collect { }
                }

                capturedSessionListener!!.onStreamReceived(stream)
                Thread.sleep(200)
                callScheduler.runCurrent()
                awaitItem() // StreamReceived

                // Advance past the 100ms sample on participantsStateFlow so the subscriber
                // appears in the emitted list.
                callScheduler.advanceTimeBy(200)
                runCurrent()

                val participant = call.participantsStateFlow.value
                    .filterIsInstance<ParticipantState>()
                    .firstOrNull { it.id == "sub-1" }
                assertNotNull("Subscriber must appear in participantsStateFlow", participant)
                assertTrue("Mic should initially be enabled", participant!!.isMicEnabled.value)

                // Simulate remote publisher muting their mic.
                capturedSessionListener!!.onStreamPropertyChanged("sub-1", hasVideo = true, hasAudio = false)

                assertFalse(
                    "isMicEnabled must be false after onStreamPropertyChanged(hasAudio=false)",
                    participant.isMicEnabled.value,
                )

                participantsCollectorJob.cancel()
                cancelAndIgnoreRemainingEvents()
            }
        }

    // endregion

    // region Active speaker promotion gate (Bug D)

    @Test
    fun `activeSpeaker should not be promoted when camera is off`() =
        runTest(testDispatcher) {
            // extraBufferCapacity = 1 so tryEmit can buffer without suspending (avoids a
            // deadlock between the test-body coroutine and the callDispatcher collector).
            val mockActiveSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>(
                extraBufferCapacity = 1,
            )
            val mockTracker = mockk<ActiveSpeakerTracker>(relaxed = true) {
                every { activeSpeakerChanges } returns mockActiveSpeakerChanges
            }
            val call = createCallWithTracker(mockTracker)
            val stream = createVonageStream("sub-camera-off", "NoCamera", hasVideo = false)
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            // Start collecting in backgroundScope so capturedSessionListener is set eagerly
            // and the collection coroutine is auto-cancelled when the test body finishes.
            backgroundScope.launch { call.connect(mockContext).collect { } }

            // Drive the session lifecycle.
            capturedSessionListener!!.onConnected()
            Thread.sleep(200)
            callScheduler.runCurrent()

            // Subscribe a camera-off participant.
            capturedSessionListener!!.onStreamReceived(stream)
            Thread.sleep(200)
            callScheduler.runCurrent()

            // Emit an active-speaker change for the camera-off participant; tryEmit is
            // non-suspending so there is no deadlock with callScheduler processing.
            assertTrue(
                mockActiveSpeakerChanges.tryEmit(
                    ActiveSpeakerChangedPayload(
                        previousActiveSpeaker = ActiveSpeakerInfo(null, 0f),
                        newActiveSpeaker      = ActiveSpeakerInfo("sub-camera-off", 0.8f),
                    ),
                ),
            )
            callScheduler.runCurrent()

            // Camera-off participant must NOT be promoted; activeSpeaker stays null.
            assertNull(
                "Camera-off participant must not be promoted to active speaker",
                call.activeSpeaker.value,
            )
        }

    @Test
    fun `activeSpeaker should be promoted when camera is on`() =
        runTest(testDispatcher) {
            val mockActiveSpeakerChanges = MutableSharedFlow<ActiveSpeakerChangedPayload>(
                extraBufferCapacity = 1,
            )
            val mockTracker = mockk<ActiveSpeakerTracker>(relaxed = true) {
                every { activeSpeakerChanges } returns mockActiveSpeakerChanges
            }
            val call = createCallWithTracker(mockTracker)
            val stream = createVonageStream("sub-camera-on", "WithCamera", hasVideo = true)
            val mockSubscriber = mockk<VonageSubscriber>(relaxed = true) {
                every { this@mockk.stream } returns stream
            }
            every { mockSession.subscribe(any(), any()) } returns mockSubscriber

            backgroundScope.launch { call.connect(mockContext).collect { } }

            capturedSessionListener!!.onConnected()
            Thread.sleep(200)
            callScheduler.runCurrent()

            capturedSessionListener!!.onStreamReceived(stream)
            Thread.sleep(200)
            callScheduler.runCurrent()

            assertTrue(
                mockActiveSpeakerChanges.tryEmit(
                    ActiveSpeakerChangedPayload(
                        previousActiveSpeaker = ActiveSpeakerInfo(null, 0f),
                        newActiveSpeaker      = ActiveSpeakerInfo("sub-camera-on", 0.8f),
                    ),
                ),
            )
            callScheduler.runCurrent()
            // Advance past the activeSpeaker debounce (250ms).
            callScheduler.advanceTimeBy(300)
            callScheduler.runCurrent()

            assertNotNull(
                "Camera-on participant must be promoted to active speaker",
                call.activeSpeaker.value,
            )
            assertEquals("sub-camera-on", call.activeSpeaker.value?.id)
        }

    // endregion

    // region Helpers

    private fun createVonageStream(
        streamId: String,
        name: String,
        videoType: VonageVideoType = VonageVideoType.CAMERA,
        hasVideo: Boolean = true,
        hasAudio: Boolean = true,
    ): VonageStream = VonageStream(
        streamId = streamId,
        name = name,
        connection = VonageConnection(connectionId = "conn-$streamId"),
        creationTime = System.currentTimeMillis(),
        videoType = videoType,
        hasVideo = hasVideo,
        hasAudio = hasAudio,
    )

    private fun createCallWithTracker(tracker: ActiveSpeakerTracker): Call = Call(
        token = "test-token",
        session = mockSession,
        publisherFactory = mockPublisherFactory,
        signalPlugins = emptyList(),
        coroutineDispatcher = callDispatcher,
        activeSpeakerTrackerOverride = tracker,
    )

    // endregion
}
