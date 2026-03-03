package com.vonage.android.settings.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.settings.CallSettingsHolder
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

        callSettingsHolder.call
            .onEach { call -> _uiState.update { it.copy(call = call) } }
            .launchIn(viewModelScope)
    }

    fun toggleSenderStatsTrack(enabled: Boolean) {
        callSettingsHolder.updateSenderStatsEnabled(enabled)
    }
}

@AssistedFactory
fun interface SettingsScreenViewModelFactory {
    fun create(
        @Assisted("appVersion") appVersion: String,
        @Assisted("sdkVersion") sdkVersion: String,
    ): SettingsScreenViewModel
}

@Stable
data class SettingsUiState(
    val appVersion: String = "",
    val sdkVersion: String = "",
    val call: CallFacade? = null,
    val senderStatsEnabled: Boolean = true,
)
