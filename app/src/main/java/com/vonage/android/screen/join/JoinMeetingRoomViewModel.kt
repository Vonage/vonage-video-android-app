package com.vonage.android.screen.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.SessionRepository
import com.vonage.android.util.RoomNameGenerator
import com.vonage.android.util.isValidRoomName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinMeetingRoomViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val roomNameGenerator: RoomNameGenerator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<JoinMeetingRoomUiState>(JoinMeetingRoomUiState.Content())
    val uiState: StateFlow<JoinMeetingRoomUiState> = _uiState.asStateFlow()

    fun updateName(roomName: String) {
        val roomNameError = roomName.isValidRoomName().not()
        _uiState.value = JoinMeetingRoomUiState.Content(
            roomName = roomName,
            isRoomNameWrong = roomNameError,
        )
    }

    fun createRoom() {
        val roomNameGenerated = roomNameGenerator.generateRoomName()
        _uiState.value = JoinMeetingRoomUiState.Content(
            roomName = roomNameGenerated,
            isRoomNameWrong = false,
        )
    }

    fun joinRoom(roomName: String) {
        viewModelScope.launch {
            _uiState.emit(JoinMeetingRoomUiState.Loading)
            sessionRepository.getSession(roomName)
                .onSuccess {
                    _uiState.value = JoinMeetingRoomUiState.Success(
                        roomName = roomName,
                        apiKey = it.apiKey,
                        sessionId = it.sessionId,
                        token = it.token,
                    )
                }
                .onFailure {
                    _uiState.value = JoinMeetingRoomUiState.Content(
                        roomName = roomName,
                        isRoomNameWrong = false,
                        isError = true,
                    )
                }
        }
    }
}

sealed interface JoinMeetingRoomUiState {

    data class Content(
        val roomName: String = "",
        val isRoomNameWrong: Boolean = false,
        val isError: Boolean = false,
    ) : JoinMeetingRoomUiState

    data object Loading : JoinMeetingRoomUiState

    data class Success(
        val roomName: String,
        val apiKey: String,
        val sessionId: String,
        val token: String,
    ) : JoinMeetingRoomUiState
}
