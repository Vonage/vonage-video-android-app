package com.vonage.android.screen.waiting

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.config.GetConfig
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.fx.data.BackgroundEffectsRepository
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.data.UserRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.screen.components.audio.AudioDevicesHandler
import com.vonage.android.screen.components.audio.AudioDevicesState
import com.vonage.android.util.isValidUserName
import com.vonage.android.util.sanitizeUserName
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = WaitingRoomViewModelFactory::class)
class WaitingRoomViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    @ApplicationContext private val appContext: Context,
    private val getConfig: GetConfig,
    private val userRepository: UserRepository,
    private val videoClient: VonageVideoClient,
    private val audioDevicesHandler: AudioDevicesHandler,
    private val callSettingsHolder: CallSettingsHolder,
) : ViewModel() {

    private var publisherSetupJob: Job? = null
    private val _uiState = MutableStateFlow(WaitingRoomUiState(roomName = roomName))
    val uiState: StateFlow<WaitingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = WaitingRoomUiState(roomName = roomName),
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val repository = BackgroundEffectsRepository(appContext)
                repository.getBackgrounds()
            }.getOrElse { persistentListOf() }
                .also { backgrounds -> _uiState.update { it.copy(backgrounds = backgrounds) } }
        }
    }

    fun init(context: Context) {
        viewModelScope.launch {
            val config = getConfig()
            val name = userRepository.getUserName()
            videoClient.configurePublisher(buildPreviewConfig(name))
            videoClient.createPreviewPublisher(context)
                .also { publisher ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            userName = name,
                            publisher = publisher,
                            allowCameraControl = config.allowCameraControl,
                            allowMicrophoneControl = config.allowMicrophoneControl,
                            audioDevicesState = audioDevicesHandler.audioDevicesState,
                        )
                    }
                    publisherSetupJob = viewModelScope.launch { publisher.setup() }
                }
        }
        observeBuildTimeSettings(context)
        audioDevicesHandler.start()
    }

    fun updateUserName(userName: String) {
        _uiState.update { uiState ->
            uiState.copy(
                userName = userName,
                isUserNameValid = userName.isValidUserName(),
            )
        }
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

    fun applyVideoEffect(effect: VideoEffect) {
        currentPublisher()?.applyVideoEffect(effect)
    }

    fun joinRoom(userName: String) {
        viewModelScope.launch {
            val sanitizedUserName = userName.sanitizeUserName()
            if (userName.isValidUserName().not()) {
                _uiState.update { uiState -> uiState.copy(isUserNameValid = false) }
                return@launch
            }
            userRepository.saveUserName(sanitizedUserName)
            currentPublisher()?.let { publisher ->
                videoClient.configurePublisher(
                    PublisherConfig(
                        name = sanitizedUserName,
                        publishVideo = publisher.isCameraEnabled.value,
                        publishAudio = publisher.isMicEnabled.value,
                        initialVideoEffect = publisher.videoEffect.value,
                        cameraIndex = publisher.camera.value.index,
                        senderStatsTrack = callSettingsHolder.senderStatsEnabled.value,
                        preferredVideoCodecOrder = callSettingsHolder.preferredVideoCodecOrder.value,
                        audioBitrate = callSettingsHolder.audioBitrate.value,
                        videoBitrateConfig = callSettingsHolder.videoBitrateConfig.value,
                        captureResolution = callSettingsHolder.captureResolution.value,
                        captureFrameRate = callSettingsHolder.captureFrameRate.value,
                    )
                )
            }
            onStop()
            _uiState.update { uiState -> uiState.copy(isSuccess = true) }
        }
    }

    fun onStop() {
        publisherSetupJob?.cancel()
        currentPublisher()?.clean()
        videoClient.destroyPublisher()
    }

    private fun observeBuildTimeSettings(context: Context) {
        viewModelScope.launch {
            combine(
                listOf<Flow<Any?>>(
                    callSettingsHolder.captureFrameRate,
                    callSettingsHolder.captureResolution,
                    callSettingsHolder.preferredVideoCodecOrder,
                    callSettingsHolder.audioBitrate,
                    callSettingsHolder.opusDtxEnabled,
                    callSettingsHolder.publisherAudioFallbackEnabled,
                    callSettingsHolder.subscriberAudioFallbackEnabled,
                    callSettingsHolder.senderStatsEnabled,
                    callSettingsHolder.degradationPreference,
                ),
            ) { it }
                .drop(1)
                .collect { refreshPreviewPublisher(context) }
        }
    }

    private fun refreshPreviewPublisher(context: Context) {
        val current = currentPublisher() ?: return
        val name = _uiState.value.userName
        val publishVideo = current.isCameraEnabled.value
        val publishAudio = current.isMicEnabled.value
        val videoEffect = current.videoEffect.value
        val cameraIndex = current.camera.value.index

        publisherSetupJob?.cancel()
        current.clean()
        videoClient.destroyPublisher()

        videoClient.configurePublisher(
            buildPreviewConfig(name, publishVideo, publishAudio, videoEffect, cameraIndex),
        )
        val newPublisher = videoClient.createPreviewPublisher(context)
        _uiState.update { it.copy(publisher = newPublisher) }
        publisherSetupJob = viewModelScope.launch { newPublisher.setup() }
    }

    private fun buildPreviewConfig(
        name: String,
        publishVideo: Boolean = true,
        publishAudio: Boolean = true,
        initialVideoEffect: VideoEffect = VideoEffect.None,
        cameraIndex: Int = 1,
    ): PublisherConfig = PublisherConfig(
        name = name,
        publishVideo = publishVideo,
        publishAudio = publishAudio,
        initialVideoEffect = initialVideoEffect,
        cameraIndex = cameraIndex,
        captureFrameRate = callSettingsHolder.captureFrameRate.value,
        captureResolution = callSettingsHolder.captureResolution.value,
        preferredVideoCodecOrder = callSettingsHolder.preferredVideoCodecOrder.value,
        audioBitrate = callSettingsHolder.audioBitrate.value,
        senderStatsTrack = callSettingsHolder.senderStatsEnabled.value,
        opusDtxEnabled = callSettingsHolder.opusDtxEnabled.value,
        publisherAudioFallback = callSettingsHolder.publisherAudioFallbackEnabled.value,
        subscriberAudioFallback = callSettingsHolder.subscriberAudioFallbackEnabled.value,
    )

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
    val isUserNameValid: Boolean = true,
    val publisher: PublisherParticipant? = null,
    val isSuccess: Boolean = false,
    val allowMicrophoneControl: Boolean = true,
    val allowCameraControl: Boolean = true,
    val audioDevicesState: AudioDevicesState? = null,
    val backgrounds: ImmutableList<VideoBackgroundItem> = persistentListOf(),
)

