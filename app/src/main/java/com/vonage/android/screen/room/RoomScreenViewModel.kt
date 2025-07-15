package com.vonage.android.screen.room

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.Call
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.screen.waiting.CreatePublisherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomScreenViewModel @Inject constructor(
    private val createPublisherUseCase: CreatePublisherUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoomUiState>(RoomUiState.Loading)
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    private lateinit var call: Call

    fun init(context: Context, roomName: String) {
        viewModelScope.launch {
            sessionRepository.getSession(roomName)
                .onSuccess { s ->
                    val videoClient = VonageVideoClient(context)
                    call = videoClient.initializeSession(
                        apiKey = s.apiKey,
                        sessionId = s.sessionId,
                        token = s.token,
                    )
                    call.connect()
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

    fun endCall() {
        // change this to a cancellation approach
        if (::call.isInitialized) {
            call.end()
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
