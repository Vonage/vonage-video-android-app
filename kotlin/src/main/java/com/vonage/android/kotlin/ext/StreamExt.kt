package com.vonage.android.kotlin.ext

import com.opentok.android.Stream
import com.vonage.android.kotlin.model.VideoSource

/**
 * Converts an OpenTok Stream video type to our VideoSource enum.
 *
 * Maps camera streams to VideoSource.CAMERA and screen/custom streams to VideoSource.SCREEN.
 *
 * @return VideoSource indicating the type of video stream
 */
internal fun Stream.toParticipantType(): VideoSource =
    when (streamVideoType) {
        Stream.StreamVideoType.StreamVideoTypeCamera -> VideoSource.CAMERA
        Stream.StreamVideoType.StreamVideoTypeScreen,
        Stream.StreamVideoType.StreamVideoTypeCustom -> VideoSource.SCREEN
    }
