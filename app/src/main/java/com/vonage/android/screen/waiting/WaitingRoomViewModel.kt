package com.vonage.android.screen.waiting

import android.content.Context
import android.view.View
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.audio.util.MicVolumeListener
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = WaitingRoomViewModelFactory::class)
class WaitingRoomViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val userRepository: UserRepository,
    private val videoClient: VonageVideoClient,
    private val micVolumeListener: MicVolumeListener,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaitingRoomUiState(roomName = roomName))
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = WaitingRoomUiState(roomName = roomName),
    )

    private lateinit var publisher: VeraPublisher

    private val _audioLevel = MutableStateFlow(0F)
    private var currentBlurIndex: Int = 0

    fun init(context: Context) {
        viewModelScope.launch {
            publisher = videoClient.buildPublisher(context)
            publisher = publisher.copy(name = userRepository.getUserName())
            _uiState.update { uiState ->
                uiState.copy(
                    isCameraEnabled = publisher.isCameraEnabled.value,
                    isMicEnabled = publisher.isMicEnabled.value,
                    userName = publisher.name,
                    blurLevel = publisher.blurLevel,
                    view = publisher.view,
                    audioLevel = _audioLevel,
                )
            }
        }
        viewModelScope.launch {
            micVolumeListener.start()
            micVolumeListener.volume()
                .distinctUntilChanged()
                .collectLatest { _audioLevel.value = it }
        }
    }

    fun updateUserName(userName: String) {
        publisher = publisher.copy(name = userName)
        _uiState.update { uiState -> uiState.copy(userName = userName) }
    }

    fun onMicToggle() {
        viewModelScope.launch {
            _uiState.update { uiState -> uiState.copy(isMicEnabled = publisher.toggleMic()) }
        }
    }

    fun onCameraToggle() {
        viewModelScope.launch {
            _uiState.update { uiState -> uiState.copy(isCameraEnabled = publisher.toggleCamera()) }
        }
    }

    fun onCameraSwitch() {
        micVolumeListener.stop()
        val currentCameraIndex = 1 - publisher.cameraIndex
        publisher = publisher.copy(cameraIndex = currentCameraIndex)
        publisher.cycleCamera()
        micVolumeListener.start()
    }

    fun setBlur() {
        viewModelScope.launch {
            val blurLevel = BlurLevel.entries[currentBlurIndex % BlurLevel.entries.size]
            currentBlurIndex += 1
            publisher = publisher.copy(blurLevel = blurLevel)
            publisher.setCameraBlur(blurLevel)
            _uiState.update { uiState -> uiState.copy(blurLevel = blurLevel) }
        }
    }

    fun joinRoom(userName: String) {
        viewModelScope.launch {
            userRepository.saveUserName(userName)
            videoClient.configurePublisher(
                PublisherConfig(
                    name = userName,
                    publishVideo = publisher.isCameraEnabled.value,
                    publishAudio = publisher.isMicEnabled.value,
                    blurLevel = publisher.blurLevel,
                    cameraIndex = publisher.cameraIndex,
                )
            )
            onStop()
            _uiState.update { uiState -> uiState.copy(isSuccess = true) }
        }
    }

    fun onStop() {
        micVolumeListener.stop()
        videoClient.destroyPublisher()
    }

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
    val roomName: String = "",
    val userName: String = "",
    val isMicEnabled: Boolean = true,
    val isCameraEnabled: Boolean = true,
    val blurLevel: BlurLevel = BlurLevel.NONE,
    val audioLevel: StateFlow<Float> = MutableStateFlow(0F),
    val view: View? = null,
    val isSuccess: Boolean = false,
)
