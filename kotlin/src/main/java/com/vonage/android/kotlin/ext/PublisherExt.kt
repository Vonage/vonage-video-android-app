package com.vonage.android.kotlin.ext

import com.opentok.android.Publisher
import com.vonage.android.kotlin.model.BackgroundBlur.KEY
import com.vonage.android.kotlin.model.BackgroundBlur.params
import com.vonage.android.kotlin.model.BlurLevel

internal fun Publisher.applyVideoBlur(blurLevel: BlurLevel) {
    when (blurLevel) {
        BlurLevel.NONE -> arrayListOf()
        BlurLevel.LOW,
        BlurLevel.HIGH -> {
            arrayListOf(VideoTransformer(KEY, params(blurLevel)))
        }
    }.let {
        setVideoTransformers(it)
    }
}
