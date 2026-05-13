package com.vonage.android.fx.ui

import androidx.annotation.DrawableRes

/**
 * A virtual background option displayed in the background selection grid.
 *
 * @property id Unique identifier for the background.
 * @property thumbnailRes Optional drawable resource for the thumbnail preview. Null for
 *   user-uploaded images, which load their preview from [imagePath] at runtime.
 * @property imagePath Absolute file path to the image for the SDK background replacement.
 * @property isUserUploaded Whether this background was uploaded by the user. User-uploaded
 *   tiles show a delete affordance in the effects sheet.
 */
data class VideoBackgroundItem(
    val id: String,
    @DrawableRes val thumbnailRes: Int? = null,
    val imagePath: String? = null,
    val isUserUploaded: Boolean = false,
)
