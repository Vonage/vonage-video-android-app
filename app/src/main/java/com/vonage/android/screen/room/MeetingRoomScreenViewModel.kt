package com.vonage.android.screen.room

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.config.GetConfig
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.notifications.VeraNotificationChannelRegistry.CallAction
import com.vonage.android.screen.components.audio.AudioDevicesHandler
import com.vonage.android.screen.components.audio.AudioDevicesState
import com.vonage.android.screensharing.ScreenSharingState
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.service.VeraForegroundServiceHandler
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.util.ActivityContextProvider
import com.vonage.android.util.noOpCall
import com.vonage.logger.vonageLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
@HiltViewModel(assistedFactory = MeetingRoomViewModelFactory::class)
class MeetingRoomScreenViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val sessionRepository: SessionRepository,
    private val vonageArchiving: VonageArchiving,
    private val vonageCaptions: VonageCaptions,
    private val vonageScreenSharing: VonageScreenSharing,
    private val videoClient: VonageVideoClient,
    private val foregroundServiceHandler: VeraForegroundServiceHandler,
    private val activityContextProvider: ActivityContextProvider,
    private val getConfig: GetConfig,
    private val audioDevicesHandler: AudioDevicesHandler,
    private val callSettingsHolder: CallSettingsHolder,
) : ViewModel() {

    private val context: Context
        get() = activityContextProvider.requireActivityContext()

    private val initialUiState = MeetingRoomUiState(
        roomName = roomName,
        isLoading = true,
        isEndCall = false,
    )
    private val _uiState = MutableStateFlow(initialUiState)
    val uiState: StateFlow<MeetingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = initialUiState,
    )

    private var call: CallFacade? = null

    init {
        foregroundServiceHandler
            .startForegroundService(roomName)
    }

    fun setup(context: Context) {
        // Set the activity context in the provider for future use
        activityContextProvider.setActivityContext(context)

        viewModelScope.launch {
            val config = getConfig()
            _uiState.update { uiState ->
                uiState.copy(
                    isLoading = true,
                    allowCameraControl = config.allowCameraControl,
                    allowMicrophoneControl = config.allowMicrophoneControl,
                    allowShowParticipantList = config.allowShowParticipantList,
                    audioDevicesState = audioDevicesHandler.audioDevicesState,
                )
            }
            sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    connect(roomName = roomName, sessionInfo = sessionInfo)
                }
                .onFailure {
                    _uiState.update { uiState -> uiState.copy(isLoading = false, isError = true) }
                }
        }

        foregroundServiceHandler
            .actions
            .onEach { callAction ->
                when (callAction) {
                    CallAction.HangUp -> {
                        _uiState.update { uiState -> uiState.copy(isEndCall = true) }
                    }

                    else -> {}
                }
            }
            .launchIn(viewModelScope)

        audioDevicesHandler.start()
        observePublisherSettings()
    }

    private fun connect(sessionInfo: SessionInfo, roomName: String) {
        viewModelScope.launch {
            call = videoClient.initializeSession(
                apiKey = sessionInfo.apiKey,
                sessionId = sessionInfo.sessionId,
                token = sessionInfo.token,
            )
            listenRemoteArchiving()
            call?.let { call ->
                vonageCaptions.init(call, roomName, sessionInfo.captionsId)
                callSettingsHolder.bind(call)
                // Update UI state after call is properly initialized
                _uiState.update { uiState ->
                    uiState.copy(
                        roomName = roomName,
                        call = call,
                        archivingUiState = ArchivingUiState.IDLE,
                        captionsUiState = if (sessionInfo.captionsId.isNullOrBlank()) {
                            CaptionsUiState.IDLE
                        } else {
                            CaptionsUiState.ENABLED
                        },
                        isLoading = false,
                        isError = false,
                    )
                }

                call.connect(context)
                    .onEach { sessionEvent ->
                        when (sessionEvent) {
                            is SessionEvent.Disconnected -> endCall()

                            is SessionEvent.Error -> {
                                _uiState.update { uiState ->
                                    uiState.copy(
                                        isError = true,
                                        errorMessage = sessionEvent.error.message
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                    .collect()
            }
        }
    }

    fun onToggleMic() {
        call?.toggleLocalAudio()
    }

    fun onToggleCamera() {
        call?.toggleLocalVideo()
    }

    fun onSwitchCamera() {
        call?.toggleLocalCamera()
    }

    fun onCycleLocalCameraBlur() {
        call?.cycleLocalCameraBlur()
    }

    fun endCall() {
        foregroundServiceHandler.stopForegroundService()
        vonageScreenSharing.stopSharingScreen()
        audioDevicesHandler.stop()
        callSettingsHolder.clear()
        call?.endSession()
    }

    private fun observePublisherSettings() {
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
                ),
            ) { it }
                .drop(1)
                .collect {
                    call?.let { activeCall ->
                        videoClient.configurePublisher(
                            PublisherConfig(
                                name = activeCall.publisher.value?.name.orEmpty(),
                                publishVideo = activeCall.publisher.value?.isCameraEnabled?.value ?: true,
                                publishAudio = activeCall.publisher.value?.isMicEnabled?.value ?: true,
                                blurLevel = com.vonage.android.kotlin.model.BlurLevel.NONE,
                                cameraIndex = activeCall.publisher.value?.camera?.value?.index ?: 1,
                                captureFrameRate = callSettingsHolder.captureFrameRate.value,
                                captureResolution = callSettingsHolder.captureResolution.value,
                                preferredVideoCodecOrder = callSettingsHolder.preferredVideoCodecOrder.value,
                                audioBitrate = callSettingsHolder.audioBitrate.value,
                                senderStatsTrack = callSettingsHolder.senderStatsEnabled.value,
                                opusDtxEnabled = callSettingsHolder.opusDtxEnabled.value,
                                publisherAudioFallback = callSettingsHolder.publisherAudioFallbackEnabled.value,
                                subscriberAudioFallback = callSettingsHolder.subscriberAudioFallbackEnabled.value,
                            ),
                        )
                        vonageLogger.w("PublisherFactory", "Refresh publisher from view model (${activeCall.publisher.value?.name})")
                        activeCall.refreshPublisher(context)
                    }
                }
        }
    }

    fun sendMessage(message: String) {
        call?.sendChatMessage(message)
    }

    fun listenUnread(enable: Boolean) {
        call?.listenUnreadChatMessages(enable)
    }

    fun sendEmoji(emoji: String) {
        call?.sendEmoji(emoji)
    }

    fun changeLayout(layoutType: CallLayoutType) {
        _uiState.update { uiState -> uiState.copy(layoutType = layoutType) }
    }

    fun onTogglePinParticipant(participantId: String) {
        call?.togglePinParticipant(participantId)
    }

    fun forceMuteParticipant(participantId: String) {
        call?.forceMuteParticipant(participantId)
    }

    //region Archiving
    fun archiveCall(enable: Boolean) {
        if (enable) {
            _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.STARTING) }
        } else {
            _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.STOPPING) }
        }
        viewModelScope.launch {
            if (enable) {
                // Start recording the call session
                vonageArchiving.startArchive(roomName)
                    .onSuccess {
                        _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.RECORDING) }
                    }
                    .onFailure {
                        _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.IDLE) }
                    }
            } else {
                // Stop recording the call session
                vonageArchiving.stopArchive(roomName)
                    .onSuccess {
                        _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.IDLE) }
                    }
                    .onFailure {
                        _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.RECORDING) }
                    }
            }
        }
    }

    private fun listenRemoteArchiving() {
        viewModelScope.launch {
            call?.let {
                // Listen for remote archiving state changes from other participants
                vonageArchiving.bind(it)
                    .onEach { archivingState ->
                        when (archivingState) {
                            is ArchivingState.Idle -> {}
                            is ArchivingState.Started -> {
                                _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.RECORDING) }
                            }

                            is ArchivingState.Stopped -> {
                                _uiState.update { uiState -> uiState.copy(archivingUiState = ArchivingUiState.IDLE) }
                            }
                        }
                    }
                    .collect()
            }
        }
    }
    //endregion

    //region Captions
    fun captions(enable: Boolean) {
        if (enable) {
            _uiState.update { uiState -> uiState.copy(captionsUiState = CaptionsUiState.ENABLING) }
        } else {
            _uiState.update { uiState -> uiState.copy(captionsUiState = CaptionsUiState.DISABLING) }
        }
        viewModelScope.launch {
            if (enable) {
                vonageCaptions.enable()
                    .onSuccess { _uiState.update { uiState -> uiState.copy(captionsUiState = CaptionsUiState.ENABLED) } }
                    .onFailure { _uiState.update { uiState -> uiState.copy(captionsUiState = CaptionsUiState.IDLE) } }
            } else {
                vonageCaptions.disable()
                    .onSuccess { _uiState.update { uiState -> uiState.copy(captionsUiState = CaptionsUiState.IDLE) } }
                    .onFailure { _uiState.update { uiState -> uiState.copy(captionsUiState = CaptionsUiState.ENABLED) } }
            }
        }
    }
    //endregion

    //region Screensharing
    fun startScreenSharing(intent: Intent) {
        call?.let {
            _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.STARTING) }
            vonageScreenSharing.startScreenSharing(
                call = it,
                intent = intent,
                onStarted = { _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.SHARING) } },
                onStopped = { _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.IDLE) } },
            )
        }
    }

    fun stopScreenSharing() {
        _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.STOPPING) }
        vonageScreenSharing.stopSharingScreen()
    }
    //endregion

    override fun onCleared() {
        super.onCleared()
        activityContextProvider.clearActivityContext()
    }

    private companion object {
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
    }
}

@AssistedFactory
fun interface MeetingRoomViewModelFactory {
    fun create(roomName: String): MeetingRoomScreenViewModel
}

@Immutable
data class MeetingRoomUiState(
    val roomName: String,
    val archivingUiState: ArchivingUiState = ArchivingUiState.IDLE,
    val captionsUiState: CaptionsUiState = CaptionsUiState.IDLE,
    val screenSharingState: ScreenSharingState = ScreenSharingState.IDLE,
    val audioDevicesState: AudioDevicesState? = null,
    val call: CallFacade = noOpCall,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isEndCall: Boolean = false,
    val layoutType: CallLayoutType = CallLayoutType.GRID,
    val allowMicrophoneControl: Boolean = true,
    val allowCameraControl: Boolean = true,
    val allowShowParticipantList: Boolean = true,
)

enum class CallLayoutType {
    GRID,
    SPEAKER_LAYOUT,
    ADAPTIVE_GRID,
}
