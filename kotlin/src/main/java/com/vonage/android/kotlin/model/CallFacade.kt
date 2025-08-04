package com.vonage.android.kotlin.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Stable
interface CallFacade {
    val participantsStateFlow: StateFlow<ImmutableList<Participant>>
    val publisherAudioLevelStateFlow: StateFlow<Float>

    fun connect(): Flow<SessionEvent>
    fun observePublisherAudio(): Flow<Float>
    fun togglePublisherVideo()
    fun togglePublisherCamera()
    fun togglePublisherAudio()
    fun pauseSession()
    fun resumeSession()
    fun endSession()
}
