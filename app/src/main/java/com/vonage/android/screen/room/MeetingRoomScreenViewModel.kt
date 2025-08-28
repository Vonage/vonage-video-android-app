package com.vonage.android.screen.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.SessionEvent
import com.vonage.android.kotlin.model.SignalState
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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MeetingRoomViewModelFactory::class)
class MeetingRoomScreenViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val sessionRepository: SessionRepository,
    private val archiveRepository: ArchiveRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val initialUiState = MeetingRoomUiState(
        roomName = roomName,
        isLoading = true,
    )
    private val _uiState = MutableStateFlow(initialUiState)
    val uiState: StateFlow<MeetingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = initialUiState,
    )

    private val _audioLevel = MutableStateFlow(0F)
    val audioLevel: StateFlow<Float> = _audioLevel.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = 0F,
    )

    private var call: CallFacade? = null
    private var currentArchiveId: String? = null


    init {
        setup()
    }

    fun setup() {
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
                    _uiState.update { uiState -> uiState.copy(isError = true) }
                }
        }
    }

    private fun onSessionCreated(
        roomName: String,
        sessionInfo: SessionInfo,
    ) {
        connect(sessionInfo)
        call?.let { call ->
            _uiState.update { uiState ->
                uiState.copy(
                    roomName = roomName,
                    call = call,
                    recordingState = RecordingState.IDLE,
                    isLoading = false,
                    isError = false,
                )
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun connect(sessionInfo: SessionInfo) {
        videoClient.buildPublisher()
        call = videoClient.initializeSession(
            apiKey = sessionInfo.apiKey,
            sessionId = sessionInfo.sessionId,
            token = sessionInfo.token,
        )
        viewModelScope.launch {
            call?.connect()?.collect()
        }
        viewModelScope.launch {
            call?.let {
                it.observeLocalAudioLevel()
                    .distinctUntilChanged()
                    .debounce(PUBLISHER_AUDIO_LEVEL_DEBOUNCE_MS)
                    .onEach { audioLevel -> _audioLevel.value = audioLevel }.collect()
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

    fun archiveCall(enable: Boolean, roomName: String) {
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

    private companion object {
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
        const val PUBLISHER_AUDIO_LEVEL_DEBOUNCE_MS = 36L
    }
}

@AssistedFactory
interface MeetingRoomViewModelFactory {
    fun create(roomName: String): MeetingRoomScreenViewModel
}

data class MeetingRoomUiState(
    val roomName: String,
    val recordingState: RecordingState = RecordingState.IDLE,
    val call: CallFacade = noOpCallFacade,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)

enum class RecordingState {
    IDLE,
    STARTING,
    RECORDING,
    STOPPING,
}

@Suppress("EmptyFunctionBlock")
private val noOpCallFacade = object : CallFacade {
    override val participantsStateFlow: StateFlow<ImmutableList<Participant>> = MutableStateFlow(persistentListOf())
    override val signalStateFlow: StateFlow<SignalState?> = MutableStateFlow(null)
    override fun connect(): Flow<SessionEvent> = flowOf()
    override fun pauseSession() {}
    override fun resumeSession() {}
    override fun endSession() {}
    override fun observeLocalAudioLevel(): Flow<Float> = flowOf()
    override fun toggleLocalVideo() {}
    override fun toggleLocalCamera() {}
    override fun toggleLocalAudio() {}
    override fun sendChatMessage(message: String) {}
    override fun listenUnreadChatMessages(enable: Boolean) {}
    override fun sendEmoji(emoji: String) {}
}
