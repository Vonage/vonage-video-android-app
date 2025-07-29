package com.vonage.android.kotlin.internal

import com.opentok.android.Publisher
import com.opentok.android.Subscriber
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.ext.applyVideoBlur
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.VeraSubscriber

internal fun Subscriber.toParticipant(): VeraSubscriber = VeraSubscriber(
    id = stream.streamId,
    name = stream.name,
    isMicEnabled = stream.hasAudio(),
    isCameraEnabled = stream.hasVideo(),
    view = view,
)

internal fun Publisher.toParticipant(
    name: String? = null,
    camera: Int = 0,
): VeraPublisher = VeraPublisher(
    id = PUBLISHER_ID,
    name = stream?.name ?: name.orEmpty(),
    isMicEnabled = publishAudio,
    isCameraEnabled = publishVideo,
    view = view,
    cameraIndex = camera,
    cycleCamera = { cycleCamera() },
    blurLevel = BlurLevel.NONE,
    setCameraBlur = { blurLevel -> applyVideoBlur(blurLevel) },
)
