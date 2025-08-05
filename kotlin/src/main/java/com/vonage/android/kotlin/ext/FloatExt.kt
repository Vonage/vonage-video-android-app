package com.vonage.android.kotlin.ext

import kotlin.math.round

private const val SCALE = 10000
internal fun Float.round4() = round(this * SCALE) / SCALE
