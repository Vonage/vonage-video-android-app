package com.vonage.android.data.network.interceptor

import com.vonage.android.util.E2eTestFlags
import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

/**
 * E2E-only: replaces the `sessionKey` of a successful `v2/createSession` response with a JWT
 * whose `exp` claim is an hour in the past, so the expired-key guard can be tested against any
 * backend. Inactive unless [E2eTestFlags.expiredSessionKey] is set by the launch intent.
 */
class E2eExpiredSessionKeyInterceptor internal constructor(
    private val e2eTestFlags: E2eTestFlags,
    private val currentTimeMillis: () -> Long,
) : Interceptor {

    @Inject
    constructor(e2eTestFlags: E2eTestFlags) : this(e2eTestFlags, System::currentTimeMillis)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (!e2eTestFlags.expiredSessionKey ||
            !request.url.encodedPath.endsWith(CREATE_SESSION_PATH) ||
            !response.isSuccessful
        ) {
            return response
        }
        val body = response.body
        val original = body.string()
        val rewritten = runCatching { withExpiredSessionKey(original) }.getOrDefault(original)
        return response.newBuilder()
            .body(rewritten.toResponseBody(body.contentType()))
            .build()
    }

    /** `{"result":{"data":{...}}}` with `data.sessionKey` replaced. */
    private fun withExpiredSessionKey(json: String): String {
        val root = Json.parseToJsonElement(json).jsonObject
        val result = root.getValue("result").jsonObject
        val data = result.getValue("data").jsonObject
        val expiredKey = expiredJwt(
            roomName = data["roomName"]?.jsonPrimitive?.content.orEmpty(),
            sessionId = data["sessionId"]?.jsonPrimitive?.content.orEmpty(),
        )
        val newData = JsonObject(data + ("sessionKey" to JsonPrimitive(expiredKey)))
        val newResult = JsonObject(result + ("data" to newData))
        return JsonObject(root + ("result" to newResult)).toString()
    }

    private fun expiredJwt(roomName: String, sessionId: String): String {
        val payload = buildJsonObject {
            put("exp", currentTimeMillis() / MILLIS_PER_SECOND - EXPIRED_AGO_SECONDS)
            put("roomName", roomName)
            put("sessionId", sessionId)
        }
        return listOf(JWT_HEADER, payload.toString(), FAKE_SIGNATURE)
            .joinToString(".") { base64Url.encode(it.encodeToByteArray()) }
    }

    private companion object {
        const val CREATE_SESSION_PATH = "/v2/createSession"
        const val JWT_HEADER = """{"alg":"HS256","typ":"JWT"}"""
        const val FAKE_SIGNATURE = "e2esig"
        const val MILLIS_PER_SECOND = 1_000L
        const val EXPIRED_AGO_SECONDS = 3_600L
        val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
    }
}
