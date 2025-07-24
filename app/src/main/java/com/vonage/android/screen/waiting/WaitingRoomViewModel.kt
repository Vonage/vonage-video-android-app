package com.vonage.android.screen.waiting

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.BlurLevel
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

    private var currentBlurIndex: Int = 0

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
        publisher = publisher.copy(isMicEnabled = publisher.isMicEnabled.toggle())
        _uiState.value = buildContentUiState(
            roomName = roomName,
            participant = publisher,
        )
    }

    fun onCameraToggle() {
        publisher = publisher.copy(isCameraEnabled = publisher.isCameraEnabled.toggle())
        _uiState.value = buildContentUiState(
            roomName = roomName,
            participant = publisher,
        )
    }

    fun onCameraSwitch() {
        publisher.cycleCamera()
    }

    fun setBlur() {
        val blurLevel = BlurLevel.entries[currentBlurIndex % BlurLevel.entries.size]
        currentBlurIndex += 1
        publisher = publisher.copy(blurLevel = blurLevel)
        publisher.setCameraBlur(blurLevel)
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
                    blurLevel = publisher.blurLevel,
                )
            )
            videoClient.destroyPublisher()
            _uiState.value = WaitingRoomUiState.Success(
                roomName = roomName,
            )
        }
    }

    private fun buildContentUiState(roomName: String, participant: VeraPublisher) =
        WaitingRoomUiState.Content(
            roomName = roomName,
            isCameraEnabled = participant.isCameraEnabled,
            isMicEnabled = participant.isMicEnabled,
            userName = participant.name,
            blurLevel = participant.blurLevel,
            view = participant.view,
        )

    fun onStop() {
        videoClient.destroyPublisher()
    }
}

sealed interface WaitingRoomUiState {

    object Idle : WaitingRoomUiState
    data class Content(
        val roomName: String,
        val userName: String,
        val isMicEnabled: Boolean,
        val isCameraEnabled: Boolean,
        val blurLevel: BlurLevel,
        val view: View? = null,
    ) : WaitingRoomUiState

    data class Success(
        val roomName: String
    ) : WaitingRoomUiState
}
