package com.vonage.android.vera.api.session

import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Reads claims from the session key JWT payload. The signature is not verified. */
internal object SessionKeyParser {

    private const val JWT_PARTS = 3
    private const val MILLIS_PER_SECOND = 1_000.0
    private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    /** The `exp` claim in epoch seconds, or `null` when the key is not a JWT or has no `exp`. */
    fun extractExpiration(sessionKey: String): Double? =
        decodePayload(sessionKey)?.get("exp")?.let { runCatching { it.jsonPrimitive.doubleOrNull }.getOrNull() }

    /**
     * `true` if the key has an `exp` claim at or before [nowMillis]. Per RFC 7519 §4.1.4 the token
     * must not be accepted on or after `exp`. A key without `exp` is never expired.
     */
    fun isExpired(sessionKey: String, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val expiration = extractExpiration(sessionKey) ?: return false
        return expiration * MILLIS_PER_SECOND <= nowMillis
    }

    private fun decodePayload(jwt: String): JsonObject? {
        val parts = jwt.split('.')
        if (parts.size != JWT_PARTS) return null
        return runCatching {
            Json.parseToJsonElement(base64Url.decode(parts[1]).decodeToString()).jsonObject
        }.getOrNull()
    }
}
