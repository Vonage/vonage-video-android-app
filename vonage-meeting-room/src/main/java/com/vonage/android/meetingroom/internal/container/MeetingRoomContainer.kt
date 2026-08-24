package com.vonage.android.meetingroom.internal.container

import android.content.Context
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.chat.ChatModule
import com.vonage.android.fx.data.BackgroundEffectsRepository
import com.vonage.android.fx.data.DefaultBackgroundEffectsRepository
import com.vonage.android.fx.data.DefaultUserBackgroundRepository
import com.vonage.android.fx.data.GetBackgroundsUseCase
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.kotlin.VonageVideoClient
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.sdk.VonageSdkFactory
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.internal.data.MeetingRoomApiService
import com.vonage.android.meetingroom.internal.data.MeetingRoomNetworkFactory
import com.vonage.android.meetingroom.internal.data.MeetingRoomSessionRepository
import com.vonage.android.meetingroom.internal.factory.createVonageArchiving
import com.vonage.android.meetingroom.internal.factory.createVonageCaptions
import com.vonage.android.meetingroom.internal.factory.createVonageScreenSharing
import com.vonage.android.meetingroom.internal.screen.audio.AudioDevicesState
import com.vonage.android.meetingroom.internal.service.MeetingRoomForegroundServiceHandler
import com.vonage.android.meetingroom.internal.util.ActivityContextHolder
import com.vonage.android.reactions.ReactionSignalPlugin
import com.vonage.android.reactions.di.ReactionsModule
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.audioselector.AudioDeviceSelector
import com.vonage.audioselector.VeraAudioDevice
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit

/**
 * Manual DI container for the meeting room session.
 *
 * All dependencies are created lazily and scoped to the lifetime of this container, which
 * is tied to the [MeetingRoomViewModel]. No Hilt or any other DI framework is used.
 */
internal class MeetingRoomContainer(
    private val applicationContext: Context,
    val prebuilt: MeetingRoomPrebuilt,
) {

    private val retrofit: Retrofit by lazy {
        MeetingRoomNetworkFactory.createRetrofit(
            baseUrl = prebuilt.baseUrl,
            isDebug = prebuilt.isDebug,
        )
    }

    private val apiService: MeetingRoomApiService by lazy {
        retrofit.create(MeetingRoomApiService::class.java)
    }

    val sessionRepository: MeetingRoomSessionRepository by lazy {
        MeetingRoomSessionRepository(apiService)
    }

    private val chatSignalPlugin: ChatSignalPlugin by lazy {
        ChatModule.provideChatSignalPlugin(applicationContext)
    }

    private val reactionSignalPlugin: ReactionSignalPlugin by lazy {
        ReactionsModule.provideReactionSignalPlugin()
    }

    private val veraAudioDevice: VeraAudioDevice by lazy {
        VeraAudioDevice(applicationContext)
    }

    private val sdkFactory: VonageSdkFactory by lazy {
        VonageSdkFactory.create(baseAudioDevice = veraAudioDevice)
    }

    private val publisherFactory: PublisherFactory by lazy {
        PublisherFactory(sdkFactory = sdkFactory)
    }

    val videoClient: VonageVideoClient by lazy {
        VonageVideoClient(
            context = applicationContext,
            sdkFactory = sdkFactory,
            publisherFactory = publisherFactory,
            signalPlugins = listOfNotNull(chatSignalPlugin, reactionSignalPlugin),
        )
    }

    val vonageArchiving: VonageArchiving by lazy {
        createVonageArchiving(retrofit)
    }

    val vonageCaptions: VonageCaptions by lazy {
        createVonageCaptions(retrofit)
    }

    val vonageScreenSharing: VonageScreenSharing by lazy {
        createVonageScreenSharing(applicationContext)
    }

    val callSettingsHolder: CallSettingsHolder by lazy {
        prebuilt.callSettingsHolder ?: CallSettingsHolder()
    }

    val foregroundServiceHandler: MeetingRoomForegroundServiceHandler by lazy {
        val handler = MeetingRoomForegroundServiceHandler(applicationContext)
        handler.createNotificationChannel()
        handler
    }

    val audioDeviceSelector: AudioDeviceSelector by lazy {
        AudioDeviceSelector(
            context = applicationContext,
            dispatcher = Dispatchers.Default,
        )
    }

    val audioDevicesState: AudioDevicesState by lazy {
        AudioDevicesState(
            availableDevices = audioDeviceSelector.availableDevices,
            activeDevice = audioDeviceSelector.activeDevice,
            selectDevice = { device -> audioDeviceSelector.selectDevice(device) },
        )
    }

    val activityContextHolder: ActivityContextHolder by lazy {
        ActivityContextHolder()
    }

    private val backgroundEffectsRepository: BackgroundEffectsRepository by lazy {
        DefaultBackgroundEffectsRepository(applicationContext)
    }

    internal val userBackgroundRepository: UserBackgroundRepository by lazy {
        DefaultUserBackgroundRepository(applicationContext)
    }

    val getBackgroundsUseCase: GetBackgroundsUseCase by lazy {
        GetBackgroundsUseCase(backgroundEffectsRepository, userBackgroundRepository)
    }
}
