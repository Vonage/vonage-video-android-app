package com.vonage.android.fx.ui

import androidx.annotation.DrawableRes

/**
 * A virtual background option displayed in the background selection grid.
 *
 * @property id Unique identifier for the background.
 * @property thumbnailRes Optional drawable resource for the thumbnail preview.
 * @property imagePath Absolute file path to the image for the SDK background replacement.
 */
data class VideoBackgroundItem(
    val id: String,
    @DrawableRes val thumbnailRes: Int? = null,
    val imagePath: String? = null,
)
