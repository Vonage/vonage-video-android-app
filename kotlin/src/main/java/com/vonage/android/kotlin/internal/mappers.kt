package com.vonage.android.kotlin.internal

import com.opentok.android.Publisher
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_SCREEN_ID
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.VideoSource
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.VeraScreenPublisher
import com.vonage.android.kotlin.model.VeraSubscriber

internal fun Subscriber.toParticipant(): VeraSubscriber = VeraSubscriber(
    id = stream.streamId,
    videoSource = toParticipantType(),
    name = stream.name,
    isMicEnabled = stream.hasAudio(),
    isCameraEnabled = stream.hasVideo(),
    view = view,
    isSpeaking = false,
)

internal fun Publisher.toParticipant(
    name: String? = null,
    camera: Int = 0,
    isSpeaking: Boolean = false,
): VeraPublisher = VeraPublisher(
    id = PUBLISHER_ID,
    videoSource = VideoSource.CAMERA,
    name = stream?.name ?: name.orEmpty(),
    isMicEnabled = publishAudio,
    isCameraEnabled = publishVideo,
    view = view,
    cameraIndex = camera,
    cycleCamera = { cycleCamera() },
    blurLevel = BlurLevel.NONE,
    setCameraBlur = { blurLevel -> applyVideoBlur(blurLevel) },
    isSpeaking = isSpeaking,
)

internal fun Publisher.toScreenParticipant(): VeraScreenPublisher = VeraScreenPublisher(
    id = PUBLISHER_SCREEN_ID,
    videoSource = VideoSource.SCREEN,
    name = name,
    isMicEnabled = false,
    isCameraEnabled = true,
    view = view,
    isSpeaking = false,
)

internal fun Subscriber.toParticipantType(): VideoSource =
    when (stream.streamVideoType) {
        Stream.StreamVideoType.StreamVideoTypeCamera -> VideoSource.CAMERA
        Stream.StreamVideoType.StreamVideoTypeScreen,
        Stream.StreamVideoType.StreamVideoTypeCustom -> VideoSource.SCREEN
    }
