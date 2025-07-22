package com.vonage.android.screen.waiting

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingRoomViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WaitingRoomUiState>(WaitingRoomUiState.Idle)
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.asStateFlow()

    private lateinit var publisher: VeraPublisher
    private lateinit var roomName: String

    fun init(roomName: String) {
        this.roomName = roomName
        publisher = videoClient.buildPublisher()
        viewModelScope.launch {
            publisher = publisher.copy(name = userRepository.getUserName().orEmpty())
            _uiState.value = buildContentUiState(
                roomName = roomName,
                participant = publisher,
            )
        }
    }

    fun updateUserName(userName: String) {
        publisher = publisher.copy(name = userName)
        _uiState.value = buildContentUiState(
            roomName = roomName,
            participant = publisher,
        )
    }

    fun onMicToggle() {
        publisher = publisher.copy(isMicEnabled = !publisher.isMicEnabled)
        _uiState.value = buildContentUiState(
            roomName = roomName,
            participant = publisher,
        )
    }

    fun onCameraToggle() {
        publisher = publisher.copy(isCameraEnabled = !publisher.isCameraEnabled)
        _uiState.value = buildContentUiState(
            roomName = roomName,
            participant = publisher,
        )
    }

    fun joinRoom(roomName: String, userName: String) {
        viewModelScope.launch {
            userRepository.saveUserName(userName)
            videoClient.configurePublisher(
                PublisherConfig(
                    name = userName,
                    publishVideo = publisher.isCameraEnabled,
                    publishAudio = publisher.isMicEnabled,
                )
            )
            videoClient.destroyPublisher()
            _uiState.value = WaitingRoomUiState.Success(
                roomName = roomName,
            )
        }
    }

    private fun buildContentUiState(roomName: String, participant: Participant) =
        WaitingRoomUiState.Content(
            roomName = roomName,
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            userName = participant.name,
            view = participant.view,
        )
}

sealed interface WaitingRoomUiState {

    object Idle : WaitingRoomUiState
    data class Content(
        val roomName: String,
        val userName: String,
        val isMicEnabled: Boolean,
        val isCameraEnabled: Boolean,
        val view: View? = null,
    ) : WaitingRoomUiState

    data class Success(
        val roomName: String
    ) : WaitingRoomUiState
}
