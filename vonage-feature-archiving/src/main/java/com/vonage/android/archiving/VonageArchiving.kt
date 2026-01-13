package com.vonage.android.archiving

import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@JvmInline
value class ArchiveId(val id: String)

class VonageArchiving(
    private val archiveRepository: ArchiveRepository,
) {

    private var currentArchiveId: ArchiveId? = null

    suspend fun bind(call: CallFacade) {
        call.archivingStateFlow
            .onEach {

            }
            .collect()
    }

    suspend fun startArchive(roomName: String): Result<ArchiveId> =
        archiveRepository.startArchive(roomName)
            .map { id ->
                currentArchiveId = id
                id
            }

    suspend fun stopArchive(roomName: String): Result<Boolean> =
        currentArchiveId?.let { archiveId ->
            archiveRepository.stopArchive(roomName, archiveId)
                .map {
                    currentArchiveId = null
                    it
                }
        } ?: Result.failure(Exception("No current archive id"))

}
