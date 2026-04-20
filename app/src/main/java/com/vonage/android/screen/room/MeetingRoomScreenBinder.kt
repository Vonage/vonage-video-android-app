package com.vonage.android.screen.room

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.meetingroom.ActivityContextProvider
import com.vonage.android.meetingroom.ConfigProvider
import com.vonage.android.meetingroom.ForegroundServiceHandler
import com.vonage.android.meetingroom.MeetingRoomScreenRoute
import com.vonage.android.meetingroom.MeetingRoomScreenViewModel
import com.vonage.android.meetingroom.SessionProvider
import com.vonage.android.meetingroom.audio.AudioDevicesHandler
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.settings.CallSettingsHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Wrapper composable that resolves dependencies via Hilt and creates the
 * meeting room ViewModel using the module's [MeetingRoomScreenViewModel.Factory].
 */
@Composable
fun MeetingRoomScreenBinder(
    roomName: String,
    navigateToGoodBye: () -> Unit,
    navigateToShare: (String) -> Unit,
    navigateToSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    deps: MeetingRoomDependenciesHolder = hiltViewModel(),
) {
    val meetingViewModel: MeetingRoomScreenViewModel = viewModel(
        key = roomName,
        factory = MeetingRoomScreenViewModel.Factory(
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
        ),
    )

    MeetingRoomScreenRoute(
        viewModel = meetingViewModel,
        navigateToGoodBye = navigateToGoodBye,
        navigateToShare = navigateToShare,
        navigateToSettings = navigateToSettings,
        onBack = onBack,
        modifier = modifier,
    )
}

/**
 * Thin Hilt ViewModel that holds meeting room dependencies so they can be
 * accessed from composables without passing them through the navigation graph.
 */
@HiltViewModel
class MeetingRoomDependenciesHolder @Inject constructor(
    val sessionProvider: SessionProvider,
    val vonageArchiving: VonageArchiving,
    val vonageCaptions: VonageCaptions,
    val vonageScreenSharing: VonageScreenSharing,
    val videoClient: VonageVideoClient,
    val foregroundServiceHandler: ForegroundServiceHandler,
    val activityContextProvider: ActivityContextProvider,
    val configProvider: ConfigProvider,
    val audioDevicesHandler: AudioDevicesHandler,
    val callSettingsHolder: CallSettingsHolder,
) : ViewModel()
