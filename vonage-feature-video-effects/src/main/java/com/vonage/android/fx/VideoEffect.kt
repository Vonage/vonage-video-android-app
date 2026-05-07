package com.vonage.android.fx

import androidx.compose.runtime.Stable

/**
 * Represents the currently applied video effect on the publisher's camera feed.
 */
@Stable
sealed interface VideoEffect {
    data object None : VideoEffect
    data object BlurLow : VideoEffect
    data object BlurHigh : VideoEffect
    data class BackgroundImage(val id: String, val imagePath: String) : VideoEffect
}
