package com.vonage.android.kotlin.sdk

/**
 * Network condition reported by the SDK media link stats of a publisher or subscriber.
 *
 * @property score From 0 (unknown) to 5 (excellent).
 * @property reason One of `none`, `bandwidth`, `packet_loss` or `unknown`.
 */
data class VonageNetworkCondition(
    val score: Int,
    val reason: String,
)

fun interface VonageNetworkConditionListener {
    fun onNetworkCondition(condition: VonageNetworkCondition)
}
