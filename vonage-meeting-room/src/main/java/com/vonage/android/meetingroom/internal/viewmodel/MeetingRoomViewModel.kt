package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.fx.data.BackgroundsResult
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.api.MeetingRoomCallState
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer
import com.vonage.android.meetingroom.internal.data.SessionInfo
import com.vonage.android.meetingroom.internal.screen.CallLayoutType
import com.vonage.android.meetingroom.internal.screen.MeetingRoomUiState
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler.CallAction
import com.vonage.android.screensharing.ScreenSharingState
import com.vonage.logger.vonageLogger
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
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

@Suppress("LongParameterList")
internal class MeetingRoomViewModel(
    private val container: MeetingRoomContainer,
) : ViewModel() {

    private val prebuilt: MeetingRoomPrebuilt get() = container.prebuilt
    private val roomName: String get() = prebuilt.roomName

    private val context: Context
        get() = container.activityContextHolder.requireActivityContext()

    private val initialUiState = MeetingRoomUiState(
        roomName = roomName,
        isLoading = true,
        isEndCall = false,
        allowCameraControl = prebuilt.configuration.allowCameraControl,
        allowMicrophoneControl = prebuilt.configuration.allowMicrophoneControl,
        allowShowParticipantList = prebuilt.configuration.allowShowParticipantList,
        enabledFeatures = prebuilt.enabledFeatures,
    )
    private val _uiState = MutableStateFlow(initialUiState)
    val uiState: StateFlow<MeetingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = initialUiState,
    )

    private var call: CallFacade? = null
    private val callEnded = AtomicBoolean(false)

    init {
        if (prebuilt.foregroundServiceEnabled) {
            container.foregroundServiceHandler.startForegroundService(roomName)
        }
        viewModelScope.launch {
            prebuilt.hangUpCommand.collect {
                if (!callEnded.get()) {
                    _uiState.update { state -> state.copy(isEndCall = true) }
                }
            }
        }
        observeUiStateForPublicBridge()
    }

    fun setup(context: Context) {
        container.activityContextHolder.setActivityContext(context)

        // Apply initial publisher settings from PublisherSettings
        container.videoClient.configurePublisher(
            PublisherConfig(
                name = prebuilt.publisherSettings.username,
                publishAudio = prebuilt.publisherSettings.publishAudio,
                publishVideo = prebuilt.publisherSettings.publishVideo,
                initialVideoEffect = prebuilt.publisherSettings.initialVideoEffect,
                cameraIndex = 1, // default to front camera
            ),
        )

        viewModelScope.launch { refreshBackgrounds() }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    audioDevicesState = container.audioDevicesHandler.audioDevicesState,
                )
            }

            container.sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    connect(roomName = roomName, sessionInfo = sessionInfo)
                }
                .onFailure {
                    _uiState.update { state -> state.copy(isLoading = false, isError = true) }
                }
        }

        if (prebuilt.foregroundServiceEnabled) {
            container.foregroundServiceHandler.actions
                .onEach { callAction ->
                    when (callAction) {
                        CallAction.HangUp -> _uiState.update { state -> state.copy(isEndCall = true) }
                        else -> {}
                    }
                }
                .launchIn(viewModelScope)
        }

        container.audioDevicesHandler.start()
        observePublisherSettings()
    }

    /** Bridges the internal [MeetingRoomUiState] to the public [MeetingRoomCallState]. */
    private fun observeUiStateForPublicBridge() {
        uiState
            .onEach { state ->
                val activeCall = state.call
                val publisher = activeCall?.publisher?.value
                prebuilt.updateCallState(
                    MeetingRoomCallState(
                        isConnected = activeCall != null && !state.isLoading,
                        participantCount = activeCall?.participantsCount?.value ?: 0,
                        isLocalMicEnabled = publisher?.isMicEnabled?.value ?: prebuilt.publisherSettings.publishAudio,
                        isLocalCameraEnabled = publisher?.isCameraEnabled?.value ?: prebuilt.publisherSettings.publishVideo,
                        roomName = state.roomName,
                    ),
                )
            }
            .launchIn(viewModelScope)
    }

    private fun connect(
        sessionInfo: SessionInfo,
        roomName: String,
    ) {
        viewModelScope.launch {
            call = container.videoClient.initializeSession(
                apiKey = sessionInfo.apiKey,
                sessionId = sessionInfo.sessionId,
                token = sessionInfo.token,
            )
            listenRemoteArchiving()
            call?.let { activeCall ->
                container.vonageCaptions.init(activeCall, roomName, sessionInfo.captionsId)
                container.callSettingsHolder.bind(activeCall)
                _uiState.update { state ->
                    state.copy(
                        roomName = roomName,
                        call = activeCall,
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

                activeCall.connect(context)
                    .onEach { sessionEvent ->
                        when (sessionEvent) {
                            is SessionEvent.Disconnected -> endCall()
                            is SessionEvent.Error -> {
                                _uiState.update { state ->
                                    state.copy(
                                        isError = true,
                                        errorMessage = sessionEvent.error.message,
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

    fun onToggleMic() { call?.toggleLocalAudio() }

    fun onToggleCamera() { call?.toggleLocalVideo() }

    fun onSwitchCamera() { call?.toggleLocalCamera() }

    /**
     * Applies [effect] to the real session publisher immediately.
     * Invoked when the user selects an effect tile in the effects sheet.
     */
    fun applyVideoEffect(effect: VideoEffect) {
        call?.applyLocalVideoEffect(effect)
    }

    /**
     * Saves each image in [uris] to persistent storage and refreshes the backgrounds list.
     * Images are saved sequentially; saves that hit the cap or encounter an unreadable URI are
     * silently skipped (the repository returns `null` for those).
     */
    fun addBackground(uris: List<Uri>) {
        viewModelScope.launch {
            val resolution = container.callSettingsHolder.captureResolution.value
            uris.forEach { uri -> container.addBackgroundUseCase(uri, resolution) }
            refreshBackgrounds()
        }
    }

    /**
     * Deletes the user-uploaded background identified by [item]. If the deleted background is
     * currently active on the publisher, the effect is reset to [VideoEffect.None].
     */
    fun deleteBackground(item: VideoBackgroundItem) {
        viewModelScope.launch {
            container.deleteBackgroundUseCase(item.id)
            val currentEffect = call?.publisher?.value?.videoEffect?.value
            if (currentEffect is VideoEffect.BackgroundImage && currentEffect.id == item.id) {
                applyVideoEffect(VideoEffect.None)
            }
            refreshBackgrounds()
        }
    }

    /**
     * Fetches the merged backgrounds list via [GetBackgroundsUseCase] and updates the UI state.
     */
    private suspend fun refreshBackgrounds() {
        val resolution = container.callSettingsHolder.captureResolution.value
        val result = runCatching {
            container.getBackgroundsUseCase(resolution)
        }.getOrElse { BackgroundsResult(persistentListOf(), remainingBackgroundSlots = UserBackgroundRepository.MAX_USER_BACKGROUNDS) }

        _uiState.update { it.copy(backgrounds = result.backgrounds, remainingBackgroundSlots = result.remainingBackgroundSlots) }
    }

    fun endCall() {
        if (!callEnded.compareAndSet(false, true)) return
        if (prebuilt.foregroundServiceEnabled) {
            container.foregroundServiceHandler.stopForegroundService()
        }
        container.vonageScreenSharing.stopSharingScreen()
        container.audioDevicesHandler.stop()
        container.callSettingsHolder.clear()
        call?.endSession()
    }

    private fun observePublisherSettings() {
        viewModelScope.launch {
            val holder = container.callSettingsHolder
            combine(
                listOf<Flow<Any?>>(
                    holder.captureFrameRate,
                    holder.captureResolution,
                    holder.preferredVideoCodecOrder,
                    holder.audioBitrate,
                    holder.opusDtxEnabled,
                    holder.publisherAudioFallbackEnabled,
                    holder.subscriberAudioFallbackEnabled,
                    holder.senderStatsEnabled,
                ),
            ) { it }
                .drop(1)
                .collect {
                    call?.let { activeCall ->
                        container.videoClient.configurePublisher(
                            PublisherConfig(
                                // Prefer the name set by PublisherSettings; fall back to the active publisher name
                                name = prebuilt.publisherSettings.username.ifEmpty {
                                    activeCall.publisher.value?.name.orEmpty()
                                },
                                publishVideo = activeCall.publisher.value?.isCameraEnabled?.value ?: true,
                                publishAudio = activeCall.publisher.value?.isMicEnabled?.value ?: true,
                                initialVideoEffect = activeCall.publisher.value?.videoEffect?.value ?: VideoEffect.None,
                                cameraIndex = activeCall.publisher.value?.camera?.value?.index ?: 1,
                                captureFrameRate = holder.captureFrameRate.value,
                                captureResolution = holder.captureResolution.value,
                                preferredVideoCodecOrder = holder.preferredVideoCodecOrder.value,
                                audioBitrate = holder.audioBitrate.value,
                                senderStatsTrack = holder.senderStatsEnabled.value,
                                opusDtxEnabled = holder.opusDtxEnabled.value,
                                publisherAudioFallback = holder.publisherAudioFallbackEnabled.value,
                                subscriberAudioFallback = holder.subscriberAudioFallbackEnabled.value,
                            ),
                        )
                        vonageLogger.d("MeetingRoomViewModel", "Refresh publisher (${activeCall.publisher.value?.name})")
                        activeCall.refreshPublisher(context)
                    }
                }
        }
    }

    fun sendMessage(message: String) { call?.sendChatMessage(message) }

    fun listenUnread(enable: Boolean) { call?.listenUnreadChatMessages(enable) }

    fun sendEmoji(emoji: String) { call?.sendEmoji(emoji) }

    fun changeLayout(layoutType: CallLayoutType) {
        _uiState.update { state -> state.copy(layoutType = layoutType) }
    }

    fun onTogglePinParticipant(participantId: String) { call?.togglePinParticipant(participantId) }

    fun forceMuteParticipant(participantId: String) { call?.forceMuteParticipant(participantId) }

    //region Archiving
    fun archiveCall(enable: Boolean) {
        if (enable) {
            _uiState.update { state -> state.copy(archivingUiState = ArchivingUiState.STARTING) }
        } else {
            _uiState.update { state -> state.copy(archivingUiState = ArchivingUiState.STOPPING) }
        }
        viewModelScope.launch {
            if (enable) {
                container.vonageArchiving.startArchive(roomName)
                    .onSuccess { _uiState.update { state -> state.copy(archivingUiState = ArchivingUiState.RECORDING) } }
                    .onFailure { _uiState.update { state -> state.copy(archivingUiState = ArchivingUiState.IDLE) } }
            } else {
                container.vonageArchiving.stopArchive(roomName)
                    .onSuccess { _uiState.update { state -> state.copy(archivingUiState = ArchivingUiState.IDLE) } }
                    .onFailure { _uiState.update { state -> state.copy(archivingUiState = ArchivingUiState.RECORDING) } }
            }
        }
    }

    private fun listenRemoteArchiving() {
        viewModelScope.launch {
            call?.let {
                container.vonageArchiving.bind(it)
                    .onEach { archivingState ->
                        when (archivingState) {
                            is ArchivingState.Idle -> {}
                            is ArchivingState.Started -> _uiState.update { state ->
                                state.copy(archivingUiState = ArchivingUiState.RECORDING)
                            }
                            is ArchivingState.Stopped -> _uiState.update { state ->
                                state.copy(archivingUiState = ArchivingUiState.IDLE)
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
            _uiState.update { state -> state.copy(captionsUiState = CaptionsUiState.ENABLING) }
        } else {
            _uiState.update { state -> state.copy(captionsUiState = CaptionsUiState.DISABLING) }
        }
        viewModelScope.launch {
            if (enable) {
                container.vonageCaptions.enable()
                    .onSuccess { _uiState.update { state -> state.copy(captionsUiState = CaptionsUiState.ENABLED) } }
                    .onFailure { _uiState.update { state -> state.copy(captionsUiState = CaptionsUiState.IDLE) } }
            } else {
                container.vonageCaptions.disable()
                    .onSuccess { _uiState.update { state -> state.copy(captionsUiState = CaptionsUiState.IDLE) } }
                    .onFailure { _uiState.update { state -> state.copy(captionsUiState = CaptionsUiState.ENABLED) } }
            }
        }
    }
    //endregion

    //region Screensharing
    fun startScreenSharing(intent: Intent) {
        call?.let {
            _uiState.update { state -> state.copy(screenSharingState = ScreenSharingState.STARTING) }
            container.vonageScreenSharing.startScreenSharing(
                call = it,
                intent = intent,
                onStarted = { _uiState.update { state -> state.copy(screenSharingState = ScreenSharingState.SHARING) } },
                onStopped = { _uiState.update { state -> state.copy(screenSharingState = ScreenSharingState.IDLE) } },
            )
        }
    }

    fun stopScreenSharing() {
        _uiState.update { state -> state.copy(screenSharingState = ScreenSharingState.STOPPING) }
        container.vonageScreenSharing.stopSharingScreen()
    }
    //endregion

    override fun onCleared() {
        super.onCleared()
        endCall()
        container.activityContextHolder.clearActivityContext()
    }

    private companion object {
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
    }
}
