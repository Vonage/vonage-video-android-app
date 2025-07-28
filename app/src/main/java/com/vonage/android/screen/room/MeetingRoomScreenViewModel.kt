package com.vonage.android.screen.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingRoomScreenViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MeetingRoomUiState>(MeetingRoomUiState.Loading)
    val uiState: StateFlow<MeetingRoomUiState> = _uiState.asStateFlow()

    private var call: CallFacade? = null

    fun init(roomName: String) {
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
        call?.let {
            _uiState.value = MeetingRoomUiState.Content(
                roomName = roomName,
                call = it,
            )
        }
    }

    private fun connect(sessionInfo: SessionInfo) {
        videoClient.buildPublisher()
        call = videoClient.initializeSession(
            apiKey = sessionInfo.apiKey,
            sessionId = sessionInfo.sessionId,
            token = sessionInfo.token,
        )
        viewModelScope.launch {
            call?.connect()?.collect()
        }
    }

    fun onToggleMic() {
        call?.togglePublisherAudio()
    }

    fun onToggleCamera() {
        call?.togglePublisherVideo()
    }

    fun onSwitchCamera() {
        call?.togglePublisherCamera()
    }

    fun endCall() {
        call?.endSession()
    }

    fun onPause() {
        call?.pauseSession()
    }

    fun onResume() {
        call?.resumeSession()
    }
}

sealed interface MeetingRoomUiState {
    data class Content(
        val roomName: String = "",
        val call: CallFacade,
    ) : MeetingRoomUiState

    data object Loading : MeetingRoomUiState
    data object SessionError : MeetingRoomUiState
}
