package com.vonage.android.meetingroom.api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vonage.android.compose.theme.VonageVideoTheme

/**
 * Standalone Activity entry point for the meeting room.
 *
 * Launch via [MeetingRoom.launch]; for embedding in an existing nav graph use [MeetingRoomComponent].
 */
class MeetingRoomActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val config = MeetingRoomConfig.fromBundle(intent.extras ?: Bundle())

        setContent {
            VonageVideoTheme {
                MeetingRoomComponent(
                    config = config,
                    onCallEnd = { finish() },
                    isDebug = intent.getBooleanExtra(EXTRA_IS_DEBUG, false),
                )
            }
        }
    }

    companion object {
        const val EXTRA_IS_DEBUG = "meetingRoom_isDebug"
    }
}

/**
 * Helper object for launching [MeetingRoomActivity].
 *
 * Usage:
 * ```kotlin
 * MeetingRoom.launch(context, MeetingRoomConfig(baseUrl = "https://...", roomName = "my-room"))
 * ```
 */
object MeetingRoom {

    /**
     * Launch [MeetingRoomActivity] from any context.
     *
     * @param context   The calling context.
     * @param config    Meeting room configuration.
     * @param isDebug   When true, enables verbose HTTP logging.
     */
    fun launch(
        context: Context,
        config: MeetingRoomConfig,
        isDebug: Boolean = false,
    ) {
        val intent = Intent(context, MeetingRoomActivity::class.java).apply {
            putExtras(config.toBundle())
            putExtra(MeetingRoomActivity.EXTRA_IS_DEBUG, isDebug)
        }
        context.startActivity(intent)
    }
}
