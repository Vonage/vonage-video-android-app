package com.vonage.android.meetingroom.internal

import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.logger.vonageLogger
import java.util.concurrent.atomic.AtomicReference

/**
 * Temporary holder used to pass a [MeetingRoomPrebuilt] to [MeetingRoomActivity].
 *
 * Because [MeetingRoomPrebuilt] contains non-parcelable fields (theme colors, action lambdas,
 * composable content), it cannot be serialized into an [android.os.Bundle]. Instead it is placed
 * here before the Activity is started and retrieved in [MeetingRoomActivity.onCreate].
 *
 * The reference is cleared once it is consumed, so a single launch at a time is supported.
 * If the process is killed while the Activity is in the background the prebuilt will be gone;
 * the Activity handles this by finishing immediately (the call is already terminated).
 */
internal object MeetingRoomPrebuiltHolder {

    const val EXTRA_HELD = "meetingRoom_prebuiltHeld"

    private val held: AtomicReference<MeetingRoomPrebuilt?> = AtomicReference(null)

    fun set(prebuilt: MeetingRoomPrebuilt) {
        if (held.get() != null) {
            vonageLogger.w(
                "MeetingRoomPrebuiltHolder",
                "Overwriting previously held MeetingRoomPrebuilt — was the previous launch consumed?",
            )
        }
        held.set(prebuilt)
    }

    fun take(): MeetingRoomPrebuilt? = held.getAndSet(null)
}
