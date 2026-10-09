package com.vonage.android.data.network.interceptor

import com.vonage.android.util.E2eTestFlags
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import javax.inject.Inject

/**
 * E2E-only: simulates a backend that enforces authentication, so the sign-in-required
 * flow can be tested against any backend. Inactive unless [E2eTestFlags.forceAuthRequired]
 * is set by the launch intent.
 *
 * Must be registered after [AuthorizationInterceptor]: requests that already carry a token
 * pass through, exactly like a real auth-enforcing backend would let them.
 */
class E2eForceUnauthorizedInterceptor @Inject constructor(
    private val e2eTestFlags: E2eTestFlags,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val isSignedOutSessionRequest = request.url.encodedPath.endsWith(CREATE_SESSION_PATH) &&
            request.header("Authorization") == null
        if (!e2eTestFlags.forceAuthRequired || !isSignedOutSessionRequest) {
            return chain.proceed(request)
        }
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(HTTP_UNAUTHORIZED)
            .message("Unauthorized")
            .body("".toResponseBody(null))
            .build()
    }

    private companion object {
        // First call of the v2 session bootstrap; a 401 here stops it before joinSession.
        const val CREATE_SESSION_PATH = "/v2/createSession"
    }
}
