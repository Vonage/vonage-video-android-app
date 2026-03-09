package com.vonage.android.screen.settings

import com.vonage.android.core.ActionScope
import com.vonage.android.core.ViewAction
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.settings.SettingsUiState
import kotlinx.coroutines.launch

class ObserveSettingsAction :
    ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        val holder = dependencies.callSettingsHolder
        val scope = dependencies.coroutineScope
        scope.launch {
            holder.senderStatsEnabled.collect { enabled ->
                actionScope.setState { copy(senderStatsEnabled = enabled) }
            }
        }
        scope.launch {
            holder.opusDtxEnabled.collect { enabled ->
                actionScope.setState { copy(opusDtxEnabled = enabled) }
            }
        }
        scope.launch { holder.call.collect { call -> actionScope.setState { copy(call = call) } } }
        scope.launch {
            holder.videoBitrateConfig.collect { config ->
                actionScope.setState { copy(videoBitrateConfig = config) }
            }
        }
        scope.launch {
            holder.degradationPreference.collect { pref ->
                actionScope.setState { copy(degradationPreference = pref) }
            }
        }
        scope.launch {
            holder.captureFrameRate.collect { fr ->
                actionScope.setState { copy(captureFrameRate = fr) }
            }
        }
        scope.launch {
            holder.captureResolution.collect { res ->
                actionScope.setState { copy(captureResolution = res) }
            }
        }
    }
}

class ToggleSenderStatsAction(
    private val enabled: Boolean,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateSenderStatsEnabled(enabled)
    }
}

class ToggleOpusDtxAction(
    private val enabled: Boolean,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateOpusDtx(enabled)
    }
}

class UpdateVideoBitrateConfigAction(
    private val config: VideoBitrateConfig,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateVideoBitrateConfig(config)
    }
}

class UpdateDegradationPreferenceAction(
    private val preference: DegradationPreference,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateDegradationPreference(preference)
    }
}

class UpdateCaptureFrameRateAction(
    private val frameRate: CaptureFrameRate,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateCaptureFrameRate(frameRate)
    }
}

class UpdateCaptureResolutionAction(
    private val resolution: CaptureResolution?,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateCaptureResolution(resolution)
    }
}
