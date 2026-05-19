package com.vonage.android.kotlin.model

enum class NoiseSuppression {
    ENABLED,
    DISABLED;

    fun isEnabled(): Boolean = this == ENABLED
}
