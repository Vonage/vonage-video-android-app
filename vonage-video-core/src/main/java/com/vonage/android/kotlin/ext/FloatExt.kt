package com.vonage.android.kotlin.ext

import kotlin.math.round

/** Scale factor for rounding to 2 decimal places */
private const val SCALE = 100

/**
 * Rounds a float to 2 decimal places.
 *
 * Useful for normalizing audio levels and reducing precision noise in audio level
 * comparisons and UI display.
 *
 * @return Float rounded to 2 decimal places
 */
internal fun Float.round2() = round(this * SCALE) / SCALE
