package com.vonage.android.vera.api.session

import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject

/** Reads claims from the session key JWT payload. The signature is not verified. */
internal object SessionKeyParser {

    private const val JWT_PARTS = 3
    private const val MILLIS_PER_SECOND = 1_000.0
    private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    /**
     * The key's expiration in epoch seconds: the `exp` claim (seconds) or, for scheduled meeting
     * keys without `exp`, `schedule.endsAtMs` (milliseconds). `null` when the key is not a JWT or
     * has neither claim.
     */
    fun extractExpiration(sessionKey: String): Double? {
        val payload = decodePayload(sessionKey) ?: return null
        return payload["exp"]?.numberOrNull()
            ?: (payload["schedule"] as? JsonObject)?.get("endsAtMs")?.numberOrNull()?.div(MILLIS_PER_SECOND)
    }

    /**
     * `true` if the key's expiration ([extractExpiration]) is at or before [nowMillis]. Per
     * RFC 7519 §4.1.4 the token must not be accepted on or after `exp`. A key without an
     * expiration is never expired.
     */
    fun isExpired(sessionKey: String, nowMillis: Long = System.currentTimeMillis()): Boolean {
        val expiration = extractExpiration(sessionKey) ?: return false
        return expiration * MILLIS_PER_SECOND <= nowMillis
    }

    private fun JsonElement.numberOrNull(): Double? = (this as? JsonPrimitive)?.doubleOrNull

    private fun decodePayload(jwt: String): JsonObject? {
        val parts = jwt.split('.')
        if (parts.size != JWT_PARTS) return null
        return runCatching {
            Json.parseToJsonElement(base64Url.decode(parts[1]).decodeToString()).jsonObject
        }.getOrNull()
    }
}
