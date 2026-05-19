package com.vonage.android.meetingroom.internal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt

/**
 * Standalone Activity entry point for the meeting room.
 *
 * Started via [MeetingRoomPrebuilt.launch]; for embedding in an existing Compose nav graph use
 * [MeetingRoomPrebuilt.content].
 *
 * The [MeetingRoomPrebuilt] instance is retrieved from [MeetingRoomPrebuiltHolder] and is
 * unavailable after process death. In that case the Activity finishes immediately (the live
 * call is already terminated on process death).
 */
internal class MeetingRoomActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prebuilt = MeetingRoomPrebuiltHolder.take()
        if (prebuilt == null) {
            finish()
            return
        }

        setContent {
            MeetingRoomContent(
                prebuilt = prebuilt,
                onActivityFinish = ::finish,
            )
        }
    }
}
