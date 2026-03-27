package com.vonage.android.screen.settings

import com.vonage.android.core.ActionScope
import com.vonage.android.core.ViewAction
import com.vonage.android.data.SendClientLogsResult
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoCodec
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
        scope.launch {
            holder.publisherAudioFallbackEnabled.collect { enabled ->
                actionScope.setState { copy(publisherAudioFallbackEnabled = enabled) }
            }
        }
        scope.launch {
            holder.subscriberAudioFallbackEnabled.collect { enabled ->
                actionScope.setState { copy(subscriberAudioFallbackEnabled = enabled) }
            }
        }
        scope.launch {
            holder.preferredVideoCodecOrder.collect { order ->
                actionScope.setState { copy(preferredVideoCodecOrder = order) }
            }
        }
        scope.launch {
            holder.audioBitrate.collect { bitrate ->
                actionScope.setState { copy(audioBitrate = bitrate) }
            }
        }
        scope.launch {
            dependencies.clientLogsRepository.logsEnabled.collect { enabled ->
                actionScope.setState { copy(logsEnabled = enabled) }
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

class TogglePublisherAudioFallback(
    private val enabled: Boolean,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updatePublisherAudioFallback(enabled)
    }
}

class ToggleSubscriberAudioFallback(
    private val enabled: Boolean,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateSubscriberAudioFallback(enabled)
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

class UpdatePreferredVideoCodecOrderAction(
    private val order: List<VideoCodec>?,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updatePreferredVideoCodecOrder(order)
    }
}

class UpdateAudioBitrateAction(
    private val bitrate: Int?,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.callSettingsHolder.updateAudioBitrate(bitrate)
    }
}

class ToggleLogsAction(
    private val enabled: Boolean,
) : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        dependencies.clientLogsRepository.setLogsEnabled(enabled)
    }
}

class SendClientLogsAction : ViewAction<SettingsActionDependencies, SettingsUiState, SettingsViewEvent> {
    override suspend fun execute(
        dependencies: SettingsActionDependencies,
        actionScope: ActionScope<SettingsUiState, SettingsViewEvent>,
    ) {
        actionScope.setState { copy(isSendingLogs = true) }

        when (dependencies.clientLogsRepository.sendLogs()) {
            SendClientLogsResult.Success -> actionScope.sendEvent(SettingsViewEvent.LogsSent)
            SendClientLogsResult.NoLogsAvailable -> actionScope.sendEvent(SettingsViewEvent.NoLogsAvailable)
            SendClientLogsResult.Failure -> actionScope.sendEvent(SettingsViewEvent.LogsSendFailed)
        }

        actionScope.setState { copy(isSendingLogs = false) }
    }
}

