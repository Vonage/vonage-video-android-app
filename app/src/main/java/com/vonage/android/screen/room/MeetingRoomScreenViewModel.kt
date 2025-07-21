package com.vonage.android.screen.room

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.Call
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.SessionEvent
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

    private lateinit var call: Call

    fun init(roomName: String) {
        viewModelScope.launch {
            sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    connect(sessionInfo)
                    _uiState.value = RoomUiState.Content(
                        roomName = roomName,
                        call = call,
                    )
                }
                .onFailure {
                    // how to handle this error?
                }
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
            call.connect()
                .collect {
                    when (it) {
                        is SessionEvent.Connected -> {}
                        is SessionEvent.Disconnected -> {}
                        is SessionEvent.Error -> {}
                        is SessionEvent.StreamDropped -> {}
                        is SessionEvent.StreamReceived -> {}
                    }
                    Log.d("XXX", "ViewModel received $it")
                }
        }
    }

    fun onToggleMic() {
        call.togglePublisherAudio()
    }

    fun onToggleCamera() {
        call.togglePublisherVideo()
    }

    fun onToggleParticipants() {
        // toggle participant list show flag
    }

    fun endCall() {
        // change this to a cancellation approach
        if (::call.isInitialized) {
            call.end()
        }
    }

    fun onPause() {
        // change this to a cancellation approach
        if (::call.isInitialized) {
            call.pause()
        }
    }

    fun onResume() {
        // change this to a cancellation approach
        if (::call.isInitialized) {
            call.resume()
        }
    }
}

sealed interface RoomUiState {

    data class Content(
        val roomName: String = "",
        val call: Call,
    ) : RoomUiState

    data object Loading : RoomUiState

}
