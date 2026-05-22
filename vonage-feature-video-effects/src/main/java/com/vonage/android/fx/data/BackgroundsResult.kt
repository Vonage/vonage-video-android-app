package com.vonage.android.fx.data

import com.vonage.android.fx.ui.VideoBackgroundItem
import kotlinx.collections.immutable.ImmutableList

/**
 * The result of a backgrounds load, combining both built-in and user-uploaded entries.
 *
 * @property backgrounds Merged list of all available backgrounds (built-in first, then user-uploaded).
 * @property remainingBackgroundSlots Number of additional user backgrounds the user may still upload
 *   before reaching [UserBackgroundRepository.MAX_USER_BACKGROUNDS]. Zero means the cap is reached
 *   and the "Add image" tile should be hidden.
 */
data class BackgroundsResult(
    val backgrounds: ImmutableList<VideoBackgroundItem>,
    val remainingBackgroundSlots: Int,
)
