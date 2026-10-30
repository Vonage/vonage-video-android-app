package com.vonage.android.kotlin.model

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.sdk.VonageNetworkConditionListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Network condition of a publisher or subscriber link.
 *
 * @property score From 0 (unknown) to 5 (excellent).
 * @property reason One of `none`, `bandwidth`, `packet_loss` or `unknown`.
 */
@Stable
data class NetworkCondition(
    val score: Int,
    val reason: String,
)

internal fun observeNetworkCondition(
    setListener: (VonageNetworkConditionListener?) -> Unit,
): Flow<NetworkCondition> = callbackFlow {
    setListener { condition -> trySend(NetworkCondition(score = condition.score, reason = condition.reason)) }
    awaitClose { setListener(null) }
}
