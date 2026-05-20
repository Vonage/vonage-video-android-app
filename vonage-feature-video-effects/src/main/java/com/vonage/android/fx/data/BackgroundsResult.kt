package com.vonage.android.fx.data

import com.vonage.android.fx.ui.VideoBackgroundItem
import kotlinx.collections.immutable.ImmutableList

/**
 * The result of a backgrounds load, combining both built-in and user-uploaded entries.
 *
 * @property backgrounds Merged list of all available backgrounds (built-in first, then user-uploaded).
 * @property canAddBackground `true` while the number of user uploads is below [UserBackgroundRepository.MAX_USER_BACKGROUNDS].
 */
data class BackgroundsResult(
    val backgrounds: ImmutableList<VideoBackgroundItem>,
    val canAddBackground: Boolean,
)
