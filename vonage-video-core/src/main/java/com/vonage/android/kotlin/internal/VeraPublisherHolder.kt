package com.vonage.android.kotlin.internal

import com.vonage.android.kotlin.sdk.VonagePublisher

/**
 * Container holding the camera publisher instance.
 *
 * The screen publisher is owned by `Call` (`participants[PUBLISHER_SCREEN_ID]`) and is not kept here,
 * so stopping a share drops the last reference to it and its `MediaProjection`.
 *
 * @property publisher The main camera publisher
 */
data class VeraPublisherHolder(
    val publisher: VonagePublisher,
)
