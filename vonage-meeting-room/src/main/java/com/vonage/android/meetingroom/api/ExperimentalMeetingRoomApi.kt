package com.vonage.android.meetingroom.api

/**
 * Marks the MeetingRoom SDK API as experimental.
 *
 * Any API annotated with [ExperimentalMeetingRoomApi] may change or be removed in a future
 * release without a deprecation cycle. To use these APIs, opt in explicitly:
 *
 * - At the call site: `@OptIn(ExperimentalMeetingRoomApi::class)`
 * - For a whole module: add `-opt-in=com.vonage.android.meetingroom.api.ExperimentalMeetingRoomApi`
 *   to `freeCompilerArgs` in the module's `kotlinOptions` block.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "The MeetingRoom SDK API is experimental and may change without notice. " +
        "Opt in with @OptIn(ExperimentalMeetingRoomApi::class).",
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
)
@MustBeDocumented
annotation class ExperimentalMeetingRoomApi
