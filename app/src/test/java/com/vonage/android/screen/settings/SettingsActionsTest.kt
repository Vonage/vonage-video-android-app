package com.vonage.android.screen.settings

import com.vonage.android.core.ActionScope
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.settings.SettingsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsActionsTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())
    private val callSettingsHolder = CallSettingsHolder(scope = testScope)

    private fun actionScope(
        initialState: SettingsUiState = SettingsUiState(),
    ): Pair<ActionScope<SettingsUiState, SettingsViewEvent>, MutableStateFlow<SettingsUiState>> {
        val stateFlow = MutableStateFlow(initialState)
        val eventChannel = Channel<SettingsViewEvent>(Channel.BUFFERED)
        return ActionScope(stateFlow, eventChannel) to stateFlow
    }

    // region ObserveSettingsAction

    @Test
    fun `ObserveSettingsAction syncs senderStatsEnabled from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateSenderStatsEnabled(false)
        assertEquals(false, stateFlow.value.senderStatsEnabled)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs opusDtxEnabled from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateOpusDtx(false)
        assertEquals(false, stateFlow.value.opusDtxEnabled)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs publisherAudioFallback from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updatePublisherAudioFallback(false)
        assertEquals(false, stateFlow.value.publisherAudioFallbackEnabled)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs subscriberAudioFallback from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateSubscriberAudioFallback(false)
        assertEquals(false, stateFlow.value.subscriberAudioFallbackEnabled)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs videoBitrateConfig from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val config = VideoBitrateConfig(preset = VideoBitratePreset.CUSTOM, maxBitrate = 5_000)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateVideoBitrateConfig(config)
        assertEquals(config, stateFlow.value.videoBitrateConfig)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs degradationPreference from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateDegradationPreference(DegradationPreference.MAINTAIN_FRAME_RATE)
        assertEquals(DegradationPreference.MAINTAIN_FRAME_RATE, stateFlow.value.degradationPreference)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs captureFrameRate from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateCaptureFrameRate(CaptureFrameRate.FPS_30)
        assertEquals(CaptureFrameRate.FPS_30, stateFlow.value.captureFrameRate)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs captureResolution from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        callSettingsHolder.updateCaptureResolution(CaptureResolution.HIGH)
        assertEquals(CaptureResolution.HIGH, stateFlow.value.captureResolution)
        collectScope.cancel()
    }

    @Test
    fun `ObserveSettingsAction syncs call from holder`() = runTest {
        val collectScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val dependencies = SettingsActionDependencies(collectScope, callSettingsHolder)
        val (scope, stateFlow) = actionScope()
        ObserveSettingsAction().execute(dependencies, scope)
        assertNull(stateFlow.value.call)
        collectScope.cancel()
    }

    // endregion

    // region ToggleSenderStatsAction

    @Test
    fun `ToggleSenderStatsAction updates holder to false`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        ToggleSenderStatsAction(enabled = false).execute(dependencies, scope)
        assertEquals(false, callSettingsHolder.senderStatsEnabled.value)
    }

    @Test
    fun `ToggleSenderStatsAction updates holder to true`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        callSettingsHolder.updateSenderStatsEnabled(false)
        val (scope, _) = actionScope()
        ToggleSenderStatsAction(enabled = true).execute(dependencies, scope)
        assertTrue(callSettingsHolder.senderStatsEnabled.value)
    }

    // endregion

    // region ToggleOpusDtxAction

    @Test
    fun `ToggleOpusDtxAction updates holder to false`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        ToggleOpusDtxAction(enabled = false).execute(dependencies, scope)
        assertEquals(false, callSettingsHolder.opusDtxEnabled.value)
    }

    @Test
    fun `ToggleOpusDtxAction updates holder to true`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        callSettingsHolder.updateOpusDtx(false)
        val (scope, _) = actionScope()
        ToggleOpusDtxAction(enabled = true).execute(dependencies, scope)
        assertTrue(callSettingsHolder.opusDtxEnabled.value)
    }

    // endregion

    // region TogglePublisherAudioFallback

    @Test
    fun `TogglePublisherAudioFallback updates holder to false`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        TogglePublisherAudioFallback(enabled = false).execute(dependencies, scope)
        assertEquals(false, callSettingsHolder.publisherAudioFallbackEnabled.value)
    }

    @Test
    fun `TogglePublisherAudioFallback updates holder to true`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        callSettingsHolder.updatePublisherAudioFallback(false)
        val (scope, _) = actionScope()
        TogglePublisherAudioFallback(enabled = true).execute(dependencies, scope)
        assertTrue(callSettingsHolder.publisherAudioFallbackEnabled.value)
    }

    // endregion

    // region ToggleSubscriberAudioFallback

    @Test
    fun `ToggleSubscriberAudioFallback updates holder to false`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        ToggleSubscriberAudioFallback(enabled = false).execute(dependencies, scope)
        assertEquals(false, callSettingsHolder.subscriberAudioFallbackEnabled.value)
    }

    @Test
    fun `ToggleSubscriberAudioFallback updates holder to true`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        callSettingsHolder.updateSubscriberAudioFallback(false)
        val (scope, _) = actionScope()
        ToggleSubscriberAudioFallback(enabled = true).execute(dependencies, scope)
        assertTrue(callSettingsHolder.subscriberAudioFallbackEnabled.value)
    }

    // endregion

    // region UpdateVideoBitrateConfigAction

    @Test
    fun `UpdateVideoBitrateConfigAction updates holder`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val config = VideoBitrateConfig(preset = VideoBitratePreset.CUSTOM, maxBitrate = 8_000)
        val (scope, _) = actionScope()
        UpdateVideoBitrateConfigAction(config).execute(dependencies, scope)
        assertEquals(config, callSettingsHolder.videoBitrateConfig.value)
    }

    // endregion

    // region UpdateDegradationPreferenceAction

    @Test
    fun `UpdateDegradationPreferenceAction updates holder`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        UpdateDegradationPreferenceAction(DegradationPreference.MAINTAIN_RESOLUTION).execute(dependencies, scope)
        assertEquals(DegradationPreference.MAINTAIN_RESOLUTION, callSettingsHolder.degradationPreference.value)
    }

    // endregion

    // region UpdateCaptureFrameRateAction

    @Test
    fun `UpdateCaptureFrameRateAction updates holder`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        UpdateCaptureFrameRateAction(CaptureFrameRate.FPS_30).execute(dependencies, scope)
        assertEquals(CaptureFrameRate.FPS_30, callSettingsHolder.captureFrameRate.value)
    }

    // endregion

    // region UpdateCaptureResolutionAction

    @Test
    fun `UpdateCaptureResolutionAction updates holder`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        val (scope, _) = actionScope()
        UpdateCaptureResolutionAction(CaptureResolution.HIGH_1080P).execute(dependencies, scope)
        assertEquals(CaptureResolution.HIGH_1080P, callSettingsHolder.captureResolution.value)
    }

    @Test
    fun `UpdateCaptureResolutionAction updates holder with null`() = runTest {
        val dependencies = SettingsActionDependencies(this, callSettingsHolder)
        callSettingsHolder.updateCaptureResolution(CaptureResolution.MEDIUM)
        val (scope, _) = actionScope()
        UpdateCaptureResolutionAction(null).execute(dependencies, scope)
        assertNull(callSettingsHolder.captureResolution.value)
    }

    // endregion
}
