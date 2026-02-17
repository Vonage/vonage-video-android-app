package com.vonage.android.kotlin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal object BackgroundBlur {

    internal fun params(blurLevel: BlurLevel) =
        Json.encodeToString(Radius(blurLevel))

    internal const val KEY = "BackgroundBlur"
}

@Serializable
internal data class Radius(
    val radius: BlurLevel,
)

/**
 * Enumeration of background blur levels for video.
 */
@Serializable
enum class BlurLevel {
    /** Low blur effect */
    @SerialName("Low")
    LOW,

    /** High blur effect */
    @SerialName("High")
    HIGH,

    /** No blur effect */
    @SerialName("None")
    NONE;

    companion object {
        private val map = BlurLevel.entries.toTypedArray()
        
        /**
         * Gets blur level by cycling through values using modulo.
         *
         * @param index The index to convert to a blur level
         * @return The blur level at the wrapped index
         */
        infix fun by(index: Int): BlurLevel = map[index % BlurLevel.entries.size]
    }
}
