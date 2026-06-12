package com.vonage.android.fx.data

import android.content.Context
import android.net.Uri
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * No-op stub for the disabled video effects flavor.
 */
@Suppress("UnusedParameter")
class DefaultUserBackgroundRepository(@Suppress("UNUSED_PARAMETER") context: Context) : UserBackgroundRepository {

    override suspend fun getUserBackgrounds(
        captureResolution: CaptureResolution?,
    ): ImmutableList<VideoBackgroundItem> = persistentListOf()

    @Suppress("FunctionOnlyReturningConstant")
    override suspend fun saveBackground(
        uri: Uri,
        captureResolution: CaptureResolution?,
    ): VideoBackgroundItem? = null

    @Suppress("FunctionOnlyReturningConstant")
    override suspend fun deleteBackground(id: String): Boolean = false
}
