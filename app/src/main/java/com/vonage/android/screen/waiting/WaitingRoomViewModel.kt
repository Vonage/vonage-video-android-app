package com.vonage.android.screen.waiting

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherParticipant
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = WaitingRoomViewModelFactory::class)
class WaitingRoomViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val userRepository: UserRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingRoomUiState(roomName = roomName))
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = WaitingRoomUiState(roomName = roomName),
    )

    fun init(context: Context) {
        viewModelScope.launch {
            val name = userRepository.getUserName()
            videoClient.createPreviewPublisher(context, name)
                .also { publisher ->
                    _uiState.update { uiState -> uiState.copy(userName = name, publisher = publisher) }
                    publisher.setup()
                }
        }
    }

    fun updateUserName(userName: String) {
        _uiState.update { uiState -> uiState.copy(userName = userName) }
    }

    fun onMicToggle() {
        currentPublisher()?.toggleAudio()
    }

    fun onCameraToggle() {
        currentPublisher()?.toggleVideo()
    }

    fun onCameraSwitch() {
        currentPublisher()?.cycleCamera()
    }

    fun setBlur() {
        currentPublisher()?.cycleCameraBlur()
    }

    fun joinRoom(userName: String) {
        viewModelScope.launch {
            userRepository.saveUserName(userName)
            currentPublisher()?.let { publisher ->
                videoClient.configurePublisher(
                    PublisherConfig(
                        name = userName,
                        publishVideo = publisher.isCameraEnabled.value,
                        publishAudio = publisher.isMicEnabled.value,
                        blurLevel = publisher.blurLevel.value,
                        cameraIndex = publisher.camera.value.index,
                    )
                )
            }
            onStop()
            _uiState.update { uiState -> uiState.copy(isSuccess = true) }
        }
    }

    fun onStop() {
        currentPublisher()?.clean()
        videoClient.destroyPublisher()
    }

    private fun currentPublisher() = _uiState.value.publisher

    private companion object {
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
    }
}

@AssistedFactory
fun interface WaitingRoomViewModelFactory {
    fun create(roomName: String): WaitingRoomViewModel
}

@Immutable
data class WaitingRoomUiState(
    val roomName: String,
    val userName: String = "",
    val publisher: PublisherParticipant? = null,
    val isSuccess: Boolean = false,
)
