package com.vonage.android.archiving

import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    /** Archives the session reports as running; their IDs win over the locally started one. */
    private var sessionArchiveIds = emptyMap<ArchiveType, ArchiveId>()

    /** Recording started by this client that the session has not reported yet. */
    private var currentArchiveId: ArchiveId? = null

    /**
     * Binds to the archives running in a call, by type, and tracks their IDs so archives started by
     * other participants can be stopped by ID too.
     *
     * This should be collected in a coroutine to continuously receive state updates
     * throughout the call lifecycle.
     *
     * @param call The call facade to monitor for running archives
     * @return Flow of the running archives by type, starting with the current ones
     */
    override fun bind(call: CallFacade): Flow<Map<ArchiveType, ArchiveId>> =
        call.activeArchives
            .map { archives ->
                archives.entries.associate { (id, name) -> ArchiveType.fromArchiveName(name) to ArchiveId(id) }
            }
            .onEach { archives ->
                mutex.withLock {
                    sessionArchiveIds = archives
                    if (ArchiveType.RECORDING in archives) currentArchiveId = null
                }
                vonageLogger.d(TAG, "bind: running archives=${archives.mapValues { it.value.id }}")
            }

    /**
     * Starts a new archiving/recording session for the session identified by [sessionKey].
     * Stores the returned archive ID for tracking and later stopping the recording.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing the ArchiveId on success, or an error on failure
     */
    override suspend fun startArchive(sessionKey: String): Result<ArchiveId> =
        archiveRepository.startArchive(sessionKey)
            .onFailure { vonageLogger.e(TAG, "startArchive: failed", it) }
            .map { id ->
                mutex.withLock {
                    currentArchiveId = id
                    vonageLogger.d(TAG, "startArchive: succeeded, archiveId=${id.id}")
                    id
                }
            }

    /**
     * Stops the currently active archive/recording for the session identified by [sessionKey].
     * Sends the tracked archive ID when known; otherwise the backend stops the archive it has
     * stored for the session (e.g. one started by another participant before this client bound).
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result with true on success, or an error on failure
     */
    override suspend fun stopArchive(sessionKey: String): Result<Boolean> {
        val archiveId = mutex.withLock { sessionArchiveIds[ArchiveType.RECORDING] ?: currentArchiveId }
        return archiveRepository.stopArchive(sessionKey, archiveId)
            .onFailure { vonageLogger.e(TAG, "stopArchive: failed, archiveId=${archiveId?.id}", it) }
            .map {
                mutex.withLock {
                    currentArchiveId = null
                    vonageLogger.d(TAG, "stopArchive: succeeded, archiveId=${archiveId?.id}")
                    it
                }
            }
    }

    /**
     * Retrieves all past recording archives for the session identified by [sessionKey].
     * Returns a list of Archive objects containing metadata about completed recordings.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing a list of Archive objects, or an error on failure
     */
    override suspend fun getRecordings(sessionKey: String): Result<List<Archive>> =
        archiveRepository.getRecordings(sessionKey)

}
