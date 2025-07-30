package com.vonage.android.screen.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MeetingRoomViewModelFactory::class)
class MeetingRoomScreenViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val sessionRepository: SessionRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MeetingRoomUiState>(MeetingRoomUiState.Loading)
    val uiState: StateFlow<MeetingRoomUiState> = _uiState.asStateFlow()

    private lateinit var call: CallFacade

    fun init() {
        // todo: find better way to avoid multiple calls to this method on configuration changes
        if (this::call.isInitialized) return
        viewModelScope.launch {
            _uiState.value = MeetingRoomUiState.Loading
            sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    onSessionCreated(
                        roomName = roomName,
                        sessionInfo = sessionInfo,
                    )
                }
                .onFailure {
                    _uiState.value = MeetingRoomUiState.SessionError
                }
        }
    }

    private fun onSessionCreated(
        roomName: String,
        sessionInfo: SessionInfo,
    ) {
        connect(sessionInfo)
        _uiState.value = MeetingRoomUiState.Content(
            roomName = roomName,
            call = call,
        )
    }

    private fun connect(sessionInfo: SessionInfo) {
        videoClient.buildPublisher()
        call = videoClient.initializeSession(
            apiKey = sessionInfo.apiKey,
            sessionId = sessionInfo.sessionId,
            token = sessionInfo.token,
        )
        viewModelScope.launch {
            call.connect().collect()
        }
    }

    fun onToggleMic() {
        call.togglePublisherAudio()
    }

    fun onToggleCamera() {
        call.togglePublisherVideo()
    }

    fun onSwitchCamera() {
        call.togglePublisherCamera()
    }

    fun endCall() {
        call.endSession()
    }

    fun onPause() {
        call.pauseSession()
    }

    fun onResume() {
        if (this::call.isInitialized) {
            call.resumeSession()
        }
    }
}

@AssistedFactory
interface MeetingRoomViewModelFactory {
    fun create(roomName: String): MeetingRoomScreenViewModel
}

sealed interface MeetingRoomUiState {
    data class Content(
        val roomName: String,
        val call: CallFacade,
    ) : MeetingRoomUiState

    data object Loading : MeetingRoomUiState
    data object SessionError : MeetingRoomUiState
}
