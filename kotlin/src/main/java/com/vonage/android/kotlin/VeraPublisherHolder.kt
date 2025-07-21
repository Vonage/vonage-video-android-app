package com.vonage.android.kotlin

import com.opentok.android.Publisher
import com.vonage.android.kotlin.model.VeraPublisher

internal data class VeraPublisherHolder(
    val participant: VeraPublisher,
    val publisher: Publisher,
)
