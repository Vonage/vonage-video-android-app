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
class UserBackgroundRepository(@Suppress("UNUSED_PARAMETER") context: Context) {

    fun getUserBackgrounds(captureResolution: CaptureResolution? = null): ImmutableList<VideoBackgroundItem> =
        persistentListOf()

    @Suppress("FunctionOnlyReturningConstant")
    fun saveBackground(uri: Uri, captureResolution: CaptureResolution? = null): VideoBackgroundItem? = null

    @Suppress("FunctionOnlyReturningConstant")
    fun deleteBackground(id: String): Boolean = false

    companion object {
        const val MAX_USER_BACKGROUNDS = 10
    }
}
