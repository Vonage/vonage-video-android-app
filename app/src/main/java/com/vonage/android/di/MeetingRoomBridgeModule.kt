package com.vonage.android.di

import com.vonage.android.config.AppConfig
import com.vonage.android.data.SessionRepository
import com.vonage.android.meetingroom.ActivityContextProvider
import com.vonage.android.meetingroom.ConfigProvider
import com.vonage.android.meetingroom.ForegroundAction
import com.vonage.android.meetingroom.ForegroundServiceHandler
import com.vonage.android.meetingroom.MeetingRoomConfig
import com.vonage.android.meetingroom.MeetingSessionInfo
import com.vonage.android.meetingroom.SessionProvider
import com.vonage.android.notifications.VeraNotificationChannelRegistry.CallAction
import com.vonage.android.service.VeraForegroundServiceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MeetingRoomBridgeModule {

    @Provides
    fun provideSessionProvider(
        sessionRepository: SessionRepository,
    ): SessionProvider = SessionProvider { roomName ->
        sessionRepository.getSession(roomName).map { sessionInfo ->
            MeetingSessionInfo(
                apiKey = sessionInfo.apiKey,
                sessionId = sessionInfo.sessionId,
                token = sessionInfo.token,
                captionsId = sessionInfo.captionsId,
            )
        }
    }

    @Provides
    fun provideConfigProvider(): ConfigProvider = ConfigProvider {
        MeetingRoomConfig(
            allowCameraControl = AppConfig.VideoSettings.ALLOW_CAMERA_CONTROL,
            allowMicrophoneControl = AppConfig.AudioSettings.ALLOW_MICROPHONE_CONTROL,
            allowShowParticipantList = AppConfig.MeetingRoomSettings.SHOW_PARTICIPANT_LIST,
        )
    }

    @Provides
    @Singleton
    fun provideForegroundServiceHandler(
        handler: VeraForegroundServiceHandler,
    ): ForegroundServiceHandler = object : ForegroundServiceHandler {
        override fun startForegroundService(roomName: String) {
            handler.startForegroundService(roomName)
        }

        override fun stopForegroundService() {
            handler.stopForegroundService()
        }

        override val actions: Flow<ForegroundAction>
            get() = handler.actions.mapNotNull { callAction ->
                when (callAction) {
                    CallAction.HangUp -> ForegroundAction.HangUp
                    else -> null
                }
            }
    }

    @Provides
    @Singleton
    fun provideActivityContextProvider(
        provider: com.vonage.android.util.ActivityContextProvider,
    ): ActivityContextProvider = object : ActivityContextProvider {
        override fun requireActivityContext() = provider.requireActivityContext()
        override fun setActivityContext(context: android.content.Context) = provider.setActivityContext(context)
        override fun clearActivityContext() = provider.clearActivityContext()
    }
}

