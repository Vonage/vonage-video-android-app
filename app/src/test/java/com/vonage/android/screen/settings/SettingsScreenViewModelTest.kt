package com.vonage.android.screen.settings

import android.content.Context
import app.cash.turbine.test
import com.vonage.android.MainDispatcherRule
import com.vonage.android.data.ClientLogsRepository
import com.vonage.android.data.network.APIService
import com.vonage.android.data.storage.ClientLogsSettingsStorage
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.logger.DefaultVonageLogger
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SettingsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val callSettingsHolder = CallSettingsHolder()
    private val apiService: APIService = mockk()
    private val context: Context = mockk()
    private val clientLogsSettingsStorage: ClientLogsSettingsStorage = mockk(relaxed = true)
    private lateinit var filesDir: File
    private lateinit var clientLogsRepository: ClientLogsRepository

    @Before
    fun setUp() {
        filesDir = Files.createTempDirectory("settings-view-model").toFile()
        every { context.filesDir } returns filesDir
        DefaultVonageLogger.setEnabled(false)
        coEvery { clientLogsSettingsStorage.getLogsEnabled() } returns null
        coEvery { clientLogsSettingsStorage.getLogLevel() } returns null
        clientLogsRepository = ClientLogsRepository(context, apiService, clientLogsSettingsStorage)
    }

    @After
    fun tearDown() {
        DefaultVonageLogger.setEnabled(false)
        filesDir.deleteRecursively()
    }

    private fun sut() = SettingsScreenViewModel(
        appVersion = APP_VERSION,
        sdkVersion = SDK_VERSION,
        callSettingsHolder = callSettingsHolder,
        clientLogsRepository = clientLogsRepository,
    )

    // region initial state

    @Test
    fun `given viewmodel when created then state has app and sdk version`() = runTest {
        val sut = sut()
        sut.state.test {
            val state = awaitItem()
            assertEquals(APP_VERSION, state.appVersion)
            assertEquals(SDK_VERSION, state.sdkVersion)
        }
    }

    @Test
    fun `given viewmodel when created then state has default settings`() = runTest {
        val sut = sut()
        sut.state.test {
            val state = awaitItem()
            assertTrue(state.senderStatsEnabled)
            assertEquals(false, state.logsEnabled)
            assertTrue(state.opusDtxEnabled)
            assertTrue(state.publisherAudioFallbackEnabled)
            assertTrue(state.subscriberAudioFallbackEnabled)
            assertEquals(DegradationPreference.NOT_SET, state.degradationPreference)
            assertEquals(CaptureFrameRate.FPS_15, state.captureFrameRate)
            assertNull(state.captureResolution)
            assertNull(state.call)
        }
    }

    // endregion

    // region toggle actions

    @Test
    fun `when toggleSenderStatsTrack then state is updated`() = runTest {
        val sut = sut()
        sut.toggleSenderStatsTrack(false)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(false, awaitItem().senderStatsEnabled)
        }
    }

    @Test
    fun `when toggleOpusDtx then state is updated`() = runTest {
        val sut = sut()
        sut.toggleOpusDtx(false)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(false, awaitItem().opusDtxEnabled)
        }
    }

    @Test
    fun `when toggleLogs then state is updated`() = runTest {
        val sut = sut()
        sut.toggleLogs(false)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(false, awaitItem().logsEnabled)
        }
    }

    @Test
    fun `when togglePublisherAudioFallback then state is updated`() = runTest {
        val sut = sut()
        sut.togglePublisherAudioFallback(false)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(false, awaitItem().publisherAudioFallbackEnabled)
        }
    }

    @Test
    fun `when toggleSubscriberAudioFallback then state is updated`() = runTest {
        val sut = sut()
        sut.toggleSubscriberAudioFallback(false)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(false, awaitItem().subscriberAudioFallbackEnabled)
        }
    }

    // endregion

    // region update actions

    @Test
    fun `when updateVideoBitrateConfig then state is updated`() = runTest {
        val config = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 5_000,
        )
        val sut = sut()
        sut.updateVideoBitrateConfig(config)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(config, awaitItem().videoBitrateConfig)
        }
    }

    @Test
    fun `when updateDegradationPreference then state is updated`() = runTest {
        val sut = sut()
        sut.updateDegradationPreference(DegradationPreference.MAINTAIN_FRAME_RATE)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(DegradationPreference.MAINTAIN_FRAME_RATE, awaitItem().degradationPreference)
        }
    }

    @Test
    fun `when updateCaptureFrameRate then state is updated`() = runTest {
        val sut = sut()
        sut.updateCaptureFrameRate(CaptureFrameRate.FPS_30)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(CaptureFrameRate.FPS_30, awaitItem().captureFrameRate)
        }
    }

    @Test
    fun `when updateCaptureResolution then state is updated`() = runTest {
        val sut = sut()
        sut.updateCaptureResolution(CaptureResolution.HIGH)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertEquals(CaptureResolution.HIGH, awaitItem().captureResolution)
        }
    }

    @Test
    fun `when updateCaptureResolution with null then state is null`() = runTest {
        val sut = sut()
        sut.updateCaptureResolution(CaptureResolution.MEDIUM)
        mainDispatcherRule.testScheduler.advanceUntilIdle()
        sut.updateCaptureResolution(null)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            assertNull(awaitItem().captureResolution)
        }
    }

    // endregion

    // region observation from holder

    @Test
    fun `given holder updated externally then state reflects change`() = runTest {
        val sut = sut()
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        callSettingsHolder.updateSenderStatsEnabled(false)
        callSettingsHolder.updateOpusDtx(false)
        callSettingsHolder.updatePublisherAudioFallback(false)
        callSettingsHolder.updateSubscriberAudioFallback(false)
        clientLogsRepository.setLogsEnabled(false)
        callSettingsHolder.updateCaptureFrameRate(CaptureFrameRate.FPS_30)
        callSettingsHolder.updateCaptureResolution(CaptureResolution.HIGH_1080P)
        callSettingsHolder.updateDegradationPreference(DegradationPreference.MAINTAIN_RESOLUTION)
        mainDispatcherRule.testScheduler.advanceUntilIdle()

        sut.state.test {
            val state = awaitItem()
            assertEquals(false, state.senderStatsEnabled)
            assertEquals(false, state.logsEnabled)
            assertEquals(false, state.opusDtxEnabled)
            assertEquals(false, state.publisherAudioFallbackEnabled)
            assertEquals(false, state.subscriberAudioFallbackEnabled)
            assertEquals(CaptureFrameRate.FPS_30, state.captureFrameRate)
            assertEquals(CaptureResolution.HIGH_1080P, state.captureResolution)
            assertEquals(DegradationPreference.MAINTAIN_RESOLUTION, state.degradationPreference)
        }
    }

    // endregion

    companion object {
        private const val APP_VERSION = "1.0.0"
        private const val SDK_VERSION = "2.28.0"
    }
}
