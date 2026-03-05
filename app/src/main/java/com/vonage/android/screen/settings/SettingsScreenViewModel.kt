package com.vonage.android.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.settings.SettingsUiState
import com.vonage.android.kotlin.model.VideoBitrateConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = SettingsScreenViewModelFactory::class)
class SettingsScreenViewModel @AssistedInject constructor(
    @Assisted("appVersion") val appVersion: String,
    @Assisted("sdkVersion") val sdkVersion: String,
    private val callSettingsHolder: CallSettingsHolder,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            appVersion = appVersion,
            sdkVersion = sdkVersion,
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        callSettingsHolder.senderStatsEnabled
            .onEach { enabled -> _uiState.update { it.copy(senderStatsEnabled = enabled) } }
            .launchIn(viewModelScope)
        callSettingsHolder.opusDtxEnabled
            .onEach { enabled -> _uiState.update { it.copy(opusDtxEnabled = enabled) } }
            .launchIn(viewModelScope)

        callSettingsHolder.call
            .onEach { call -> _uiState.update { it.copy(call = call) } }
            .launchIn(viewModelScope)
        callSettingsHolder.videoBitrateConfig
            .onEach { config -> _uiState.update { it.copy(videoBitrateConfig = config) } }
            .launchIn(viewModelScope)
        callSettingsHolder.degradationPreference
            .onEach { pref -> _uiState.update { it.copy(degradationPreference = pref) } }
            .launchIn(viewModelScope)
        callSettingsHolder.captureFrameRate
            .onEach { fr -> _uiState.update { it.copy(captureFrameRate = fr) } }
            .launchIn(viewModelScope)
        callSettingsHolder.captureResolution
            .onEach { res -> _uiState.update { it.copy(captureResolution = res) } }
            .launchIn(viewModelScope)
    }

    fun toggleSenderStatsTrack(enabled: Boolean) {
        callSettingsHolder.updateSenderStatsEnabled(enabled)
    }
    fun toggleOpusDtx(enabled: Boolean) {
        callSettingsHolder.updateOpusDtx(enabled)
    }
    fun updateVideoBitrateConfig(config: VideoBitrateConfig) {
        callSettingsHolder.updateVideoBitrateConfig(config)
    }
    fun updateDegradationPreference(preference: DegradationPreference) {
        callSettingsHolder.updateDegradationPreference(preference)
    }

    fun updateCaptureFrameRate(frameRate: CaptureFrameRate) {
        callSettingsHolder.updateCaptureFrameRate(frameRate)
    }

    fun updateCaptureResolution(resolution: CaptureResolution?) {
        callSettingsHolder.updateCaptureResolution(resolution)
    }
}

@AssistedFactory
fun interface SettingsScreenViewModelFactory {
    fun create(
        @Assisted("appVersion") appVersion: String,
        @Assisted("sdkVersion") sdkVersion: String,
    ): SettingsScreenViewModel
}
