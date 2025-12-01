package com.vonage.android.kotlin.ext

import com.opentok.android.Stream
import com.vonage.android.kotlin.model.VideoSource

internal fun Stream.toParticipantType(): VideoSource =
    when (streamVideoType) {
        Stream.StreamVideoType.StreamVideoTypeCamera -> VideoSource.CAMERA
        Stream.StreamVideoType.StreamVideoTypeScreen,
        Stream.StreamVideoType.StreamVideoTypeCustom -> VideoSource.SCREEN
    }
