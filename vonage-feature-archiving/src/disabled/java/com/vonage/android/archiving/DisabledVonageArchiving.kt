package com.vonage.android.archiving

import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DisabledVonageArchiving : VonageArchiving {

    override fun bind(call: CallFacade): Flow<ArchivingState> =
        flowOf(ArchivingState.Idle)

    override suspend fun startArchive(sessionKey: String): Result<ArchiveId> =
        Result.failure(Exception("Archiving feature is disabled"))

    override suspend fun stopArchive(sessionKey: String): Result<Boolean> =
        Result.failure(Exception("Archiving feature is disabled"))

    override suspend fun getRecordings(sessionKey: String): Result<List<Archive>> =
        Result.failure(Exception("Archiving feature is disabled"))

}