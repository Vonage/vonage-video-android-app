package com.vonage.android.kotlin.ext

import com.vonage.android.kotlin.model.Participant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun Flow<ImmutableList<Participant>>.mapSorted(): Flow<ImmutableList<Participant>> =
    map { participants ->
        participants
            .sortedWith(
                compareByDescending<Participant> { it.isScreenShare }
                    .thenByDescending { it.creationTime }
            )
            .toImmutableList()
    }

internal fun Iterable<Participant>.firstScreenSharing() =
    firstOrNull { it.isScreenShare }
