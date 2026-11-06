package com.vonage.android.archiving

import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.Flow

/**
 * VonageArchiving provides call recording and archiving functionality for Vonage video sessions.
 * This interface defines operations to start, stop, monitor, and retrieve archiving recordings.
 */
interface VonageArchiving {

    /**
     * Binds to the archives running in a call. Emits the current ones first, then every change;
     * several can run at once, at most one per [ArchiveType].
     *
     * @param call The call facade to bind to
     * @return Flow of the running archives by type
     */
    fun bind(call: CallFacade): Flow<Map<ArchiveType, ArchiveId>>

    /**
     * Starts archiving/recording for the session identified by [sessionKey].
     * Initiates a new recording session and returns the archive ID for tracking.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing the archive ID on success, or an error on failure
     */
    suspend fun startArchive(sessionKey: String): Result<ArchiveId>

    /**
     * Stops the current active archive/recording for the session identified by [sessionKey].
     * Ends the recording and finalizes the archive for later retrieval.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result indicating success (true) or failure with error details
     */
    suspend fun stopArchive(sessionKey: String): Result<Boolean>

    /**
     * Retrieves all recorded archives for the session identified by [sessionKey].
     * Returns a list of previously recorded sessions with their metadata.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing a list of archives on success, or an error on failure
     */
    suspend fun getRecordings(sessionKey: String): Result<List<Archive>>

}
