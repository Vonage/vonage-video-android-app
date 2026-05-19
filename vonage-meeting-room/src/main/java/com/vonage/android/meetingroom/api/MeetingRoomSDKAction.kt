package com.vonage.android.meetingroom.api

/**
 * Navigation callbacks emitted by the meeting room SDK.
 *
 * Register a handler via [MeetingRoomBuilder.onAction]. The host app must handle all cases;
 * alerts (permission prompts, errors) are presented automatically by the SDK.
 */
@ExperimentalMeetingRoomApi
sealed class MeetingRoomSDKAction {

    /** The call ended — navigate to the goodbye or home screen. */
    data object CallDidEnd : MeetingRoomSDKAction()

    /**
     * The user tapped the back / leave button without ending the call for others.
     *
     * @param roomName The name of the room that was left.
     */
    data class GoBack(val roomName: String) : MeetingRoomSDKAction()

    /**
     * The user tapped the share button — share a link to the meeting room.
     *
     * @param roomName The name of the room to share.
     */
    data class ShareRoom(val roomName: String) : MeetingRoomSDKAction()

    /** The user tapped the settings button — navigate to the in-call settings screen. */
    data object NavigateToSettings : MeetingRoomSDKAction()
}
