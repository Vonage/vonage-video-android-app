package com.vonage.android.screen.room

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.SessionInfo
import com.vonage.android.data.SessionRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.service.CallAction
import com.vonage.android.service.CallActionsListener
import com.vonage.android.service.VeraNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @param:ApplicationContext val context: Context,
    private val sessionRepository: SessionRepository,
    private val videoClient: VonageVideoClient,
    private val notificationManager: VeraNotificationManager,
    private val callActionsListener: CallActionsListener,
    private val savedStateHandle: SavedStateHandle,
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
    private var initialized = false

    init {
//        if (!initialized) {
        if (savedStateHandle.get<Boolean>("initialized") == null) {
            setup()

            notificationManager
                .createNotificationChannel()
                .startForegroundService(roomName)
                .listenCallActions()
        }

        initialized = true
        savedStateHandle["initialized"] = false
    }

    fun setup() {
        viewModelScope.launch {
            _uiState.value = MeetingRoomUiState.Loading
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
        viewModelScope.launch {
            callActionsListener.actions.collect { callAction ->
                when (callAction) {
                    CallAction.HangUp -> {
                        _uiState.value = MeetingRoomUiState.EndCall
                    }

                    else -> {}
                }
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
        notificationManager.stopForegroundService()
        call?.endSession()
    }

    fun onPause() {
//        call?.pauseSession()
    }

    fun onResume() {
//        call?.resumeSession()
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
        val call: CallFacade,
    ) : MeetingRoomUiState

    data object Loading : MeetingRoomUiState
    data object SessionError : MeetingRoomUiState
    data object EndCall : MeetingRoomUiState
}
