package com.vonage.android.screen.waiting

import android.content.Context
import com.vonage.android.kotlin.Participant
import com.vonage.android.kotlin.PublisherFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreatePublisherUseCase @Inject constructor(
    @param:ApplicationContext val context: Context,
    val publisherFactory: PublisherFactory,
) {
    operator fun invoke(): Participant =
        publisherFactory.buildPublisher(context)

}
