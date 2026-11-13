package com.vonage.android.vera.api.http

import kotlinx.serialization.Serializable

/**
 * Envelope used by every v2 (tRPC-over-HTTP) backend response: `{ "result": { "data": <payload> } }`.
 *
 * Error responses use a non-2xx status code with an `{ "error": { ... } }` body,
 * so they never reach this type.
 */
@Serializable
internal data class TrpcResponse<T>(
    val result: TrpcResult<T>,
)

@Serializable
internal data class TrpcResult<T>(
    val data: T,
)

/**
 * Request body shared by the v2 endpoints that only need the session key JWT.
 */
@Serializable
internal data class SessionKeyRequest(
    val sessionKey: String,
)
