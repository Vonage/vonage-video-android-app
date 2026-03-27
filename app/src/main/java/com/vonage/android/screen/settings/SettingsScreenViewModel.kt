package com.vonage.android.screen.settings

import androidx.lifecycle.viewModelScope
import com.vonage.android.core.BaseViewModel
import com.vonage.android.data.ClientLogsRepository
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.settings.SettingsUiState
import com.vonage.android.kotlin.model.VideoBitrateConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = SettingsScreenViewModelFactory::class)
class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted("appVersion") appVersion: String,
    @Assisted("sdkVersion") sdkVersion: String,
    private val callSettingsHolder: CallSettingsHolder,
    private val clientLogsRepository: ClientLogsRepository,
) : BaseViewModel<SettingsUiState, SettingsViewEvent>(
    SettingsUiState(appVersion = appVersion, sdkVersion = sdkVersion),
) {

    override val dependencies = SettingsActionDependencies(
        coroutineScope = viewModelScope,
        callSettingsHolder = callSettingsHolder,
        clientLogsRepository = clientLogsRepository,
    )

    init {
        dispatch(ObserveSettingsAction())
    }

    fun toggleSenderStatsTrack(enabled: Boolean) = dispatch(ToggleSenderStatsAction(enabled))

    fun toggleOpusDtx(enabled: Boolean) = dispatch(ToggleOpusDtxAction(enabled))

    fun togglePublisherAudioFallback(enabled: Boolean) = dispatch(TogglePublisherAudioFallback(enabled))

    fun toggleSubscriberAudioFallback(enabled: Boolean) = dispatch(ToggleSubscriberAudioFallback(enabled))

    fun updateVideoBitrateConfig(config: VideoBitrateConfig) = dispatch(UpdateVideoBitrateConfigAction(config))

    fun updateDegradationPreference(preference: DegradationPreference) = dispatch(UpdateDegradationPreferenceAction(preference))

    fun updateCaptureFrameRate(frameRate: CaptureFrameRate) = dispatch(UpdateCaptureFrameRateAction(frameRate))

    fun updateCaptureResolution(resolution: CaptureResolution?) = dispatch(UpdateCaptureResolutionAction(resolution))

    fun updatePreferredVideoCodecOrder(order: List<VideoCodec>?) = dispatch(UpdatePreferredVideoCodecOrderAction(order))

    fun updateAudioBitrate(bitrate: Int?) = dispatch(UpdateAudioBitrateAction(bitrate))

    fun toggleLogs(enabled: Boolean) = dispatch(ToggleLogsAction(enabled))

    fun sendLogs() = dispatch(SendClientLogsAction())
}

@AssistedFactory
fun interface SettingsScreenViewModelFactory {
    fun create(
        @Assisted("appVersion") appVersion: String,
        @Assisted("sdkVersion") sdkVersion: String,
    ): SettingsScreenViewModel
}
