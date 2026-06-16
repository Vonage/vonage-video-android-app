package com.vonage.android.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.settings.CallSettingsHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Integration test verifying that the shared CallSettingsHolder instance
 * properly propagates settings changes to an active call.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MeetingRoomSettingsIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var callSettingsHolder: CallSettingsHolder

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_sharedCallSettingsHolder_when_videoBitrateChanged_then_activeCallReceivesUpdate() {
        // Given: A mock call bound to the shared CallSettingsHolder
        val mockCall: CallFacade = mockk(relaxed = true)
        callSettingsHolder.bind(mockCall)

        // When: Video bitrate config is changed
        val customConfig = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 5_000
        )
        callSettingsHolder.updateVideoBitrateConfig(customConfig)

        // Then: The active call receives the update
        verify { mockCall.setVideoBitrate(customConfig) }
    }

    @Test
    fun given_sharedCallSettingsHolder_when_degradationPreferenceChanged_then_activeCallReceivesUpdate() {
        // Given: A mock call bound to the shared CallSettingsHolder
        val mockCall: CallFacade = mockk(relaxed = true)
        callSettingsHolder.bind(mockCall)

        // When: Degradation preference is changed
        callSettingsHolder.updateDegradationPreference(DegradationPreference.MAINTAIN_FRAME_RATE)

        // Then: The active call receives the update
        verify { mockCall.setDegradationPreference(DegradationPreference.MAINTAIN_FRAME_RATE) }
    }

    @Test
    fun given_sharedCallSettingsHolder_when_callCleared_then_onlyCallReferenceIsCleared() {
        // Given: A bound call with custom settings
        val mockCall: CallFacade = mockk(relaxed = true)
        callSettingsHolder.bind(mockCall)
        callSettingsHolder.updateSenderStatsEnabled(false)
        val customConfig = VideoBitrateConfig(
            preset = VideoBitratePreset.CUSTOM,
            maxBitrate = 8_000
        )
        callSettingsHolder.updateVideoBitrateConfig(customConfig)

        // When: Call is cleared
        callSettingsHolder.clearCall()

        // Then: Call reference is null but settings persist
        assertNull(callSettingsHolder.call.value)
        assertFalse(callSettingsHolder.senderStatsEnabled.value)
        assertEquals(customConfig, callSettingsHolder.videoBitrateConfig.value)
    }
}
