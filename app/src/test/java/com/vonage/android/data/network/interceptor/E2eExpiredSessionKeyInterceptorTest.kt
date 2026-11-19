package com.vonage.android.data.network.interceptor

import com.vonage.android.util.E2eTestFlags
import io.mockk.every
import io.mockk.mockk
import kotlin.io.encoding.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class E2eExpiredSessionKeyInterceptorTest {

    private val flags: E2eTestFlags = mockk()
    private val sut = E2eExpiredSessionKeyInterceptor(flags, currentTimeMillis = { NOW_SECONDS * 1_000 })

    @Test
    fun `given flag enabled replaces createSession sessionKey with an expired jwt`() {
        every { flags.expiredSessionKey } returns true

        val body = sut.intercept(chain(CREATE_SESSION_URL)).body.string()

        val data = Json.parseToJsonElement(body).jsonObject.getValue("result").jsonObject.getValue("data").jsonObject
        assertEquals("sid", data.getValue("sessionId").jsonPrimitive.content)
        assertEquals("app", data.getValue("applicationId").jsonPrimitive.content)
        val payload = jwtPayload(data.getValue("sessionKey").jsonPrimitive.content)
        assertEquals(NOW_SECONDS - 3_600, payload.getValue("exp").jsonPrimitive.long)
        assertEquals("room", payload.getValue("roomName").jsonPrimitive.content)
        assertEquals("sid", payload.getValue("sessionId").jsonPrimitive.content)
    }

    @Test
    fun `given flag disabled leaves the response untouched`() {
        every { flags.expiredSessionKey } returns false

        assertEquals(CREATE_SESSION_BODY, sut.intercept(chain(CREATE_SESSION_URL)).body.string())
    }

    @Test
    fun `given flag enabled leaves other endpoints untouched`() {
        every { flags.expiredSessionKey } returns true

        assertEquals(CREATE_SESSION_BODY, sut.intercept(chain(JOIN_SESSION_URL)).body.string())
    }

    @Test
    fun `given flag enabled leaves error responses untouched`() {
        every { flags.expiredSessionKey } returns true

        val response = sut.intercept(chain(CREATE_SESSION_URL, code = 500, body = "oops"))

        assertEquals(500, response.code)
        assertEquals("oops", response.body.string())
    }

    @Test
    fun `given flag enabled leaves an unexpected body untouched`() {
        every { flags.expiredSessionKey } returns true

        assertEquals("not json", sut.intercept(chain(CREATE_SESSION_URL, body = "not json")).body.string())
    }

    private fun chain(url: String, code: Int = 200, body: String = CREATE_SESSION_BODY): Interceptor.Chain {
        val request = Request.Builder().url(url).post(RequestBody.EMPTY).build()
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("msg")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
        return mockk {
            every { request() } returns request
            every { proceed(any()) } returns response
        }
    }

    private fun jwtPayload(jwt: String) = Json.parseToJsonElement(
        Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL).decode(jwt.split('.')[1]).decodeToString()
    ).jsonObject

    private companion object {
        const val NOW_SECONDS = 1_800_000_000L
        const val CREATE_SESSION_URL = "https://example.com/v2/createSession"
        const val JOIN_SESSION_URL = "https://example.com/v2/joinSession"
        const val CREATE_SESSION_BODY =
            """{"result":{"data":{"sessionId":"sid","sessionKey":"real-key","applicationId":"app","roomName":"room"}}}"""
    }
}
