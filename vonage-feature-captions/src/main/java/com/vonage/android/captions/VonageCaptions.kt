package com.vonage.android.captions

import com.vonage.android.kotlin.model.CallFacade

interface VonageCaptions {

    /** True when the captions feature is compiled in (captionsEnabled flavor). */
    val isCapable: Boolean

    fun init(callFacade: CallFacade, roomName: String, captionsId: String?)

    suspend fun enable(): Result<Unit>

    suspend fun disable(): Result<Unit>

}
