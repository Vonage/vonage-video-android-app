package com.vonage.android.archiving

import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.kotlin.model.ArchivingState
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "EnabledVonageArchiving"

/**
 * EnabledVonageArchiving is the active implementation of VonageArchiving for call recording.
 * This class provides full archiving functionality including starting/stopping recordings,
 * monitoring state changes, and retrieving past recordings.
 * 
 * Used when the archiving feature is enabled in the build configuration.
 */
class EnabledVonageArchiving(
    private val archiveRepository: ArchiveRepository,
) : VonageArchiving {

    val mutex = Mutex()
    /**
     * Tracks the current active archive session ID.
     * Set when archiving starts, cleared when it stops.
     */
    private var currentArchiveId: ArchiveId? = null

    /**
     * Binds to a call's archiving state flow to monitor real-time state changes.
     * Automatically updates the current archive ID when archiving starts or stops.
     * 
     * This should be collected in a coroutine to continuously receive state updates
     * throughout the call lifecycle.
     *
     * @param call The call facade to monitor for archiving state changes
     * @return Flow emitting ArchivingState updates (Started with ID, Stopped, or Idle)
     */
    override fun bind(call: CallFacade): Flow<ArchivingState> =
        call.archivingStateFlow
            .map {
                mutex.withLock {
                    when (it) {
                        is ArchivingState.Started -> {
                            currentArchiveId = ArchiveId(it.id)
                            vonageLogger.d(TAG, "bind: archiving started, archiveId=${it.id}")
                        }
                        is ArchivingState.Stopped -> {
                            currentArchiveId = null
                            vonageLogger.d(TAG, "bind: archiving stopped, archiveId=${it.id}")
                        }
                        else -> {}
                    }
                    it
                }
            }

    /**
     * Starts a new archiving/recording session for the specified room.
     * Stores the returned archive ID for tracking and later stopping the recording.
     *
     * @param roomName The name of the room to start archiving
     * @return Result containing the ArchiveId on success, or an error on failure
     */
    override suspend fun startArchive(roomName: String): Result<ArchiveId> =
        archiveRepository.startArchive(roomName)
            .onFailure { vonageLogger.e(TAG, "startArchive: failed for room=$roomName", it) }
            .map { id ->
                mutex.withLock {
                    currentArchiveId = id
                    vonageLogger.d(TAG, "startArchive: succeeded, archiveId=${id.id}")
                    id
                }
            }

    /**
     * Stops the currently active archive/recording session for the room.
     * Requires an active recording to be in progress (currentArchiveId must be set).
     *
     * @param roomName The name of the room to stop archiving
     * @return Result with true on success, or failure if no active recording exists
     */
    override suspend fun stopArchive(roomName: String): Result<Boolean> =
        currentArchiveId?.let { archiveId ->
            archiveRepository.stopArchive(roomName, archiveId)
                .onFailure {
                    vonageLogger.e(TAG, "stopArchive: failed for room=$roomName, archiveId=${archiveId.id}", it)
                }
                .map {
                    mutex.withLock {
                        currentArchiveId = null
                        vonageLogger.d(TAG, "stopArchive: succeeded, archiveId=${archiveId.id}")
                        it
                    }
                }
        } ?: run {
            vonageLogger.e(TAG, "stopArchive: no current archive id for room=$roomName")
            Result.failure(Exception("No current archive id"))
        }

    /**
     * Retrieves all past recording archives for the specified room.
     * Returns a list of Archive objects containing metadata about completed recordings.
     *
     * @param roomName The name of the room to retrieve recordings for
     * @return Result containing a list of Archive objects, or an error on failure
     */
    override suspend fun getRecordings(roomName: String): Result<List<Archive>> =
        archiveRepository.getRecordings(roomName)

}
