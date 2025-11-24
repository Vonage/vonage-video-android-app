package com.vonage.android.screen.join

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.vonage.android.util.RoomNameGenerator
import com.vonage.android.util.isValidRoomName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class JoinMeetingRoomViewModel @Inject constructor(
    private val roomNameGenerator: RoomNameGenerator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JoinMeetingRoomUiState())
    val uiState: StateFlow<JoinMeetingRoomUiState> = _uiState.asStateFlow()

    fun updateName(roomName: String) {
        val roomNameError = roomName.isValidRoomName().not()
        _uiState.value = JoinMeetingRoomUiState(
            roomName = roomName,
            isRoomNameWrong = roomNameError,
        )
    }

    fun createRoom() {
        val roomNameGenerated = roomNameGenerator.generateRoomName()
        joinRoom(roomNameGenerated)
    }

    fun joinRoom(roomName: String) {
        _uiState.value = JoinMeetingRoomUiState(
            roomName = roomName,
            isSuccess = true,
        )
    }
}

@Immutable
data class JoinMeetingRoomUiState(
    val roomName: String = "",
    val isRoomNameWrong: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
)
