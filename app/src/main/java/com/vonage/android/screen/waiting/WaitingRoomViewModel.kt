package com.vonage.android.screen.waiting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow<WaitingRoomUiState>(WaitingRoomUiState.Content())
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.asStateFlow()

}

sealed interface WaitingRoomUiState {

    data class Content(
        val roomName: String = "",
        val userName: String = "",
    ) : WaitingRoomUiState

    data object Loading : WaitingRoomUiState
}
