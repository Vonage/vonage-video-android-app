package com.vonage.android.kotlin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object BackgroundBlur {

    fun params(blurLevel: BlurLevel) =
        Json.encodeToString(Radius(blurLevel))

    const val KEY = "BackgroundBlur"
}

@Serializable
data class Radius(
    val radius: BlurLevel,
)

@Serializable
enum class BlurLevel {
    @SerialName("Low")
    LOW,

    @SerialName("High")
    HIGH,

    @SerialName("None")
    NONE
}
