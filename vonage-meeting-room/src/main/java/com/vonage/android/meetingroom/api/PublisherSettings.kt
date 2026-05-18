package com.vonage.android.meetingroom.api

import com.vonage.android.kotlin.model.VideoEffect

/**
 * Initial publisher configuration carried into the call.
 *
 * Pass an instance to [MeetingRoomBuilder.publisherSettings] to pre-populate the local
 * participant's display name and audio/video state — useful when a waiting room already
 * captured these preferences.
 *
 * @param username            Display name for the local participant. Defaults to empty string.
 * @param publishAudio        Whether the microphone starts enabled. Default `true`.
 * @param publishVideo        Whether the camera starts enabled. Default `true`.
 * @param initialVideoEffect  Video effect to apply as soon as the publisher starts.
 *                            Defaults to [VideoEffect.None]. Pass the effect selected in
 *                            the waiting room to preserve continuity.
 */
@ExperimentalMeetingRoomApi
data class PublisherSettings(
    val username: String = "",
    val publishAudio: Boolean = true,
    val publishVideo: Boolean = true,
    val initialVideoEffect: VideoEffect = VideoEffect.None,
)
