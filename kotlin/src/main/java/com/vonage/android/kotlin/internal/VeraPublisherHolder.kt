package com.vonage.android.kotlin.internal

import com.opentok.android.Publisher

data class VeraPublisherHolder(
    val publisher: Publisher,
    var screenPublisher: Publisher? = null,
)
