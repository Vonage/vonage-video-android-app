package com.vonage.android.vera.api

/**
 * The backend answered with a non-2xx status ([httpCode]) or without the expected body.
 *
 * [errorBody] is the raw error payload, for diagnostics only; it is not part of [message].
 */
class VeraApiException(
    message: String,
    val httpCode: Int,
    val errorBody: String? = null,
) : Exception(message)
