package com.vonage.android.fx.data

import android.net.Uri
import com.vonage.android.fx.ui.VideoBackgroundItem
import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.ImmutableList

/**
 * Manages user-uploaded virtual background images.
 *
 * Implementations are flavor-specific:
 * - `enabled` flavor: [DefaultUserBackgroundRepository] — persists JPEGs in `filesDir/user_backgrounds/`.
 * - `disabled` flavor: no-op stub that always returns empty / null / false.
 */
interface UserBackgroundRepository {

    /**
     * Returns all user-uploaded backgrounds sorted chronologically (oldest first).
     * [captureResolution] is accepted for API symmetry but is not used — images were already
     * cropped to the resolution active at save time.
     */
    suspend fun getUserBackgrounds(captureResolution: CaptureResolution? = null): ImmutableList<VideoBackgroundItem>

    /**
     * Reads the image at [uri], center-crops it to the portrait frame for [captureResolution],
     * and persists it to storage.
     *
     * @return The saved [VideoBackgroundItem], or `null` if the cap is reached or the URI is
     *         unreadable.
     */
    suspend fun saveBackground(uri: Uri, captureResolution: CaptureResolution? = null): VideoBackgroundItem?

    /**
     * Deletes the user-uploaded background identified by [id].
     *
     * @return `true` if the file was found and successfully deleted.
     */
    suspend fun deleteBackground(id: String): Boolean

    companion object {
        /** Maximum number of user-uploaded backgrounds allowed. The "Add image" tile is hidden once reached. */
        const val MAX_USER_BACKGROUNDS = 10
    }
}
