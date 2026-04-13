package com.vonage.android.kotlin.internal

import com.vonage.android.kotlin.VonagePublisher

/**
 * Container holding publisher instances for camera and screen sharing.
 *
 * @property publisher The main camera publisher
 * @property screenPublisher Optional screen sharing publisher
 */
data class VeraPublisherHolder(
    val publisher: VonagePublisher,
    var screenPublisher: VonagePublisher? = null,
)
