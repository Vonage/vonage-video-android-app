package com.vonage.android.fx.data

import android.content.Context
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * No-op stub for the disabled video effects flavor.
 */
class DefaultBackgroundEffectsRepository(@Suppress("UNUSED_PARAMETER") context: Context) : BackgroundEffectsRepository {

    override suspend fun getBackgrounds(
        captureResolution: CaptureResolution?,
    ): ImmutableList<VideoBackgroundItem> = persistentListOf()
}
