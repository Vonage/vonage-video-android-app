package com.vonage.android.fx.data

import android.net.Uri
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution

/**
 * Saves a user-uploaded background image to persistent storage.
 *
 * Delegates entirely to [UserBackgroundRepository.saveBackground]. Returns `null` if
 * the [UserBackgroundRepository.MAX_USER_BACKGROUNDS] cap is reached or the URI is unreadable.
 */
class AddBackgroundUseCase(
    private val userBackgroundRepository: UserBackgroundRepository,
) {
    suspend operator fun invoke(
        uri: Uri,
        captureResolution: CaptureResolution? = null,
    ): VideoBackgroundItem? = userBackgroundRepository.saveBackground(uri, captureResolution)
}
