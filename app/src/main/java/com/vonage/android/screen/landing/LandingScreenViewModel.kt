package com.vonage.android.screen.landing

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
class LandingScreenViewModel @Inject constructor(
    private val roomNameGenerator: RoomNameGenerator,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LandingScreenUiState>(LandingScreenUiState.Content())
    val uiState: StateFlow<LandingScreenUiState> = _uiState.asStateFlow()

    fun updateName(roomName: String) {
        val roomNameError = roomName.isValidRoomName().not()
        _uiState.value = LandingScreenUiState.Content(
            roomName = roomName,
            isRoomNameWrong = roomNameError,
        )
    }

    fun createRoom() {
        val roomNameGenerated = roomNameGenerator.generateRoomName()
        joinRoom(roomNameGenerated)
    }

    fun joinRoom(roomName: String) {
        if (roomName.isValidRoomName()) {
            _uiState.value = LandingScreenUiState.Success(
                roomName = roomName,
            )
        } else {
            _uiState.value = LandingScreenUiState.Content(
                roomName = roomName,
                isRoomNameWrong = true,
            )
        }
    }
}

@Immutable
sealed interface LandingScreenUiState {
    data class Content(
        val roomName: String = "",
        val isRoomNameWrong: Boolean = false,
        val isError: Boolean = false,
    ) : LandingScreenUiState

    data class Success(
        val roomName: String = "",
    ) : LandingScreenUiState
}
