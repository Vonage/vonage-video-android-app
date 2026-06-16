package com.vonage.android.settings

import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CallSettingsHolderTest {

    private val sut = CallSettingsHolder()

    // region defaults

    @Test
    fun `initial state has expected defaults`() {
        assertNull(sut.call.value)
        assertTrue(sut.senderStatsEnabled.value)
        assertTrue(sut.opusDtxEnabled.value)
        assertEquals(VideoBitratePreset.DEFAULT, sut.videoBitrateConfig.value.preset)
        assertEquals(DegradationPreference.NOT_SET, sut.degradationPreference.value)
        assertEquals(CaptureFrameRate.FPS_15, sut.captureFrameRate.value)
        assertNull(sut.captureResolution.value)
        assertTrue(sut.publisherAudioFallbackEnabled.value)
        assertTrue(sut.subscriberAudioFallbackEnabled.value)
    }

    // endregion

    // region update methods

    @Test
    fun `when updateSenderStatsEnabled then value is updated`() {
        sut.updateSenderStatsEnabled(false)
        assertEquals(false, sut.senderStatsEnabled.value)
    }

    @Test
    fun `when updateOpusDtx then value is updated`() {
        sut.updateOpusDtx(false)
        assertEquals(false, sut.opusDtxEnabled.value)
    }

    @Test
    fun `when updateVideoBitrateConfig then value is updated`() {
        val config = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 5_000,
        )
        sut.updateVideoBitrateConfig(config)
        assertEquals(config, sut.videoBitrateConfig.value)
    }

    @Test
    fun `when updateVideoBitrateConfig with bound call then delegates to call`() {
        val call: CallFacade = mockk(relaxed = true)
        sut.bind(call)

        val config = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 3_000,
        )
        sut.updateVideoBitrateConfig(config)

        verify { call.setVideoBitrate(config) }
    }

    @Test
    fun `when updateVideoBitrateConfig without bound call then does not throw`() {
        val config = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 3_000,
        )
        sut.updateVideoBitrateConfig(config)
        assertEquals(config, sut.videoBitrateConfig.value)
    }

    @Test
    fun `when updateDegradationPreference then value is updated`() {
        sut.updateDegradationPreference(DegradationPreference.MAINTAIN_FRAME_RATE)
        assertEquals(DegradationPreference.MAINTAIN_FRAME_RATE, sut.degradationPreference.value)
    }

    @Test
    fun `when updateDegradationPreference with bound call then delegates to call`() {
        val call: CallFacade = mockk(relaxed = true)
        sut.bind(call)

        sut.updateDegradationPreference(DegradationPreference.MAINTAIN_RESOLUTION)

        verify { call.setDegradationPreference(DegradationPreference.MAINTAIN_RESOLUTION) }
    }

    @Test
    fun `when updateCaptureFrameRate then value is updated`() {
        sut.updateCaptureFrameRate(CaptureFrameRate.FPS_30)
        assertEquals(CaptureFrameRate.FPS_30, sut.captureFrameRate.value)
    }

    @Test
    fun `when updateCaptureResolution then value is updated`() {
        sut.updateCaptureResolution(CaptureResolution.HIGH)
        assertEquals(CaptureResolution.HIGH, sut.captureResolution.value)
    }

    @Test
    fun `when updateCaptureResolution with null then value is null`() {
        sut.updateCaptureResolution(CaptureResolution.MEDIUM)
        sut.updateCaptureResolution(null)
        assertNull(sut.captureResolution.value)
    }

    @Test
    fun `when updatePublisherAudioFallback then value is updated`() {
        sut.updatePublisherAudioFallback(false)
        assertEquals(false, sut.publisherAudioFallbackEnabled.value)
    }

    @Test
    fun `when updateSubscriberAudioFallback then value is updated`() {
        sut.updateSubscriberAudioFallback(false)
        assertEquals(false, sut.subscriberAudioFallbackEnabled.value)
    }

    // endregion

    // region bind

    @Test
    fun `when bind then call is set`() {
        val call: CallFacade = mockk(relaxed = true)
        sut.bind(call)
        assertEquals(call, sut.call.value)
    }

    // endregion

    // region clearCall

    @Test
    fun `when clearCall then only call reference is cleared`() {
        val call: CallFacade = mockk(relaxed = true)
        sut.bind(call)
        sut.updateSenderStatsEnabled(false)
        sut.updateOpusDtx(false)
        val customConfig = VideoBitrateConfig(preset = VideoBitratePreset.CUSTOM, maxBitrate = 9_999)
        sut.updateVideoBitrateConfig(customConfig)
        sut.updateDegradationPreference(DegradationPreference.MAINTAIN_FRAME_RATE)
        sut.updateCaptureFrameRate(CaptureFrameRate.FPS_30)
        sut.updateCaptureResolution(CaptureResolution.HIGH_1080P)
        sut.updatePublisherAudioFallback(false)
        sut.updateSubscriberAudioFallback(false)

        sut.clearCall()

        // Call reference is cleared
        assertNull(sut.call.value)
        // But user preferences remain
        assertEquals(false, sut.senderStatsEnabled.value)
        assertEquals(false, sut.opusDtxEnabled.value)
        assertEquals(customConfig, sut.videoBitrateConfig.value)
        assertEquals(DegradationPreference.MAINTAIN_FRAME_RATE, sut.degradationPreference.value)
        assertEquals(CaptureFrameRate.FPS_30, sut.captureFrameRate.value)
        assertEquals(CaptureResolution.HIGH_1080P, sut.captureResolution.value)
        assertEquals(false, sut.publisherAudioFallbackEnabled.value)
        assertEquals(false, sut.subscriberAudioFallbackEnabled.value)
    }

    // endregion
}
