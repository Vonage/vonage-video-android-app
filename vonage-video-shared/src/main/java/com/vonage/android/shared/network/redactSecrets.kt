package com.vonage.android.shared.network

private val SECRET_JSON_FIELDS = Regex(""""(sessionKey|token)"\s*:\s*"[^"]*"""")

/**
 * Masks the session key JWT and client token in a JSON log line, e.g. for an OkHttp
 * `HttpLoggingInterceptor` logging bodies in debug builds.
 */
fun String.redactSecrets(): String =
    SECRET_JSON_FIELDS.replace(this) { """"${it.groupValues[1]}":"[REDACTED]"""" }
