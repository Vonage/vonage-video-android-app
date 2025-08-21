package com.vonage.android.screen.waiting

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.audio.util.MicVolume
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.ext.toggle
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = WaitingRoomViewModelFactory::class)
class WaitingRoomViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val userRepository: UserRepository,
    private val videoClient: VonageVideoClient,
    private val micVolume: MicVolume,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WaitingRoomUiState>(WaitingRoomUiState.Idle)
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = WaitingRoomUiState.Idle,
    )

    private val _audioLevel = MutableStateFlow(0F)
    val audioLevel: StateFlow<Float> = _audioLevel.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = 0F,
    )

    private lateinit var publisher: VeraPublisher
    private var currentBlurIndex: Int = 0

    fun init() {
        publisher = videoClient.buildPublisher()
        viewModelScope.launch {
            publisher = publisher.copy(name = userRepository.getUserName())
            _uiState.value = buildContentUiState(
                roomName = roomName,
                participant = publisher,
            )
        }
        viewModelScope.launch {
            micVolume.start()
            micVolume.volume()
                .distinctUntilChanged()
                .onEach {
                    _audioLevel.value = it
                }
                .collect()
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
        micVolume.stop()
        val currentCameraIndex = 1 - publisher.cameraIndex
        publisher = publisher.copy(cameraIndex = currentCameraIndex)
        publisher.cycleCamera()
        micVolume.start()
        _uiState.value = buildContentUiState(
            roomName = roomName,
            participant = publisher,
        )
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
                    cameraIndex = publisher.cameraIndex,
                )
            )
            onStop()
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
        micVolume.stop()
        videoClient.destroyPublisher()
    }

    private companion object {
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
    }
}

@AssistedFactory
interface WaitingRoomViewModelFactory {
    fun create(roomName: String): WaitingRoomViewModel
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
