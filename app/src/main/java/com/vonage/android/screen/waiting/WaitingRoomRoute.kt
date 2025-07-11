package com.vonage.android.screen.waiting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.compose.permissions.LaunchVideoCallPermissions

@Composable
fun WaitingRoomRoute(
    modifier: Modifier = Modifier,
    apiKey: String,
    sessionId: String,
    token: String,
) {
    LaunchVideoCallPermissions(Unit)

    WaitingRoomScreen(
        modifier = modifier,
        roomName = "test-name",
        participant = CreatePublisherUseCase().invoke(LocalContext.current),
    )
}
