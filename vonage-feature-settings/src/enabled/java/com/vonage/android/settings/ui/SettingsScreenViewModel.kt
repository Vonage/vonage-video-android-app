package com.vonage.android.settings.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.settings.BuildConfig
import com.vonage.android.settings.PublisherStatsHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val publisherStatsHolder: PublisherStatsHolder,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            //appVersion = BuildConfig.VERSION_NAME,
            //sdkVersion = BuildConfig.OPENTOK_SDK_VERSION,
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
                _uiState.update { it.copy(subscriberStats = stats)
                } }
            .launchIn(viewModelScope)
    }

    fun toggleSenderStatsTrack(enabled: Boolean) {
        publisherStatsHolder.updateSenderStatsEnabled(enabled)
    }
}
