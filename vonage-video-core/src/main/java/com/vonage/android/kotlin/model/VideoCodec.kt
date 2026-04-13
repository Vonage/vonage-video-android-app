package com.vonage.android.kotlin.model

import com.vonage.android.kotlin.VonageVideoCodec

/**
 * App-level representation of the preferred video codec.
 *
 * This is a **build-time** setting — changes take effect on the next publisher creation.
 *
 * @property label Human-readable label shown in the UI
 */
enum class VideoCodec(val label: String) {
    VP8("VP8"),
    H264("H.264"),
    VP9("VP9"),
}

/**
 * The default codec order used when the user has not configured a manual preference.
 */
val DEFAULT_VIDEO_CODEC_ORDER: List<VideoCodec> = listOf(VideoCodec.VP8, VideoCodec.H264, VideoCodec.VP9)

fun VideoCodec.toVonageVideoCodec(): VonageVideoCodec = when (this) {
    VideoCodec.VP8 -> VonageVideoCodec.VP8
    VideoCodec.H264 -> VonageVideoCodec.H264
    VideoCodec.VP9 -> VonageVideoCodec.VP9
}
