package com.vonage.android.archiving

import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "DefaultVonageArchiving"

/**
 * Implementation of VonageArchiving for recordings and transcriptions: starting/stopping them,
 * monitoring the running ones, and retrieving past ones.
 *
 * Only archive types in [enabledTypes] are handled, so recording and transcription can be turned
 * on independently. Other types are left out of [bind] and [getRecordings], and starting or
 * stopping them fails without reaching the backend.
 */
class DefaultVonageArchiving(
    private val archiveRepository: ArchiveRepository,
    private val enabledTypes: Set<ArchiveType>,
) : VonageArchiving {

    val mutex = Mutex()

    /** Archives the session reports as running; their IDs win over locally started ones. */
    private var sessionArchiveIds = emptyMap<ArchiveType, ArchiveId>()

    /** Archives started by this client that the session has not reported yet. */
    private val startedArchiveIds = mutableMapOf<ArchiveType, ArchiveId>()

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
                archives.entries
                    .associate { (id, name) -> ArchiveType.fromArchiveName(name) to ArchiveId(id) }
                    .filterKeys { it in enabledTypes }
            }
            .onEach { archives ->
                mutex.withLock {
                    sessionArchiveIds = archives
                    startedArchiveIds.keys.removeAll(archives.keys)
                }
                vonageLogger.d(TAG, "bind: running archives=${archives.mapValues { it.value.id }}")
            }

    /**
     * Starts a new archive of [type] for the session identified by [sessionKey].
     * Stores the returned archive ID for tracking and later stopping it.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @param type Recording or transcription archive
     * @return Result containing the ArchiveId on success, or an error on failure
     */
    override suspend fun startArchive(sessionKey: String, type: ArchiveType): Result<ArchiveId> {
        if (type !in enabledTypes) return disabled(type)
        return archiveRepository.startArchive(
            sessionKey = sessionKey,
            withTranscription = type == ArchiveType.TRANSCRIPTION,
        )
            .onFailure { vonageLogger.e(TAG, "startArchive: $type failed", it) }
            .map { id ->
                mutex.withLock {
                    startedArchiveIds[type] = id
                    vonageLogger.d(TAG, "startArchive: $type succeeded, archiveId=${id.id}")
                    id
                }
            }
    }

    /**
     * Stops the active archive of [type] for the session identified by [sessionKey].
     *
     * A recording sends the tracked archive ID when known; otherwise the backend stops the archive
     * it has stored for the session (e.g. one started by another participant before this client
     * bound). A transcription always sends its explicit ID: without one the backend fallback could
     * stop the recording instead, so the call fails without reaching the backend.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @param type Recording or transcription archive
     * @return Result with true on success, or an error on failure
     */
    override suspend fun stopArchive(sessionKey: String, type: ArchiveType): Result<Boolean> {
        if (type !in enabledTypes) return disabled(type)
        val archiveId = mutex.withLock { sessionArchiveIds[type] ?: startedArchiveIds[type] }
        if (archiveId == null && type == ArchiveType.TRANSCRIPTION) {
            vonageLogger.e(TAG, "stopArchive: no active transcription archive")
            return Result.failure(IllegalStateException("No active transcription archive"))
        }
        return archiveRepository.stopArchive(sessionKey, archiveId)
            .onFailure { vonageLogger.e(TAG, "stopArchive: $type failed, archiveId=${archiveId?.id}", it) }
            .map {
                mutex.withLock {
                    startedArchiveIds.remove(type)
                    vonageLogger.d(TAG, "stopArchive: $type succeeded, archiveId=${archiveId?.id}")
                    it
                }
            }
    }

    /**
     * Retrieves the past archives of the enabled types for the session identified by [sessionKey].
     * Fails without reaching the backend when no type is enabled.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing a list of Archive objects, or an error on failure
     */
    override suspend fun getRecordings(sessionKey: String): Result<List<Archive>> {
        if (enabledTypes.isEmpty()) return Result.failure(IllegalStateException("Archiving is disabled"))
        return archiveRepository.getRecordings(sessionKey)
            .map { archives -> archives.filter { it.type in enabledTypes } }
    }

    private fun <T> disabled(type: ArchiveType): Result<T> {
        vonageLogger.e(TAG, "$type archives are disabled")
        return Result.failure(IllegalStateException("$type archives are disabled"))
    }
}
