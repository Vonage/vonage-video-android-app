package com.vonage.android.screen.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MeetingRoomViewModelFactory::class)
class MeetingRoomScreenViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val sessionRepository: SessionRepository,
    private val archiveRepository: ArchiveRepository,
    private val videoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MeetingRoomUiState>(MeetingRoomUiState.Loading)
    val uiState: StateFlow<MeetingRoomUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = MeetingRoomUiState.Loading,
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
            sessionRepository.getSession(roomName)
                .onSuccess { sessionInfo ->
                    onSessionCreated(
                        roomName = roomName,
                        sessionInfo = sessionInfo,
                    )
                }
                .onFailure {
                    _uiState.value = MeetingRoomUiState.SessionError
                }
        }
    }

    private fun onSessionCreated(
        roomName: String,
        sessionInfo: SessionInfo,
    ) {
        connect(sessionInfo)
        call?.let {
            _uiState.value = MeetingRoomUiState.Content(
                roomName = roomName,
                call = it,
                isRecording = false,
            )
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
                    .onEach { audioLevel ->
                        _audioLevel.value = audioLevel
                    }.collect()
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
        viewModelScope.launch {
            if (enable) {
                archiveRepository.startArchive(roomName)
                    .onSuccess {
                        currentArchiveId = it
                        _uiState.value = MeetingRoomUiState.Content(
                            roomName = roomName,
                            call = call!!, // watch out!
                            isRecording = true,
                        )
                    }
            } else {
                currentArchiveId?.let { archiveId ->
                    archiveRepository.stopArchive(roomName, archiveId)
                        .onSuccess {
                            currentArchiveId = null
                            _uiState.value = MeetingRoomUiState.Content(
                                roomName = roomName,
                                call = call!!, // watch out!
                                isRecording = false,
                            )
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

sealed interface MeetingRoomUiState {
    data class Content(
        val roomName: String,
        val isRecording: Boolean,
        val call: CallFacade,
    ) : MeetingRoomUiState

    data object Loading : MeetingRoomUiState
    data object SessionError : MeetingRoomUiState
}
