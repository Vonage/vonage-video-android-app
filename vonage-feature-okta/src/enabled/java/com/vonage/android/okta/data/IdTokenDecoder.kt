package com.vonage.android.okta.data

import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * Minimal, offline decoder for OIDC ID token claims.
 *
 * The token signature is NOT verified here — the token comes straight from the
 * Okta SDK over TLS and is only used to display the user's name in the UI.
 */
internal object IdTokenDecoder {

    private const val PAYLOAD_INDEX = 1

    private val json = Json { ignoreUnknownKeys = true }
    private val base64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    /** Returns the `name` claim of the given ID token, or null when unavailable. */
    fun name(idToken: String?): String? = claim(idToken, "name")

    private fun claim(idToken: String?, claim: String): String? = runCatching {
        val payload = idToken?.split(".")?.getOrNull(PAYLOAD_INDEX) ?: return null
        val claims = json.parseToJsonElement(base64.decode(payload).decodeToString()).jsonObject
        (claims[claim] as? JsonPrimitive)?.takeIf { it.isString }?.content
    }.getOrNull()
}
