package com.vonage.android.screen.landing

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.RoomAccessRepository
import com.vonage.android.util.RoomNameGenerator
import com.vonage.android.util.isValidRoomName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    private val roomNameGenerator: RoomNameGenerator,
    private val roomAccessRepository: RoomAccessRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LandingScreenUiState>(LandingScreenUiState.Content())
    val uiState: StateFlow<LandingScreenUiState> = _uiState.asStateFlow()

    private val content: LandingScreenUiState.Content
        get() = _uiState.value as? LandingScreenUiState.Content ?: LandingScreenUiState.Content()

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
        if (content.isCheckingAccess) return
        if (roomName.isValidRoomName()) {
            checkAccessAndJoin(roomName)
        } else {
            _uiState.value = LandingScreenUiState.Content(
                roomName = roomName,
                isRoomNameWrong = true,
            )
        }
    }

    fun onAuthenticated() {
        val roomName = content.authRequiredRoomName ?: return
        _uiState.value = LandingScreenUiState.Success(roomName = roomName)
    }

    fun onAuthenticationDismissed() {
        _uiState.value = content.copy(authRequiredRoomName = null)
    }

    private fun checkAccessAndJoin(roomName: String) {
        val current = content.copy(isRoomNameWrong = false)
        _uiState.value = current.copy(isCheckingAccess = true)
        viewModelScope.launch {
            _uiState.value = if (roomAccessRepository.requiresAuthentication(roomName)) {
                current.copy(authRequiredRoomName = roomName)
            } else {
                LandingScreenUiState.Success(roomName = roomName)
            }
        }
    }
}

@Immutable
sealed interface LandingScreenUiState {
    data class Content(
        val roomName: String = "",
        val isRoomNameWrong: Boolean = false,
        val isError: Boolean = false,
        val isCheckingAccess: Boolean = false,
        /** Room the user tried to enter while the backend requires authentication. */
        val authRequiredRoomName: String? = null,
    ) : LandingScreenUiState

    data class Success(
        val roomName: String = "",
    ) : LandingScreenUiState
}
