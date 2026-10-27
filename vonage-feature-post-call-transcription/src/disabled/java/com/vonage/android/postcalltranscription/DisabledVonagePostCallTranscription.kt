package com.vonage.android.postcalltranscription

class DisabledVonagePostCallTranscription : VonagePostCallTranscription {

    override suspend fun getTranscriptions(sessionKey: String): Result<List<Transcription>> =
        Result.failure(Exception("Post-call transcription feature is disabled"))
}
