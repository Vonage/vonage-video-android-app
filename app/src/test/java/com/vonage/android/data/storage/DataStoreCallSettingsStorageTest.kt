package com.vonage.android.data.storage

import androidx.datastore.preferences.core.preferencesOf
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_AUDIO_BITRATE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_BACKGROUND_ID
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_BACKGROUND_PATH
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CAPTURE_FRAME_RATE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CAPTURE_RESOLUTION
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_CODEC_ORDER
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_DEGRADATION_PREFERENCE
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_OPUS_DTX
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISHER_AUDIO_FALLBACK
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISH_AUDIO
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_PUBLISH_VIDEO
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_SENDER_STATS
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_SUBSCRIBER_AUDIO_FALLBACK
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_BITRATE_MAX
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_BITRATE_PRESET
import com.vonage.android.data.storage.GlobalDataStorage.Companion.SETTINGS_VIDEO_EFFECT
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.settings.PersistedCallSettings
import com.vonage.android.settings.PublisherPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DataStoreCallSettingsStorageTest {

    private val dataStore: GlobalDataStorage = mockk(relaxed = true)
    private val sut = DataStoreCallSettingsStorage(dataStore)

    @Test
    fun `load on empty store returns defaults`() = runTest {
        coEvery { dataStore.data } returns flowOf()

        assertEquals(PersistedCallSettings(), sut.load())
    }

    @Test
    fun `load maps all stored values to PersistedCallSettings`() = runTest {
        val prefs = preferencesOf(
            SETTINGS_CAPTURE_FRAME_RATE to CaptureFrameRate.FPS_30.name,
            SETTINGS_CAPTURE_RESOLUTION to CaptureResolution.HIGH.name,
            SETTINGS_CODEC_ORDER to "H264,VP8",
            SETTINGS_AUDIO_BITRATE to 64_000,
            SETTINGS_OPUS_DTX to false,
            SETTINGS_PUBLISHER_AUDIO_FALLBACK to false,
            SETTINGS_SUBSCRIBER_AUDIO_FALLBACK to false,
            SETTINGS_SENDER_STATS to false,
            SETTINGS_VIDEO_BITRATE_PRESET to VideoBitratePreset.CUSTOM.name,
            SETTINGS_VIDEO_BITRATE_MAX to 3_000,
            SETTINGS_DEGRADATION_PREFERENCE to DegradationPreference.MAINTAIN_FRAME_RATE.name,
        )
        coEvery { dataStore.data } returns flowOf(prefs)

        val result = sut.load()

        assertEquals(CaptureFrameRate.FPS_30, result.captureFrameRate)
        assertEquals(CaptureResolution.HIGH, result.captureResolution)
        assertEquals(listOf(VideoCodec.H264, VideoCodec.VP8), result.preferredVideoCodecOrder)
        assertEquals(64_000, result.audioBitrate)
        assertEquals(false, result.opusDtxEnabled)
        assertEquals(false, result.publisherAudioFallbackEnabled)
        assertEquals(false, result.subscriberAudioFallbackEnabled)
        assertEquals(false, result.senderStatsEnabled)
        assertEquals(VideoBitratePreset.CUSTOM, result.videoBitrateConfig.preset)
        assertEquals(3_000, result.videoBitrateConfig.maxBitrate)
        assertEquals(DegradationPreference.MAINTAIN_FRAME_RATE, result.degradationPreference)
    }

    @Test
    fun `load returns null for absent nullable fields`() = runTest {
        val prefs = preferencesOf(
            SETTINGS_CAPTURE_FRAME_RATE to CaptureFrameRate.FPS_15.name,
        )
        coEvery { dataStore.data } returns flowOf(prefs)

        val result = sut.load()

        assertNull(result.captureResolution)
        assertNull(result.preferredVideoCodecOrder)
        assertNull(result.audioBitrate)
    }

    @Test
    fun `save calls dataStore updateData`() = runTest {
        sut.save(PersistedCallSettings())

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun `load maps stored publisher preferences`() = runTest {
        val prefs = preferencesOf(
            SETTINGS_PUBLISH_AUDIO to false,
            SETTINGS_PUBLISH_VIDEO to true,
            SETTINGS_VIDEO_EFFECT to "BACKGROUND_IMAGE",
            SETTINGS_BACKGROUND_ID to "bg-1",
            SETTINGS_BACKGROUND_PATH to "/path/bg-1.jpg",
        )
        coEvery { dataStore.data } returns flowOf(prefs)

        assertEquals(
            PublisherPreferences(
                publishAudio = false,
                publishVideo = true,
                videoEffect = VideoEffect.BackgroundImage(id = "bg-1", imagePath = "/path/bg-1.jpg"),
            ),
            sut.load().publisherPreferences,
        )
    }

    @Test
    fun `load without publisher preferences returns empty preferences`() = runTest {
        coEvery { dataStore.data } returns flowOf(preferencesOf(SETTINGS_VIDEO_EFFECT to "BACKGROUND_IMAGE"))

        assertEquals(PublisherPreferences(), sut.load().publisherPreferences)
    }
}
