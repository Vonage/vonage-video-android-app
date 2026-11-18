package com.vonage.android.captions.data

import com.vonage.android.vera.api.captions.CaptionsApi

class CaptionsRepository(
    private val captionsApi: CaptionsApi,
) {

    /**
     * Makes sure live captions are running for the session.
     *
     * @return The new captions id, or `null` when captions were already running for the session
     * (e.g. enabled by another participant). Both cases are a success.
     */
    suspend fun enableCaptions(sessionKey: String): Result<String?> =
        captionsApi.ensureCaptionsEnabled(sessionKey)
}
