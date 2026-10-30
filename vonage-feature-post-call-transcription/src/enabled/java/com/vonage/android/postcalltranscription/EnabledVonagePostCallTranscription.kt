package com.vonage.android.postcalltranscription

/**
 * Active implementation, compiled in when `meetingRoomSettings.allowPostCallTranscription` is `true`.
 *
 * Scaffold only: there is no backend endpoint for listing transcriptions yet, so it reports none.
 * When the endpoint lands, add a `Retrofit` parameter to `PostCallTranscriptionModule` in both
 * flavors and back this with a repository, like `vonage-feature-archiving`.
 */
class EnabledVonagePostCallTranscription : VonagePostCallTranscription {

    override suspend fun getTranscriptions(sessionKey: String): Result<List<Transcription>> =
        Result.success(emptyList())
}
