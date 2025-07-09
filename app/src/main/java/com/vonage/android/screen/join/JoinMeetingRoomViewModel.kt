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
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Content())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun updateName(roomName: String) {
        roomName.trim()
        val roomNameError = roomName.isValidRoomName().not()
        _uiState.value = MainUiState.Content(
            roomName = roomName,
            isRoomNameWrong = roomNameError,
        )
    }

    fun createRoom() {
        val roomNameGenerated = RoomNameGenerator.generateRoomName()
        _uiState.value = MainUiState.Content(
            roomName = roomNameGenerated,
            isRoomNameWrong = false,
        )
    }

    fun joinRoom(roomName: String) {
        _uiState.value = MainUiState.Loading
        viewModelScope.launch {
            val response = apiService.getSession(roomName)
            response.body()
                ?.apply {
                    _uiState.value = MainUiState.Success(
                        apiKey = apiKey,
                        sessionId = sessionId,
                        token = token,
                    )
                }
        }
    }
}

@Immutable
sealed interface MainUiState {
    @Immutable
    data class Content(
        val roomName: String = "",
        val isRoomNameWrong: Boolean = false,
    ) : MainUiState

    data object Loading : MainUiState

    @Immutable
    data class Success(
        var apiKey: String = "",
        var sessionId: String = "",
        var token: String = "",
    ) : MainUiState
}
