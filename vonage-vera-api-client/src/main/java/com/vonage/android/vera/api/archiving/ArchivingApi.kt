package com.vonage.android.vera.api.archiving

import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.execute
import com.vonage.android.vera.api.http.executeIgnoringBody

/**
 * v2 archiving endpoints. Every call is scoped by the session key JWT returned by the backend
 * when the session was created.
 */
interface ArchivingApi {

    /** Lists the archives (recordings and transcriptions) of the session. */
    suspend fun searchArchives(sessionKey: String): Result<List<ServerArchive>>

    /**
     * Starts an archive and returns its id; [withTranscription] asks the backend for a
     * transcription archive instead of a recording, and [withSummary] (transcription only, `null`
     * leaves it to the backend) whether the transcript includes the AI summary.
     */
    suspend fun startArchive(
        sessionKey: String,
        withTranscription: Boolean = false,
        withSummary: Boolean? = null,
    ): Result<String>

    /**
     * Stops an archive. When [archiveId] is null the backend falls back to the archive it has
     * stored for the session (e.g. a recording started by another participant).
     */
    suspend fun stopArchive(sessionKey: String, archiveId: String?): Result<Unit>
}

internal class DefaultArchivingApi(
    private val service: ArchivingService,
) : ArchivingApi {

    override suspend fun searchArchives(sessionKey: String): Result<List<ServerArchive>> =
        execute("Failed getting archives") { service.searchArchives(SessionKeyRequest(sessionKey)) }
            .map { it.result.data.items }

    override suspend fun startArchive(
        sessionKey: String,
        withTranscription: Boolean,
        withSummary: Boolean?,
    ): Result<String> {
        val request = StartArchiveRequest(
            sessionKey = sessionKey,
            withTranscription = withTranscription,
            withSummary = withSummary,
        )
        return execute("Failed to start archiving") { service.startArchiving(request) }
            .map { it.result.data.id }
    }

    override suspend fun stopArchive(sessionKey: String, archiveId: String?): Result<Unit> =
        executeIgnoringBody("Failed to stop archiving") {
            service.stopArchiving(StopArchiveRequest(sessionKey, archiveId))
        }
}
