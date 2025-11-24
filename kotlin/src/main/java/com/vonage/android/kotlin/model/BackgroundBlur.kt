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

@Serializable
enum class BlurLevel {
    @SerialName("Low")
    LOW,

    @SerialName("High")
    HIGH,

    @SerialName("None")
    NONE;

    companion object {
        private val map = BlurLevel.entries.toTypedArray()
        infix fun by(index: Int): BlurLevel = map[index % BlurLevel.entries.size]
    }
}
