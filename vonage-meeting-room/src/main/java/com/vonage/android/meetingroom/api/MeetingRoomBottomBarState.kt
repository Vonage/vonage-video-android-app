package com.vonage.android.meetingroom.api

/**
 * Minimal read-only call state passed to a custom bottom bar composable.
 *
 * Provided as the first parameter of the composable slot registered via
 * [MeetingRoomBuilder.bottomBar]. Contains only the fields needed to render a replacement bar;
 * use [MeetingRoomCustomActions] (the second parameter) to wire up user interactions.
 *
 * @param isMicEnabled          `true` when the local publisher's microphone is active.
 * @param isCameraEnabled       `true` when the local publisher's camera is active.
 * @param isScreenSharingActive `true` when a screen-sharing session is in progress.
 * @param isRecordingActive     `true` when session archiving/recording is in progress.
 * @param isCaptionsActive      `true` when live captions are enabled.
 */
@ExperimentalMeetingRoomApi
data class MeetingRoomBottomBarState(
    val isMicEnabled: Boolean,
    val isCameraEnabled: Boolean,
    val isScreenSharingActive: Boolean,
    val isRecordingActive: Boolean,
    val isCaptionsActive: Boolean,
)
