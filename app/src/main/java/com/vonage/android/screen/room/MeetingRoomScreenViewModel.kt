package com.vonage.android.screen.room

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.CaptionsRepository
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.notifications.VeraNotificationChannelRegistry.CallAction
import com.vonage.android.screensharing.ScreenSharingServiceListener
import com.vonage.android.screensharing.VeraScreenSharingManager
import com.vonage.android.service.VeraForegroundServiceHandler
import com.vonage.android.util.ActivityContextProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MeetingRoomViewModelFactory::class)
class MeetingRoomScreenViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val sessionRepository: SessionRepository,
    private val archiveRepository: ArchiveRepository,
    private val captionsRepository: CaptionsRepository,
    private val videoClient: VonageVideoClient,
    private val screenSharingManager: VeraScreenSharingManager,
    private val foregroundServiceHandler: VeraForegroundServiceHandler,
    private val activityContextProvider: ActivityContextProvider,
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
    private var currentArchiveId: String? = null
    private var currentCaptionsId: String? = null

    init {
        foregroundServiceHandler
            .startForegroundService(roomName)
    }

    fun setup(context: Context) {
        // Set the activity context in the provider for future use
        activityContextProvider.setActivityContext(context)
        
        viewModelScope.launch {
            _uiState.update { uiState -> uiState.copy(isLoading = true) }
            sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    onSessionCreated(
                        roomName = roomName,
                        sessionInfo = sessionInfo,
                    )
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
    }

    private fun onSessionCreated(
        roomName: String,
        sessionInfo: SessionInfo,
    ) {
        currentCaptionsId = sessionInfo.captionsId
        connect(sessionInfo, roomName)
    }

    @OptIn(FlowPreview::class)
    private fun connect(sessionInfo: SessionInfo, roomName: String) {
        viewModelScope.launch {
            videoClient.buildPublisher(context)
            call = videoClient.initializeSession(
                apiKey = sessionInfo.apiKey,
                sessionId = sessionInfo.sessionId,
                token = sessionInfo.token,
            )
            call?.let { call ->
                // Update UI state after call is properly initialized
                _uiState.update { uiState ->
                    uiState.copy(
                        roomName = roomName,
                        call = call,
                        recordingState = RecordingState.IDLE,
                        captionsState = if (sessionInfo.captionsId.isNullOrBlank()) {
                            CaptionsState.IDLE
                        } else {
                            CaptionsState.ENABLED
                        },
                        isLoading = false,
                        isError = false,
                    )
                }
                
                call.connect(context)
                    .onEach { sessionEvent ->
                        when (sessionEvent) {
                            is SessionEvent.Disconnected -> {
                                endCall()
                            }
                            is SessionEvent.Error -> {
                                _uiState.update { uiState -> uiState.copy(
                                    isError = true,
                                    errorMessage = sessionEvent.error.message,
                                ) }
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

    fun endCall() {
        foregroundServiceHandler.stopForegroundService()
        screenSharingManager.stopSharingScreen()
        call?.endSession()
    }

    fun onPause() {
        call?.pauseSession()
    }

    fun onResume() {
        call?.resumeSession()
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

    fun archiveCall(enable: Boolean) {
        if (enable) {
            _uiState.update { uiState -> uiState.copy(recordingState = RecordingState.STARTING) }
        } else {
            _uiState.update { uiState -> uiState.copy(recordingState = RecordingState.STOPPING) }
        }
        viewModelScope.launch {
            if (enable) {
                archiveRepository.startArchive(roomName)
                    .onSuccess {
                        currentArchiveId = it
                        _uiState.update { uiState -> uiState.copy(recordingState = RecordingState.RECORDING) }
                    }.onFailure {
                        _uiState.update { uiState -> uiState.copy(recordingState = RecordingState.IDLE) }
                    }
            } else {
                currentArchiveId?.let { archiveId ->
                    archiveRepository.stopArchive(roomName, archiveId)
                        .onSuccess {
                            currentArchiveId = null
                            _uiState.update { uiState -> uiState.copy(recordingState = RecordingState.IDLE) }
                        }.onFailure {
                            _uiState.update { uiState -> uiState.copy(recordingState = RecordingState.RECORDING) }
                        }
                }
            }
        }
    }

    fun captions(enable: Boolean) {
        viewModelScope.launch {
            if (enable) {
                enableCaptions()
            } else {
                disableCaptions()
            }
        }
    }

    private suspend fun enableCaptions() {
        _uiState.update { uiState -> uiState.copy(captionsState = CaptionsState.ENABLING) }
        captionsRepository.enableCaptions(roomName)
            .onSuccess {
                currentCaptionsId = it
                call?.enableCaptions(true)
                _uiState.update { uiState -> uiState.copy(captionsState = CaptionsState.ENABLED) }
            }
            .onFailure {
                _uiState.update { uiState -> uiState.copy(captionsState = CaptionsState.IDLE) }
            }
    }

    private suspend fun disableCaptions() {
        _uiState.update { uiState -> uiState.copy(captionsState = CaptionsState.DISABLING) }
        currentCaptionsId?.let {
            captionsRepository.disableCaptions(roomName, it)
                .onSuccess {
                    currentCaptionsId = null
                    call?.enableCaptions(false)
                    _uiState.update { uiState -> uiState.copy(captionsState = CaptionsState.IDLE) }
                }
                .onFailure {
                    _uiState.update { uiState -> uiState.copy(captionsState = CaptionsState.ENABLED) }
                }
        }
    }

    fun startScreenSharing(data: Intent) {
        _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.STARTING) }
        screenSharingManager.startScreenSharing(data, object : ScreenSharingServiceListener {
            override fun onStarted(mediaProjection: MediaProjection) {
                call?.startCapturingScreen(mediaProjection)
                _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.SHARING) }
            }

            override fun onStopped() {
                call?.stopCapturingScreen()
                _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.IDLE) }
            }
        })
    }

    fun stopScreenSharing() {
        _uiState.update { uiState -> uiState.copy(screenSharingState = ScreenSharingState.STOPPING) }
        screenSharingManager.stopSharingScreen()
    }

    fun changeLayout(layoutType: CallLayoutType) {
        _uiState.update { uiState -> uiState.copy(layoutType = layoutType) }
    }

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
    val recordingState: RecordingState = RecordingState.IDLE,
    val captionsState: CaptionsState = CaptionsState.IDLE,
    val screenSharingState: ScreenSharingState = ScreenSharingState.IDLE,
    val call: CallFacade = noOpCallFacade,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isEndCall: Boolean = false,
    val layoutType: CallLayoutType = CallLayoutType.GRID,
)

enum class CallLayoutType {
    GRID,
    SPEAKER_LAYOUT,
    ADAPTIVE_GRID,
}

enum class RecordingState {
    IDLE,
    STARTING,
    RECORDING,
    STOPPING,
}

enum class CaptionsState {
    IDLE,
    ENABLING,
    ENABLED,
    DISABLING,
}

enum class ScreenSharingState {
    IDLE,
    STARTING,
    SHARING,
    STOPPING,
}

@Suppress("EmptyFunctionBlock")
val noOpCallFacade = object : CallFacade {
    override fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>) {}

    override val participantsStateFlow: StateFlow<ImmutableList<ParticipantState>> = MutableStateFlow(persistentListOf())
    override val participantsCount: StateFlow<Int> = MutableStateFlow(1)
    override val activeSpeaker: StateFlow<Participant?> = MutableStateFlow(null)
    override val signalStateFlow: StateFlow<SignalState?> = MutableStateFlow(null)
    override val captionsStateFlow: StateFlow<String?> = MutableStateFlow(null)

    override fun signalState(signalType: SignalType): StateFlow<SignalStateContent?> = MutableStateFlow(null)
    override fun chatSignalState(): StateFlow<ChatState?> = MutableStateFlow(null)
    override val emojiSignalState: StateFlow<EmojiState?> = MutableStateFlow(null)

    override fun connect(context: Context): Flow<SessionEvent> = flowOf()
    override fun enableCaptions(enable: Boolean) { /* empty on purpose */ }
    override fun pauseSession() { /* empty on purpose */ }
    override fun resumeSession() { /* empty on purpose */ }
    override fun endSession() { /* empty on purpose */ }

    override val publisher: StateFlow<PublisherState?> = MutableStateFlow(null)

    override fun toggleLocalVideo() { /* empty on purpose */ }
    override fun toggleLocalCamera() { /* empty on purpose */ }
    override fun toggleLocalAudio() { /* empty on purpose */ }
    override fun sendChatMessage(message: String) { /* empty on purpose */ }
    override fun listenUnreadChatMessages(enable: Boolean) { /* empty on purpose */ }
    override fun sendEmoji(emoji: String) { /* empty on purpose */ }
    override fun startCapturingScreen(mediaProjection: MediaProjection) { /* empty on purpose */ }
    override fun stopCapturingScreen() { /* empty on purpose */ }
}
