package com.vonage.android.captions

import com.vonage.android.captions.data.CaptionsRepository
import com.vonage.android.kotlin.model.CallFacade

class EnabledVonageCaptions(
    private val captionsRepository: CaptionsRepository,
) : VonageCaptions {

    override val isCapable: Boolean = true

    private var call: CallFacade? = null
    private var sessionKey: String = ""

    /**
     * Tracked independently of the captions id: the backend returns a `null` id when captions
     * were already running for the session, which is still a successful enable.
     */
    private var isEnabled: Boolean = false

    override fun init(callFacade: CallFacade, sessionKey: String) {
        this.call = callFacade
        this.sessionKey = sessionKey
        this.isEnabled = false
    }

    override suspend fun enable(): Result<Unit> =
        captionsRepository.enableCaptions(sessionKey)
            .map {
                isEnabled = true
                call?.enableCaptions()
            }

    /** Local only: stops receiving captions on this device; the backend keeps them running for others. */
    override suspend fun disable(): Result<Unit> =
        if (isEnabled) {
            isEnabled = false
            call?.disableCaptions()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Captions are not enabled"))
        }

}
