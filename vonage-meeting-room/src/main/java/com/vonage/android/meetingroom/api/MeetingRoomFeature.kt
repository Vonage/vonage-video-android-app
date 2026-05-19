package com.vonage.android.meetingroom.api

/**
 * Runtime feature set for the meeting room.
 *
 * Compile-time Gradle flavors remain the backend mechanism. This enum acts as an additional
 * runtime filter: a feature is active only when its compile-time flavor is `enabled` **and** it
 * is present in the set passed to [MeetingRoomBuilder.enabledFeatures].
 *
 * Passing no value to [MeetingRoomBuilder.enabledFeatures] defaults to [MeetingRoomFeature.all],
 * which preserves the same behavior as before the builder API.
 */
@ExperimentalMeetingRoomApi
enum class MeetingRoomFeature {
    /** In-call text chat. */
    CHAT,

    /** Session recording. */
    ARCHIVING,

    /** Live captions overlay. */
    CAPTIONS,

    /** Emoji reactions with floating animation. */
    REACTIONS,

    /** In-call settings panel (resolution, codecs, stats). */
    SETTINGS,

    /** Screen sharing via MediaProjection. */
    SCREEN_SHARE,

    /** Background blur / virtual backgrounds. */
    BACKGROUND_EFFECTS,

    /** Advanced noise suppression. */
    AUDIO_EFFECTS;

    companion object {
        /** Convenience set containing every feature — used as the default. */
        val all: Set<MeetingRoomFeature> = entries.toSet()
    }
}
