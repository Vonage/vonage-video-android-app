package com.vonage.android.screen.join

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.network.APIService
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
    private val apiService: APIService,
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
            val response = apiService.getSession(roomName)
            if (response.isSuccessful) {
                response.body()
                    ?.apply {
                        _uiState.value = JoinMeetingRoomUiState.Success(
                            apiKey = apiKey,
                            sessionId = sessionId,
                            token = token,
                        )
                    }
            } else {
                _uiState.value = JoinMeetingRoomUiState.Content(
                    roomName = roomName,
                    isRoomNameWrong = true,
                )
            }
        }
    }
}

@Immutable
sealed interface JoinMeetingRoomUiState {
    @Immutable
    data class Content(
        val roomName: String = "",
        val isRoomNameWrong: Boolean = false,
    ) : JoinMeetingRoomUiState

    data object Loading : JoinMeetingRoomUiState

    @Immutable
    data class Success(
        var apiKey: String = "",
        var sessionId: String = "",
        var token: String = "",
    ) : JoinMeetingRoomUiState
}
