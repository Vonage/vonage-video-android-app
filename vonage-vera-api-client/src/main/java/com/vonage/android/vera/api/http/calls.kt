package com.vonage.android.vera.api.http

import com.vonage.android.vera.api.VeraApiException
import retrofit2.Response

internal const val EMPTY_RESPONSE = "Empty response"

/**
 * Runs [call] and returns its body. A non-2xx status fails with [failureMessage], a missing body
 * with [emptyMessage]; transport errors are returned as they are thrown.
 */
internal suspend fun <T : Any> execute(
    failureMessage: String,
    emptyMessage: String = EMPTY_RESPONSE,
    call: suspend () -> Response<T>,
): Result<T> =
    runCatching {
        val response = call()
        if (!response.isSuccessful) {
            throw VeraApiException(failureMessage, response.code(), response.errorBody()?.string())
        }
        response.body() ?: throw VeraApiException(emptyMessage, response.code())
    }

/** Like [execute], for endpoints whose body the caller does not need: any 2xx is a success. */
internal suspend fun executeIgnoringBody(
    failureMessage: String,
    call: suspend () -> Response<*>,
): Result<Unit> =
    runCatching {
        val response = call()
        if (!response.isSuccessful) {
            throw VeraApiException(failureMessage, response.code(), response.errorBody()?.string())
        }
    }
