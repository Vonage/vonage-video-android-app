package com.vonage.android.kotlin.internal

import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.sdk.VonageVideoSize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Adaptive video quality policy. See `docs/VIDEO_QUALITY_POLICY.md` for the rationale
 * behind every threshold in this file.
 */

// region Subscriber side

/**
 * Receive-side quality tier for a remote stream. Each tier maps to one of the simulcast
 * layers a 720p camera publisher sends (320x180, 640x360, 1280x720).
 *
 * @property layerShortEdge short edge (px) of the preferred layer, or `null` for no preference
 * @property layerLongEdge long edge (px) of the preferred layer, or `null` for no preference
 * @property frameRate preferred frame rate, or `null` for no preference
 */
internal enum class SubscriberVideoTier(
    val layerShortEdge: Int?,
    val layerLongEdge: Int?,
    val frameRate: Float?,
) {
    THUMBNAIL(layerShortEdge = 180, layerLongEdge = 320, frameRate = 15f),
    MEDIUM(layerShortEdge = 360, layerLongEdge = 640, frameRate = 30f),
    FULL(layerShortEdge = null, layerLongEdge = null, frameRate = null),
    ;

    /** Preferred resolution oriented like the source video, or `null` for no preference. */
    fun preferredResolution(sourceIsPortrait: Boolean): VonageVideoSize? {
        val short = layerShortEdge ?: return null
        val long = layerLongEdge ?: return null
        return if (sourceIsPortrait) VonageVideoSize(short, long) else VonageVideoSize(long, short)
    }
}

internal object SubscriberVideoQualityPolicy {

    /** Rendered short edge (px) at or below which the thumbnail layer is enough. */
    const val THUMBNAIL_MAX_SHORT_EDGE_PX = 240

    /** Rendered short edge (px) at or below which the medium layer is enough. */
    const val MEDIUM_MAX_SHORT_EDGE_PX = 540

    /**
     * Downgrades only happen once the rendered size is this fraction below a threshold.
     * Upgrades happen as soon as a threshold is crossed, so quality is never held back.
     */
    const val DOWNGRADE_MARGIN = 0.15f

    /** Rendered size changes are applied once they have been stable for this long. */
    const val SETTLE_MILLIS = 300L

    private const val DEFAULT_SOURCE_WIDTH = 16
    private const val DEFAULT_SOURCE_HEIGHT = 9

    /**
     * Returns the tier for a stream rendered in a [viewWidth]x[viewHeight] px view.
     *
     * The renderer letterboxes (`STYLE_VIDEO_FIT`), so the video only occupies the
     * [sourceWidth]x[sourceHeight]-shaped rectangle that fits inside the view. Its short edge
     * is what decides the tier. Unknown source sizes are treated as 16:9 landscape.
     *
     * @param current tier currently applied, used for the downgrade margin
     */
    fun tierFor(
        viewWidth: Int,
        viewHeight: Int,
        sourceWidth: Int,
        sourceHeight: Int,
        isScreenShare: Boolean,
        current: SubscriberVideoTier,
    ): SubscriberVideoTier {
        if (isScreenShare) return SubscriberVideoTier.FULL // shared screens carry text
        if (viewWidth <= 0 || viewHeight <= 0) return current

        val shortEdge = fittedShortEdge(viewWidth, viewHeight, sourceWidth, sourceHeight)
        val target = rawTier(shortEdge)
        val currentLowerBound = when (current) {
            SubscriberVideoTier.FULL -> MEDIUM_MAX_SHORT_EDGE_PX
            SubscriberVideoTier.MEDIUM -> THUMBNAIL_MAX_SHORT_EDGE_PX
            SubscriberVideoTier.THUMBNAIL -> 0
        }
        val isClearlySmaller = shortEdge < currentLowerBound * (1 - DOWNGRADE_MARGIN)
        return if (target.ordinal >= current.ordinal || isClearlySmaller) target else current
    }

    private fun rawTier(shortEdge: Int): SubscriberVideoTier = when {
        shortEdge <= THUMBNAIL_MAX_SHORT_EDGE_PX -> SubscriberVideoTier.THUMBNAIL
        shortEdge <= MEDIUM_MAX_SHORT_EDGE_PX -> SubscriberVideoTier.MEDIUM
        else -> SubscriberVideoTier.FULL
    }

    internal fun fittedShortEdge(
        viewWidth: Int,
        viewHeight: Int,
        sourceWidth: Int,
        sourceHeight: Int,
    ): Int {
        val (sw, sh) = if (sourceWidth > 0 && sourceHeight > 0) {
            sourceWidth to sourceHeight
        } else {
            DEFAULT_SOURCE_WIDTH to DEFAULT_SOURCE_HEIGHT
        }
        val scale = min(viewWidth.toFloat() / sw, viewHeight.toFloat() / sh)
        return (min(sw, sh) * scale).roundToInt()
    }
}

// endregion

// region Publisher side

/**
 * Send-side policy. A publisher cannot know how large each remote client renders it, so the
 * room size is used as a proxy: the more people in the call, the smaller every tile is.
 * The local user's explicit bitrate choice always wins, and so does speaking, because
 * other clients may put the active speaker in the spotlight.
 */
internal object PublisherVideoQualityPolicy {

    /** Up to this many remote camera streams, the user's configuration is used as-is. */
    const val SMALL_ROOM_MAX_REMOTES = 2

    /** Up to this many remote camera streams, [VideoBitratePreset.BW_SAVER] is applied. */
    const val MEDIUM_ROOM_MAX_REMOTES = 6

    /** How long the full bitrate is kept after the local user stops speaking. */
    const val SPEAKING_HOLD_MILLIS = 5_000L

    fun effectiveConfig(
        userConfig: VideoBitrateConfig,
        remoteCameraStreams: Int,
        isSpeaking: Boolean,
    ): VideoBitrateConfig {
        if (userConfig.preset != VideoBitratePreset.DEFAULT || isSpeaking) return userConfig
        val preset = when {
            remoteCameraStreams <= SMALL_ROOM_MAX_REMOTES -> return userConfig
            remoteCameraStreams <= MEDIUM_ROOM_MAX_REMOTES -> VideoBitratePreset.BW_SAVER
            else -> VideoBitratePreset.EXTRA_BW_SAVER
        }
        return VideoBitrateConfig(preset = preset, maxBitrate = null)
    }
}

/**
 * Emits `true` as soon as the upstream does, and `false` only after the upstream has stayed
 * `false` for [millis]. Starts with `false`. Keeps short pauses in speech from flipping the
 * publisher bitrate back and forth.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun Flow<Boolean>.holdTrueFor(millis: Long): Flow<Boolean> =
    transformLatest { value ->
        if (!value) delay(millis)
        emit(value)
    }
        .onStart { emit(false) }
        .distinctUntilChanged()

// endregion
