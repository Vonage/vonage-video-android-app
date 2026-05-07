package com.vonage.android.fx.data

import android.content.Context
import com.vonage.android.fx.ui.VideoBackgroundItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * No-op stub for the disabled video effects flavor.
 */
class BackgroundEffectsRepository(@Suppress("UNUSED_PARAMETER") context: Context) {

    fun getBackgrounds(): ImmutableList<VideoBackgroundItem> = persistentListOf()
}
