package com.vonage.android.captions

import com.vonage.android.kotlin.model.CallFacade

interface VonageCaptions {

    fun init(callFacade: CallFacade, roomName: String, captionsId: String?)

    suspend fun enable(): Result<Unit>

    suspend fun disable(): Result<Unit>

}
