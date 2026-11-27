package com.vonage.android.kotlin.internal

import com.vonage.android.kotlin.sdk.VonagePublisher

/**
 * Container holding the camera publisher instance.
 *
 * @property publisher The main camera publisher
 */
data class VeraPublisherHolder(
    val publisher: VonagePublisher,
)
