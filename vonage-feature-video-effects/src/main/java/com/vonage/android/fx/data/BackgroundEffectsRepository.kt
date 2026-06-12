package com.vonage.android.fx.data

import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.ImmutableList

/**
 * Provides the list of pre-rendered background images available for virtual background replacement.
 *
 * Implementations are flavor-specific:
 * - `enabled` flavor: [DefaultBackgroundEffectsRepository] — decodes drawables and caches JPEGs in `cacheDir`.
 * - `disabled` flavor: no-op stub that always returns an empty list.
 */
interface BackgroundEffectsRepository {
    /**
     * Returns all available built-in background images, cropped to the portrait aspect ratio
     * for [captureResolution]. Pass `null` to use the default resolution (HIGH, 720×1280).
     */
    suspend fun getBackgrounds(captureResolution: CaptureResolution? = null): ImmutableList<VideoBackgroundItem>
}
