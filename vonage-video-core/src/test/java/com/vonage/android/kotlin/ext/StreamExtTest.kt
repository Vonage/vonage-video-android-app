package com.vonage.android.kotlin.ext

import com.opentok.android.Stream
import com.vonage.android.kotlin.model.VideoSource
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class StreamExtTest {

    @Test
    fun `toParticipantType should return CAMERA for camera stream`() {
        val stream = mockk<Stream>()
        every { stream.streamVideoType } returns Stream.StreamVideoType.StreamVideoTypeCamera

        val result = stream.toParticipantType()

        assertEquals(VideoSource.CAMERA, result)
    }

    @Test
    fun `toParticipantType should return SCREEN for screen stream`() {
        val stream = mockk<Stream>()
        every { stream.streamVideoType } returns Stream.StreamVideoType.StreamVideoTypeScreen

        val result = stream.toParticipantType()

        assertEquals(VideoSource.SCREEN, result)
    }

    @Test
    fun `toParticipantType should return SCREEN for custom stream`() {
        val stream = mockk<Stream>()
        every { stream.streamVideoType } returns Stream.StreamVideoType.StreamVideoTypeCustom

        val result = stream.toParticipantType()

        assertEquals(VideoSource.SCREEN, result)
    }
}
