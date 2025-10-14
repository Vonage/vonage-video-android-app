package com.vonage.android.kotlin.signal

import com.opentok.android.Session
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.SignalStateContent
import com.vonage.android.kotlin.model.SignalType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.util.concurrent.CopyOnWriteArrayList

class ReactionSignalPlugin(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SignalPlugin {

    private val coroutineScope = CoroutineScope(coroutineDispatcher)
    private val reactions: MutableList<EmojiReaction> = CopyOnWriteArrayList()

    override fun canHandle(signalType: String): Boolean = signalType == SignalType.REACTION.signal

    override fun handleSignal(
        type: String,
        data: String,
        senderName: String,
        isYou: Boolean,
        callback: (SignalStateContent) -> Unit,
    ): SignalStateContent? {
        if (!canHandle(type)) return null

        val reactionSignal = try {
            Json.decodeFromString<ReactionSignal>(data)
        } catch (_: SerializationException) {
            return null
        }

        val emojiReaction = EmojiReaction(
            id = reactionSignal.time,
            emoji = reactionSignal.emoji,
            startTime = reactionSignal.time,
            sender = senderName,
            isYou = isYou,
        )
        reactions.add(0, emojiReaction)

        // remove reaction after 5 seconds
        coroutineScope.launch {
            delay(EMOJI_LIFETIME_MILLIS)
            reactions.removeAll { it.id == emojiReaction.id }
            callback(EmojiState(reactions = reactions.toImmutableList()))
        }

        return EmojiState(reactions = reactions.toImmutableList())
    }

    override fun sendSignal(session: Session, senderName: String, message: String) {
        val signal = Json.encodeToString(
            ReactionSignal(
                emoji = message,
                time = System.currentTimeMillis(),
            )
        )
        session.sendSignal(SignalType.REACTION.signal, signal)
    }
}

const val EMOJI_LIFETIME_MILLIS = 5000L

data class EmojiReaction(
    val id: Long,
    val emoji: String,
    val sender: String,
    val isYou: Boolean,
    val startTime: Long,
)

@Serializable
internal data class ReactionSignal(
    val emoji: String,
    val time: Long,
)
