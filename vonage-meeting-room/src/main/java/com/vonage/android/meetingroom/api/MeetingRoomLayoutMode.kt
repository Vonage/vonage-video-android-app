package com.vonage.android.meetingroom.api

/**
 * Video layout used when entering the meeting room.
 *
 * Pass via [MeetingRoomConfiguration.defaultLayoutMode]. Users can still switch layout from the
 * bottom bar during a call; this only selects the initial one.
 */
@ExperimentalMeetingRoomApi
enum class MeetingRoomLayoutMode {
    /** Adaptive grid of equally sized participant tiles. */
    GRID,

    /** Large active speaker with a filmstrip of the remaining participants. */
    ACTIVE_SPEAKER;

    companion object {

        /**
         * Parses the value of `meetingRoomSettings.defaultLayoutMode` from the app config.
         *
         * Accepted values are `"grid"` and `"activespeaker"`, matching the shared cross-platform
         * config schema. Comparison ignores case and any `-`/`_` separators, so `"active_speaker"`
         * and `"active-speaker"` are also accepted.
         *
         * @return the matching mode, or [default] when [value] is not recognised.
         */
        fun fromConfigValue(
            value: String,
            default: MeetingRoomLayoutMode = GRID,
        ): MeetingRoomLayoutMode =
            when (value.lowercase().replace("-", "").replace("_", "")) {
                "grid" -> GRID
                "activespeaker" -> ACTIVE_SPEAKER
                else -> default
            }
    }
}
