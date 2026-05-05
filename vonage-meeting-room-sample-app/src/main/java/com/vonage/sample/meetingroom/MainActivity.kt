package com.vonage.sample.meetingroom

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.vonage.android.meetingroom.api.MeetingRoomBuilder
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.api.MeetingRoomSDKAction
import com.vonage.android.meetingroom.api.PublisherSettings

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                JoinScreen(
                    onJoinRoom = { baseUrl, roomName, username ->
                        launchMeetingRoom(baseUrl, roomName, username)
                    },
                )
            }
        }
    }

    private fun launchMeetingRoom(baseUrl: String, roomName: String, username: String) {
        val prebuilt: MeetingRoomPrebuilt = MeetingRoomBuilder(
            baseUrl = baseUrl,
            roomName = roomName,
        )
            .publisherSettings(
                PublisherSettings(username = username),
            )
            .onAction { action ->
                when (action) {
                    is MeetingRoomSDKAction.CallDidEnd -> {
                        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show()
                    }
                    is MeetingRoomSDKAction.GoBack -> {
                        Toast.makeText(this, "Left room: ${action.roomName}", Toast.LENGTH_SHORT).show()
                    }
                    is MeetingRoomSDKAction.ShareRoom -> {
                        Toast.makeText(this, "Share: ${action.roomName}", Toast.LENGTH_SHORT).show()
                    }
                    is MeetingRoomSDKAction.NavigateToSettings -> {
                        Toast.makeText(this, "Settings requested", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .isDebug(true)
            .build()

        // Launch meeting room as a standalone Activity
        prebuilt.launch(this)
    }
}
