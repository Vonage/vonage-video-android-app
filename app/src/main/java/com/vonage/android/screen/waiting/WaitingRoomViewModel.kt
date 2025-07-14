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

    private val _uiState = MutableStateFlow<WaitingRoomUiState>(WaitingRoomUiState.Idle)
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.asStateFlow()

    private lateinit var participant: Participant

    fun init(context: Context) {
        participant = createPublisher(context)
        _uiState.value = WaitingRoomUiState.Content(
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            userName = participant.name,
            view = participant.view,
        )
    }

    fun updateUserName(userName: String) {
        participant.name = userName
        _uiState.value = WaitingRoomUiState.Content(
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            userName = participant.name,
            view = participant.view,
        )
    }

    fun onMicToggle() {
        participant.toggleAudio()
        _uiState.value = WaitingRoomUiState.Content(
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            userName = participant.name,
            view = participant.view,
        )
    }

    fun onCameraToggle() {
        participant.toggleVideo()
        _uiState.value = WaitingRoomUiState.Content(
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            userName = participant.name,
            view = participant.view,
        )
    }

    fun joinRoom(roomName: String) {
        // cache user name
        _uiState.value = WaitingRoomUiState.Success(
            roomName = roomName,
        )
    }

    fun onPermissions(map: Map<String, Boolean>) {
        val cameraPermissionGranted = map["android.permission.CAMERA"] ?: false
        val microphonePermissionGranted = map["android.permission.RECORD_AUDIO"] ?: false
        if (!cameraPermissionGranted || !microphonePermissionGranted) {
            _uiState.value = WaitingRoomUiState.Content(
                userName = "",
                isMicEnabled = microphonePermissionGranted,
                isCameraEnabled = cameraPermissionGranted,
                isPermissionError = PermissionError(
                    cameraPermissionGranted = cameraPermissionGranted,
                    microphonePermissionGranted = microphonePermissionGranted,
                ),
            )
        }
    }
}

sealed interface WaitingRoomUiState {

    object Idle : WaitingRoomUiState
    data class Content(
        val userName: String,
        val isMicEnabled: Boolean,
        val isCameraEnabled: Boolean,
        val isPermissionError: PermissionError? = null,
        val view: View? = null,
    ) : WaitingRoomUiState

    data class Success(
        val roomName: String
    ) : WaitingRoomUiState
}

data class PermissionError(
    val cameraPermissionGranted: Boolean,
    val microphonePermissionGranted: Boolean,
)
