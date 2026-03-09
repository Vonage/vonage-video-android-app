package com.vonage.android.captions

import com.vonage.android.kotlin.model.CallFacade

@Suppress("EmptyFunctionBlock")
class DisabledVonageCaptions : VonageCaptions {

    override fun init(
        callFacade: CallFacade,
        roomName: String,
        captionsId: String?
    ) {

    }

    override suspend fun enable(): Result<Unit> =
        Result.failure(Exception("Captions feature is disabled"))

    override suspend fun disable(): Result<Unit> =
        Result.failure(Exception("Captions feature is disabled"))
}