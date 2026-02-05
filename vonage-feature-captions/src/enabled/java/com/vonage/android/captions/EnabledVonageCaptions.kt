package com.vonage.android.captions

import com.vonage.android.captions.data.CaptionsRepository
import com.vonage.android.kotlin.model.CallFacade
import javax.inject.Inject

class EnabledVonageCaptions @Inject constructor(
    private val captionsRepository: CaptionsRepository,
): VonageCaptions {

    private var call: CallFacade? = null
    private var currentCaptionsId: String? = null
    private var roomName: String = ""

    override fun init(callFacade: CallFacade, roomName: String, captionsId: String?) {
        this.call = callFacade
        this.roomName = roomName
        this.currentCaptionsId = captionsId
    }

    override suspend fun enable(): Result<Unit> =
        captionsRepository.enableCaptions(roomName)
            .map {
                currentCaptionsId = it
                call?.enableCaptions(true)
            }

    override suspend fun disable(): Result<Unit> =
        currentCaptionsId?.let { captionsId ->
            captionsRepository.disableCaptions(roomName, captionsId)
                .map {
                    currentCaptionsId = null
                    call?.enableCaptions(false)
                    Unit
                }
        } ?: Result.failure(Exception("No current captions id"))

}
