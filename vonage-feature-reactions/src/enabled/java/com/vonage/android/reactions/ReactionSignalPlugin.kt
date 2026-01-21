package com.vonage.android.reactions

import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.signal.RawSignal
import com.vonage.android.kotlin.signal.SignalPlugin
import com.vonage.android.shared.EmojiReaction
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Signal plugin for handling emoji reactions in video calls.
 *
 * Manages emoji reactions sent by participants, displaying them temporarily
 * and automatically removing them after a defined lifetime.
 *
 * @param coroutineDispatcher Dispatcher for coroutine operations (defaults to Default)
 */
class ReactionSignalPlugin(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SignalPlugin {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)

    /** Thread-safe list of active emoji reactions */
    private val reactions: MutableList<EmojiReaction> = CopyOnWriteArrayList()

    private val _output = MutableStateFlow(EmojiState(reactions = persistentListOf()))
    override val output: StateFlow<SignalStateContent?> = _output

    override fun canHandle(signalType: String): Boolean = signalType == SignalType.REACTION.signal

    /**
     * Processes an incoming emoji reaction signal.
     *
     * Deserializes the signal, creates an EmojiReaction, and schedules its removal
     * after EMOJI_LIFETIME_MILLIS.
     */
    override fun handleSignal(type: String, data: String, senderName: String, isYou: Boolean) {
        if (!canHandle(type)) return

        val reactionSignal = try {
            Json.decodeFromString<ReactionSignal>(data)
        } catch (_: SerializationException) {
            return
        }

        val emojiReaction = EmojiReaction(
            id = reactionSignal.time,
            emoji = reactionSignal.emoji,
            startTime = reactionSignal.time,
            sender = senderName,
            isYou = isYou,
        )
        reactions.add(0, emojiReaction)

        // Remove reaction after 5 seconds
        coroutineScope.launch {
            delay(EMOJI_LIFETIME_MILLIS)
            reactions.removeAll { it.id == emojiReaction.id }
            _output.value = EmojiState(reactions = reactions.toImmutableList())
        }

        _output.value = EmojiState(reactions = reactions.toImmutableList())
    }

    /**
     * Formats an emoji into a reaction signal for sending.
     *
     * @param senderName Display name of the sender (unused in serialization)
     * @param message The emoji character to send
     * @return RawSignal containing type and JSON-encoded reaction data
     */
    override fun sendSignal(senderName: String, message: String): RawSignal {
        val signal = Json.encodeToString(
            ReactionSignal(
                emoji = message,
                time = System.currentTimeMillis(),
            )
        )
        return RawSignal(SignalType.REACTION.signal, signal)
    }
}

/** Duration in milliseconds that emoji reactions are displayed before auto-removal */
const val EMOJI_LIFETIME_MILLIS = 5000L

/**
 * Internal serializable format for emoji reaction signals.
 *
 * @property emoji The emoji character
 * @property time Timestamp for uniqueness and ordering
 */
@Serializable
internal data class ReactionSignal(
    val emoji: String,
    val time: Long,
)
