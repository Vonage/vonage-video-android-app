package com.vonage.android.kotlin.model

import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.vonage.android.shared.ChatMessage
import com.vonage.android.shared.EmojiReaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Main facade interface for managing a video call.
 *
 * Combines session management, publisher controls, chat, emoji reactions, and screen sharing
 * into a unified API. Provides reactive state flows for participants, signals, and captions.
 */
@Stable
interface CallFacade : SessionFacade, PublisherFacade, ChatFacade, EmojiFacade, ScreenShareFacade {

    /**
     * Updates the flow of visible participants for bandwidth optimization.
     *
     * @param snapshotFlow Flow emitting list of currently visible participant IDs
     */
    fun updateParticipantVisibilityFlow(snapshotFlow: Flow<List<String>>)

    /** StateFlow of all participants in the call */
    val participantsStateFlow: StateFlow<ImmutableList<Participant>>

    /** StateFlow of pinned participant IDs */
    val pinnedParticipantIds: StateFlow<Set<String>>

    /**
     * Toggles the pin state of a participant.
     *
     * @param participantId The ID of the participant to pin/unpin
     */
    fun togglePinParticipant(participantId: String)

    /**
     * Forcibly mutes another participant's audio in the call.
     *
     * This is typically used by privileged roles (for example, host or moderator)
     * to mute a remote participant regardless of that participant's local mute state.
     * Actual permission enforcement is handled by the underlying call implementation.
     *
     * @param participantId The ID of the participant to mute
     */
    fun forceMuteParticipant(participantId: String)

    /** StateFlow of the current participant count */
    val participantsCount: StateFlow<Int>

    /** StateFlow of the currently active speaker based on audio levels */
    val activeSpeaker: StateFlow<Participant?>

    /**
     * Gets the state flow for chat messages.
     *
     * @return StateFlow emitting chat state including messages and unread count
     */
    val chatSignalState: StateFlow<ChatState?>

    /**
     * Gets the state flow for emoji reactions.
     *
     * @return StateFlow emitting emoji state with active reactions
     */
    val emojiSignalState: StateFlow<EmojiState?>

    /** StateFlow of caption lines from remote participants */
    val captionsStateFlow: StateFlow<ImmutableList<CaptionLine>>

    /** StateFlow of archiving of the session */
    val archivingStateFlow: StateFlow<ArchivingState>
}

/**
 * Interface for managing the local publisher (camera/microphone).
 */
interface PublisherFacade {
    val publisher: StateFlow<PublisherState?>
    fun toggleLocalVideo()
    fun toggleLocalCamera()
    fun toggleLocalAudio()
    fun applyLocalVideoEffect(effect: VideoEffect)
    fun setVideoBitrate(config: VideoBitrateConfig)
    fun setDegradationPreference(preference: DegradationPreference)
}

interface SessionFacade {
    fun connect(context: Context): Flow<SessionEvent>
    fun enableCaptions()
    fun disableCaptions()
    fun pauseSession()
    fun resumeSession()
    fun endSession()
}

interface ChatFacade {
    fun sendChatMessage(message: String)
    fun listenUnreadChatMessages(enable: Boolean)
}

fun interface EmojiFacade {
    fun sendEmoji(emoji: String)
}

interface ScreenShareFacade {
    fun startCapturingScreen(mediaProjection: MediaProjection)
    fun stopCapturingScreen()
}

enum class SignalType(val signal: String) {
    CHAT("chat"),
    REACTION("emoji");
}

/** Marker interface for signal state content */
sealed interface SignalStateContent

/**
 * State containing chat messages and unread count.
 *
 * @property unreadCount Number of unread messages
 * @property messages Immutable list of all chat messages
 */
@Immutable
data class ChatState(
    val unreadCount: Int = 0,
    val messages: ImmutableList<ChatMessage> = persistentListOf(),
) : SignalStateContent

/**
 * State containing active emoji reactions.
 *
 * @property reactions Immutable list of currently displayed reactions
 */
@Immutable
data class EmojiState(
    val reactions: ImmutableList<EmojiReaction> = persistentListOf(),
) : SignalStateContent

sealed interface ArchivingState {
    data object Idle : ArchivingState
    data class Started(val id: String) : ArchivingState
    data class Stopped(val id: String) : ArchivingState
}

@Immutable
data class CaptionLine(
    val streamId: String,
    val subscriberName: String,
    val isMe: Boolean,
    val text: String,
)
