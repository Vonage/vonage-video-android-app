package com.vonage.android.captions

import com.vonage.android.kotlin.model.CallFacade

interface VonageCaptions {

    /** True when the captions feature is compiled in (captionsEnabled flavor). */
    val isCapable: Boolean

    /**
     * Binds captions to the active call.
     *
     * @param callFacade The connected call.
     * @param sessionKey The session key JWT returned by the backend when the session was created.
     */
    fun init(callFacade: CallFacade, sessionKey: String)

    suspend fun enable(): Result<Unit>

    suspend fun disable(): Result<Unit>

}
