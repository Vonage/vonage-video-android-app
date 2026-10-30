package com.vonage.android.postcalltranscription

/**
 * Post-call transcriptions for a Vonage video session.
 *
 * The Vonage Video API generates a transcription from an archive once the recording completes, so
 * transcriptions are listed after the call (goodbye screen), scoped by the session key.
 */
interface VonagePostCallTranscription {

    /**
     * Retrieves the transcriptions generated for the session identified by [sessionKey].
     *
     * @param sessionKey The session key JWT returned by the backend when the session was created
     * @return Result containing the transcriptions on success, or an error on failure
     */
    suspend fun getTranscriptions(sessionKey: String): Result<List<Transcription>>
}

/** A transcription generated from the archive [archiveId]. [url] is empty until it is [TranscriptionStatus.AVAILABLE]. */
data class Transcription(
    val archiveId: String,
    val status: TranscriptionStatus,
    val url: String,
)

enum class TranscriptionStatus {
    AVAILABLE,
    PENDING,
    FAILED,
}
