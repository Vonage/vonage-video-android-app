package com.vonage.android.archiving

import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DisabledVonageArchiving : VonageArchiving {

    override fun bind(call: CallFacade): Flow<Map<ArchiveType, ArchiveId>> =
        flowOf(emptyMap())

    override suspend fun startArchive(sessionKey: String, type: ArchiveType, withSummary: Boolean): Result<ArchiveId> =
        Result.failure(Exception("Archiving feature is disabled"))

    override suspend fun stopArchive(sessionKey: String, type: ArchiveType): Result<Boolean> =
        Result.failure(Exception("Archiving feature is disabled"))

    override suspend fun getRecordings(sessionKey: String): Result<List<Archive>> =
        Result.failure(Exception("Archiving feature is disabled"))

}