package com.vonage.android.screen.waiting

import android.content.Context
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.VonageVideoClient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreatePublisherUseCase @Inject constructor(
    @param:ApplicationContext val context: Context,
    val vonageVideoClient: VonageVideoClient,
) {
    operator fun invoke(): Participant =
        vonageVideoClient.buildPublisher()

}
