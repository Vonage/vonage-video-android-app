package com.vonage.android.postcalltranscription

import com.vonage.android.postcalltranscription.di.PostCallTranscriptionModule
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EnabledVonagePostCallTranscriptionTest {

    private val sut = PostCallTranscriptionModule.provideVonagePostCallTranscription()

    @Test
    fun `module should provide the enabled implementation`() {
        assertTrue(sut is EnabledVonagePostCallTranscription)
    }

    @Test
    fun `getTranscriptions should succeed with no transcriptions`() = runTest {
        assertEquals(Result.success(emptyList<Transcription>()), sut.getTranscriptions("session-key"))
    }
}
