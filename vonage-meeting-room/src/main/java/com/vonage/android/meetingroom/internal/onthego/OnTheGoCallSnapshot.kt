package com.vonage.android.meetingroom.internal.onthego

/** Facts the host copies out of a live participant before building a snapshot. */
internal data class OnTheGoParticipantFacts(
    val id: String,
    val name: String,
    val isPublisher: Boolean,
    val isScreenShare: Boolean,
    val isTalking: Boolean,
)

/** Someone else's screen share, identified without holding the video view. */
internal data class OnTheGoScreenShare(
    val id: String,
    val name: String,
)

/**
 * Immutable snapshot of the host call. Mic and camera state always come from here.
 * The controller never mirrors them locally.
 */
internal data class OnTheGoCallSnapshot(
    val isConnected: Boolean,
    val isMicEnabled: Boolean,
    val isCameraEnabled: Boolean,
    val participantCount: Int,
    val activeSpeakerName: String?,
    val screenShare: OnTheGoScreenShare?,
) {
    companion object {
        val disconnected = OnTheGoCallSnapshot(
            isConnected = false,
            isMicEnabled = false,
            isCameraEnabled = false,
            participantCount = 0,
            activeSpeakerName = null,
            screenShare = null,
        )
    }
}

/**
 * Maps host participants into an [OnTheGoCallSnapshot].
 *
 * The remote count excludes the local participant and every screen share.
 * The displayed speaker is the first remote camera participant who is talking.
 * A screen-share stream is never the displayed speaker. The local user's own
 * share is not offered as shared content.
 */
internal fun onTheGoSnapshot(
    isConnected: Boolean,
    isMicEnabled: Boolean,
    isCameraEnabled: Boolean,
    participants: List<OnTheGoParticipantFacts>,
): OnTheGoCallSnapshot {
    val remotes = participants.filter { !it.isPublisher && !it.isScreenShare }
    val share = participants.firstOrNull { it.isScreenShare && !it.isPublisher }
    return OnTheGoCallSnapshot(
        isConnected = isConnected,
        isMicEnabled = isMicEnabled,
        isCameraEnabled = isCameraEnabled,
        participantCount = remotes.size,
        activeSpeakerName = remotes.firstOrNull { it.isTalking }?.name,
        screenShare = share?.let { OnTheGoScreenShare(id = it.id, name = it.name) },
    )
}
