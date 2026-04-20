package com.vonage.android.screen.room

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.meetingroom.MeetingRoomScreenRoute
import com.vonage.android.meetingroom.MeetingRoomScreenViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun MeetingRoomScreenBinder(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as Activity
    val deps = EntryPointAccessors.fromActivity(
        activity,
        MeetingRoomDependenciesEntryPoint::class.java,
    ).meetingRoomDependencies()
    val factory = MeetingRoomScreenViewModel.Factory(
        roomName = roomName,
        sessionProvider = deps.sessionProvider,
        vonageArchiving = deps.vonageArchiving,
        vonageCaptions = deps.vonageCaptions,
        vonageScreenSharing = deps.vonageScreenSharing,
        videoClient = deps.videoClient,
        foregroundServiceHandler = deps.foregroundServiceHandler,
        activityContextProvider = deps.activityContextProvider,
        configProvider = deps.configProvider,
        audioDevicesHandler = deps.audioDevicesHandler,
        callSettingsHolder = deps.callSettingsHolder,
    )

    MeetingRoomScreenRoute(
        roomName = roomName,
        viewModelFactory = factory,
        navigateToGoodBye = navigateToGoodBye,
        navigateToShare = navigateToShare,
        navigateToSettings = navigateToSettings,
        onBack = onBack,
        modifier = modifier,
    )
}
