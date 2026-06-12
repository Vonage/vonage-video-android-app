package com.vonage.android.fx.data

/**
 * Deletes a user-uploaded background image from persistent storage.
 *
 * Delegates entirely to [UserBackgroundRepository.deleteBackground]. Note: if the deleted
 * background is currently active as a video effect, the caller (ViewModel) is responsible for
 * resetting the effect to [com.vonage.android.kotlin.model.VideoEffect.None].
 *
 * @return `true` if the file was found and successfully deleted.
 */
class DeleteBackgroundUseCase(
    private val userBackgroundRepository: UserBackgroundRepository,
) {
    suspend operator fun invoke(id: String): Boolean =
        userBackgroundRepository.deleteBackground(id)
}
