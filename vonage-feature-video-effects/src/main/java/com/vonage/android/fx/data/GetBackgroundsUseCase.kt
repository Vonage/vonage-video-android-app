package com.vonage.android.fx.data

import com.vonage.android.kotlin.model.CaptureResolution
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Loads all available backgrounds by merging built-in and user-uploaded sources.
 *
 * Each source is fetched independently: a failure in one does not prevent the other from
 * being included in the result. Built-in backgrounds appear before user-uploaded ones.
 *
 * The [canAddBackground][BackgroundsResult.canAddBackground] flag is derived here so that
 * ViewModels have no knowledge of [UserBackgroundRepository.MAX_USER_BACKGROUNDS].
 */
class GetBackgroundsUseCase(
    private val backgroundEffectsRepository: BackgroundEffectsRepository,
    private val userBackgroundRepository: UserBackgroundRepository,
) {
    suspend operator fun invoke(captureResolution: CaptureResolution? = null): BackgroundsResult {
        val builtIn = runCatching {
            backgroundEffectsRepository.getBackgrounds(captureResolution)
        }.getOrElse { persistentListOf() }

        val user = runCatching {
            userBackgroundRepository.getUserBackgrounds(captureResolution)
        }.getOrElse { persistentListOf() }

        return BackgroundsResult(
            backgrounds = (builtIn + user).toImmutableList(),
            canAddBackground = user.size < UserBackgroundRepository.MAX_USER_BACKGROUNDS,
        )
    }
}
