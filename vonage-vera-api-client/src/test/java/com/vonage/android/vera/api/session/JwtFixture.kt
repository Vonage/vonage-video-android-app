package com.vonage.android.vera.api.session

import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

/** Builds a fake JWT (`header.payload.signature`) for tests, without real signing. Nested maps become JSON objects. */
internal fun makeJwt(payload: Map<String, Any>): String {
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val body = payload.toJsonObject()
    return listOf(header, Json.encodeToString(JsonObject.serializer(), body), "fake-signature-bytes")
        .joinToString(".") { base64Url.encode(it.encodeToByteArray()) }
}

private fun Map<*, *>.toJsonObject(): JsonObject =
    JsonObject(entries.associate { (key, value) -> key.toString() to value.toJsonElement() })

private fun Any?.toJsonElement(): JsonElement = when (this) {
    is Number -> JsonPrimitive(this)
    is Map<*, *> -> toJsonObject()
    else -> JsonPrimitive(toString())
}

/** Epoch seconds [offsetSeconds] away from now. */
internal fun epochSecondsFromNow(offsetSeconds: Long): Long = System.currentTimeMillis() / 1_000 + offsetSeconds
