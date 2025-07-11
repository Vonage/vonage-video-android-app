package com.vonage.android.screen.waiting

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.vonage.android.kotlin.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val createPublisher: CreatePublisherUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WaitingRoomUiState>(WaitingRoomUiState.Content())
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.asStateFlow()

    private lateinit var participant: Participant

    fun init(context: Context) {
        participant = createPublisher(context)
        _uiState.value = WaitingRoomUiState.Content(
            participant = ParticipantUiState.Available(
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                userName = participant.name,
                view = participant.view,
            )
        )
    }

    fun updateUserName(userName: String) {
        participant.name = userName
        _uiState.value = WaitingRoomUiState.Content(
            participant = ParticipantUiState.Available(
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                userName = participant.name,
                view = participant.view,
            )
        )
    }

    fun onMicToggle() {
        participant.toggleAudio()
        _uiState.value = WaitingRoomUiState.Content(
            participant = ParticipantUiState.Available(
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                userName = participant.name,
                view = participant.view,
            )
        )
    }

    fun onCameraToggle() {
        participant.toggleVideo()
        _uiState.value = WaitingRoomUiState.Content(
            participant = ParticipantUiState.Available(
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                userName = participant.name,
                view = participant.view,
            )
        )
    }
}

sealed interface WaitingRoomUiState {

    data class Content(
        val participant: ParticipantUiState = ParticipantUiState.Idle,
        val roomName: String = "",
    ) : WaitingRoomUiState

}

sealed interface ParticipantUiState {
    object Idle : ParticipantUiState
    data class Available(
        val userName: String,
        val isMicEnabled: Boolean,
        val isCameraEnabled: Boolean,
        val view: View,
    ) : ParticipantUiState
}
