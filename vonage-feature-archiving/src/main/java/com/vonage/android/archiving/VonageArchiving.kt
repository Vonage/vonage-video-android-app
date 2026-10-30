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
     * Starts an archive of [type] for the session identified by [sessionKey]. A transcription
     * archive is requested with `withTranscription: true`; the backend sets its output mode and tag.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @param type Recording (default) or transcription archive
     * @param withSummary Transcription only: also generate an AI summary of the transcript
     * @return Result containing the archive ID on success, or an error on failure
     */
    suspend fun startArchive(
        sessionKey: String,
        type: ArchiveType = ArchiveType.RECORDING,
        withSummary: Boolean = false,
    ): Result<ArchiveId>

    /**
     * Stops the active archive of [type] for the session identified by [sessionKey], leaving any
     * archive of the other type running.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @param type Recording (default) or transcription archive
     * @return Result indicating success (true) or failure with error details
     */
    suspend fun stopArchive(sessionKey: String, type: ArchiveType = ArchiveType.RECORDING): Result<Boolean>

    /**
     * Retrieves all recorded archives for the session identified by [sessionKey].
     * Returns a list of previously recorded sessions with their metadata.
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing a list of archives on success, or an error on failure
     */
    suspend fun getRecordings(sessionKey: String): Result<List<Archive>>

}
