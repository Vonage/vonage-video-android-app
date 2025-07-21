package com.vonage.android.screen.room

import android.util.Log
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingRoomScreenViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoomUiState>(RoomUiState.Loading)
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    private var call: CallFacade? = null

    fun init(roomName: String) {
        viewModelScope.launch {
            _uiState.value = RoomUiState.Loading
            sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    onSessionCreated(
                        roomName = roomName,
                        sessionInfo = sessionInfo,
                    )
                }
                .onFailure {
                    _uiState.value = RoomUiState.SessionError
                }
        }
    }

    private fun onSessionCreated(
        roomName: String,
        sessionInfo: SessionInfo,
    ) {
        connect(sessionInfo)
        call?.let {
            _uiState.value = RoomUiState.Content(
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
            call?.connect()
                ?.collect {
                    // handle session events like session disconnected
                    Log.d(TAG, "SessionEvent received $it")
                }
        }
    }

    fun onToggleMic() {
        call?.togglePublisherAudio()
    }

    fun onToggleCamera() {
        call?.togglePublisherVideo()
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

    private companion object {
        const val TAG = "MeetingRoomScreenViewModel"
    }
}

sealed interface RoomUiState {

    data class Content(
        val roomName: String = "",
        val call: CallFacade,
    ) : RoomUiState

    data object Loading : RoomUiState
    data object SessionError : RoomUiState
}
