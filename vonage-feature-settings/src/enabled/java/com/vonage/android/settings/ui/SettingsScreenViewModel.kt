package com.vonage.android.settings.ui

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.settings.PublisherStatsHolder
import com.vonage.android.settings.SubscriberStatsSnapshot
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
    private val publisherStatsHolder: PublisherStatsHolder,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            appVersion = appVersion,
            sdkVersion = sdkVersion,
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        publisherStatsHolder.senderStatsEnabled
            .onEach { enabled -> _uiState.update { it.copy(senderStatsEnabled = enabled) } }
            .launchIn(viewModelScope)

        publisherStatsHolder.videoStats
            .onEach { stats -> _uiState.update { it.copy(videoStats = stats) } }
            .launchIn(viewModelScope)

        publisherStatsHolder.audioStats
            .onEach { stats -> _uiState.update { it.copy(audioStats = stats) } }
            .launchIn(viewModelScope)

        publisherStatsHolder.subscriberStats
            .onEach { stats ->
                Log.d("UI STATS", " stats to draw $stats")
                _uiState.update {
                    it.copy(subscriberStats = stats)
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleSenderStatsTrack(enabled: Boolean) {
        publisherStatsHolder.updateSenderStatsEnabled(enabled)
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
    val senderStatsEnabled: Boolean = true,
    val appVersion: String = "",
    val sdkVersion: String = "",
    val videoStats: PublisherState.VideoStats? = null,
    val audioStats: PublisherState.AudioStats? = null,
    val subscriberStats: List<SubscriberStatsSnapshot> = emptyList(),
)
