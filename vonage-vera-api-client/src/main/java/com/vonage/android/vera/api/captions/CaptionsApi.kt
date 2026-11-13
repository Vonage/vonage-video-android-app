package com.vonage.android.vera.api.captions

import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.execute

interface CaptionsApi {

    /**
     * Makes sure live captions are running for the session.
     *
     * @return The new captions id, or `null` when captions were already running for the session
     * (e.g. enabled by another participant). Both cases are a success.
     */
    suspend fun ensureCaptionsEnabled(sessionKey: String): Result<String?>
}

internal class DefaultCaptionsApi(
    private val service: CaptionsService,
) : CaptionsApi {

    override suspend fun ensureCaptionsEnabled(sessionKey: String): Result<String?> =
        execute("Failed enabling captions") { service.ensureCaptionsEnabled(SessionKeyRequest(sessionKey)) }
            .map { it.result.data.captionsId }
}
