package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveType
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.fx.data.BackgroundsResult
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.meetingroom.api.MeetingRoomCallState
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer
import com.vonage.android.shared.session.SessionInfo
import com.vonage.android.meetingroom.internal.screen.CallLayoutType
import com.vonage.android.meetingroom.internal.screen.MeetingRoomUiState
import com.vonage.android.meetingroom.internal.screen.TranscriptionNotification
import com.vonage.android.meetingroom.internal.screen.toCallLayoutType
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler.CallAction
import com.vonage.android.screensharing.ScreenSharingState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

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
        allowDeviceSelection = prebuilt.configuration.allowDeviceSelection,
        allowFeedback = prebuilt.configuration.allowFeedback,
        allowSettings = prebuilt.configuration.allowSettings,
        layoutType = prebuilt.configuration.defaultLayoutMode.toCallLayoutType(),
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

    /** Archive types the local user asked to start whose started event has not arrived yet. */
    private val localArchiveStarts = mutableSetOf<ArchiveType>()
    private var transcriptionNotificationJob: Job? = null

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
                publishCaptions = container.vonageCaptions.isCapable,
                senderStatsTrack = container.callSettingsHolder.senderStatsEnabled.value,
                opusDtxEnabled = container.callSettingsHolder.opusDtxEnabled.value,
                publisherAudioFallback = container.callSettingsHolder.publisherAudioFallbackEnabled.value,
                subscriberAudioFallback = container.callSettingsHolder.subscriberAudioFallbackEnabled.value,
                videoBitrateConfig = container.callSettingsHolder.videoBitrateConfig.value,
                captureFrameRate = container.callSettingsHolder.captureFrameRate.value,
                captureResolution = container.callSettingsHolder.captureResolution.value,
                preferredVideoCodecOrder = container.callSettingsHolder.preferredVideoCodecOrder.value,
                audioBitrate = container.callSettingsHolder.audioBitrate.value,
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

            // Never keep the key of a previous room: it is written again once the session is created.
            container.sessionKeyHolder.sessionKey = null
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
            // The user may have left while the session was being fetched: never connect or
            // publish this session's key, it could overwrite the key of a newer meeting room.
            if (callEnded.get()) return@launch
            call = container.videoClient.initializeSession(
                apiKey = sessionInfo.applicationId,
                sessionId = sessionInfo.sessionId,
                token = sessionInfo.token,
            )
            listenRemoteArchiving()
            call?.let { activeCall ->
                container.sessionKeyHolder.sessionKey = sessionInfo.sessionKey
                container.vonageCaptions.init(activeCall, sessionInfo.sessionKey)
                container.callSettingsHolder.bind(activeCall)
                _uiState.update { state ->
                    state.copy(
                        roomName = roomName,
                        call = activeCall,
                        archivingUiState = ArchivingUiState.IDLE,
                        transcriptionUiState = ArchivingUiState.IDLE,
                        captionsUiState = CaptionsUiState.IDLE,
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
            uris.forEach { uri -> container.userBackgroundRepository.saveBackground(uri, resolution) }
            refreshBackgrounds()
        }
    }

    /**
     * Deletes the user-uploaded background identified by [item]. If the deleted background is
     * currently active on the publisher, the effect is reset to [VideoEffect.None].
     */
    fun deleteBackground(item: VideoBackgroundItem) {
        viewModelScope.launch {
            container.userBackgroundRepository.deleteBackground(item.id)
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
        container.callSettingsHolder.clearCall()
        call?.endSession()
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
    fun archiveCall(enable: Boolean) = toggleArchive(ArchiveType.RECORDING, enable)

    /**
     * Starts/stops the post-call transcription archive, independently of any recording.
     * [withSummary] only applies on start and adds an AI summary to the transcript.
     */
    fun transcribeCall(enable: Boolean, withSummary: Boolean = DEFAULT_TRANSCRIPTION_SUMMARY) =
        toggleArchive(ArchiveType.TRANSCRIPTION, enable, withSummary)

    private fun toggleArchive(type: ArchiveType, enable: Boolean, withSummary: Boolean = false) {
        if (enable) {
            localArchiveStarts += type
            updateArchiveUiState(type, ArchivingUiState.STARTING)
        } else {
            updateArchiveUiState(type, ArchivingUiState.STOPPING)
        }
        viewModelScope.launch {
            val sessionKey = container.sessionKeyHolder.sessionKey
            if (enable) {
                archiveResult(sessionKey) { container.vonageArchiving.startArchive(it, type, withSummary) }
                    .onSuccess { updateArchiveUiState(type, ArchivingUiState.RECORDING) }
                    .onFailure {
                        localArchiveStarts -= type
                        updateArchiveUiState(type, ArchivingUiState.IDLE)
                    }
            } else {
                archiveResult(sessionKey) { container.vonageArchiving.stopArchive(it, type) }
                    .onSuccess {
                        localArchiveStarts -= type
                        updateArchiveUiState(type, ArchivingUiState.IDLE)
                    }
                    .onFailure { updateArchiveUiState(type, ArchivingUiState.RECORDING) }
            }
        }
    }

    private fun updateArchiveUiState(type: ArchiveType, archiveUiState: ArchivingUiState) {
        _uiState.update { state ->
            when (type) {
                ArchiveType.RECORDING -> state.copy(archivingUiState = archiveUiState)
                ArchiveType.TRANSCRIPTION -> state.copy(transcriptionUiState = archiveUiState)
            }
        }
    }

    /** The session key is always set once connected; a missing key fails the action instead of calling the backend. */
    private suspend fun <T> archiveResult(
        sessionKey: String?,
        action: suspend (String) -> Result<T>,
    ): Result<T> =
        sessionKey?.let { action(it) } ?: Result.failure(IllegalStateException("Session key not available"))

    /**
     * Running archives are state: an archive type that appears (or changes id) has started and one
     * that disappears has stopped. Recording and transcription are tracked independently, so
     * stopping one never resets the other.
     */
    private fun listenRemoteArchiving() {
        viewModelScope.launch {
            call?.let {
                var previous = emptyMap<ArchiveType, ArchiveId>()
                container.vonageArchiving.bind(it)
                    .onEach { archives ->
                        ArchiveType.entries.forEach { type ->
                            val current = archives[type]
                            when {
                                current != null && current != previous[type] -> onArchiveStarted(type)
                                current == null && previous[type] != null -> onArchiveStopped(type)
                            }
                        }
                        previous = archives
                    }
                    .collect()
            }
        }
    }

    private fun onArchiveStarted(type: ArchiveType) {
        // Consume the local request so the next archive of this type is attributed correctly
        val startedByOthers = !localArchiveStarts.remove(type)
        when (type) {
            ArchiveType.RECORDING -> {
                _uiState.update { state ->
                    state.copy(
                        archivingUiState = ArchivingUiState.RECORDING,
                        recordingStartedByOthers = startedByOthers,
                    )
                }
                // Reset the overlay flag after the overlay duration
                if (startedByOthers) {
                    viewModelScope.launch {
                        delay(RECORDING_OVERLAY_DELAY_MS)
                        _uiState.update { state -> state.copy(recordingStartedByOthers = false) }
                    }
                }
            }
            // Everyone, including whoever started it, is told the meeting is being transcribed
            ArchiveType.TRANSCRIPTION -> updateTranscription(
                notification = TranscriptionNotification.STARTED,
                transcriptionUiState = ArchivingUiState.RECORDING,
            )
        }
    }

    private fun onArchiveStopped(type: ArchiveType) {
        when (type) {
            ArchiveType.RECORDING -> updateArchiveUiState(ArchiveType.RECORDING, ArchivingUiState.IDLE)
            ArchiveType.TRANSCRIPTION -> updateTranscription(
                notification = TranscriptionNotification.STOPPED,
                transcriptionUiState = ArchivingUiState.IDLE,
            )
        }
    }

    /** Applies [transcriptionUiState] and shows its toast, which clears itself after the overlay duration. */
    private fun updateTranscription(
        notification: TranscriptionNotification,
        transcriptionUiState: ArchivingUiState,
    ) {
        transcriptionNotificationJob?.cancel()
        _uiState.update { state ->
            state.copy(transcriptionUiState = transcriptionUiState, transcriptionNotification = notification)
        }
        transcriptionNotificationJob = viewModelScope.launch {
            delay(RECORDING_OVERLAY_DELAY_MS)
            _uiState.update { state -> state.copy(transcriptionNotification = null) }
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
        const val RECORDING_OVERLAY_DELAY_MS: Long = 5_000
    }
}

/** AI summary is on unless the user unchecks it, matching the backend's transcription default. */
internal const val DEFAULT_TRANSCRIPTION_SUMMARY = true
