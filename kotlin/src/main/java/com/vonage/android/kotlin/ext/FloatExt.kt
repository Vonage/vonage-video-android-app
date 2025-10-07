package com.vonage.android.kotlin.ext

import kotlin.math.round

private const val SCALE = 100
internal fun Float.round2() = round(this * SCALE) / SCALE
