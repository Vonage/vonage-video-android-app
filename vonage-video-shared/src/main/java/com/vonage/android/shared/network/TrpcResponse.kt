package com.vonage.android.shared.network

import kotlinx.serialization.Serializable

/**
 * Envelope used by every v2 (tRPC-over-HTTP) backend response: `{ "result": { "data": <payload> } }`.
 *
 * Error responses use a non-2xx status code with an `{ "error": { ... } }` body,
 * so they never reach this type.
 */
@Serializable
data class TrpcResponse<T>(
    val result: TrpcResult<T>,
)

@Serializable
data class TrpcResult<T>(
    val data: T,
)

/**
 * Request body shared by the v2 endpoints that only need the session key JWT.
 */
@Serializable
data class SessionKeyRequest(
    val sessionKey: String,
)
