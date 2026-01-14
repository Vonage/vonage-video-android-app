package com.vonage.android.archiving

import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DisabledVonageArchiving : VonageArchiving {

    override fun bind(call: CallFacade): Flow<ArchivingState> =
        flowOf(ArchivingState.Idle)

    override suspend fun startArchive(roomName: String): Result<ArchiveId> =
        Result.failure(Exception("Not implemented"))

    override suspend fun stopArchive(roomName: String): Result<Boolean> =
        Result.failure(Exception("Not implemented"))

    override suspend fun getRecordings(roomName: String): Result<List<Archive>> =
        Result.failure(Exception("Not implemented"))

}