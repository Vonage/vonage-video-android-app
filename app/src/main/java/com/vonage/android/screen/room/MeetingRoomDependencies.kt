package com.vonage.android.screen.room

import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.meetingroom.ActivityContextProvider
import com.vonage.android.meetingroom.ConfigProvider
import com.vonage.android.meetingroom.ForegroundServiceHandler
import com.vonage.android.meetingroom.SessionProvider
import com.vonage.android.meetingroom.audio.AudioDevicesHandler
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.settings.CallSettingsHolder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@Suppress("LongParameterList")
class MeetingRoomDependencies @Inject constructor(
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
)

@EntryPoint
@InstallIn(ActivityComponent::class)
interface MeetingRoomDependenciesEntryPoint {
    fun meetingRoomDependencies(): MeetingRoomDependencies
}
