package com.vonage.android.kotlin.model

import com.opentok.android.OpentokError
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionEventTest {

    @Test
    fun `Connected should be a singleton object`() {
        val event1 = SessionEvent.Connected
        val event2 = SessionEvent.Connected
        assertTrue(event1 === event2)
        assertTrue(event1 is SessionEvent)
    }

    @Test
    fun `Disconnected should be a singleton object`() {
        val event1 = SessionEvent.Disconnected
        val event2 = SessionEvent.Disconnected
        assertTrue(event1 === event2)
        assertTrue(event1 is SessionEvent)
    }

    @Test
    fun `StreamReceived should contain stream ID`() {
        val streamId = "stream-123"
        val event = SessionEvent.StreamReceived(streamId)
        
        assertEquals(streamId, event.streamId)
        assertTrue(event is SessionEvent)
    }

    @Test
    fun `StreamDropped should contain stream ID`() {
        val streamId = "stream-456"
        val event = SessionEvent.StreamDropped(streamId)
        
        assertEquals(streamId, event.streamId)
        assertTrue(event is SessionEvent)
    }

    @Test
    fun `Error should contain OpentokError`() {
        val error = mockk<OpentokError>()
        val event = SessionEvent.Error(error)
        
        assertEquals(error, event.error)
        assertTrue(event is SessionEvent)
    }

    @Test
    fun `StreamReceived with different IDs should not be equal`() {
        val event1 = SessionEvent.StreamReceived("stream-1")
        val event2 = SessionEvent.StreamReceived("stream-2")
        
        assertTrue(event1 != event2)
    }

    @Test
    fun `StreamReceived with same ID should be equal`() {
        val event1 = SessionEvent.StreamReceived("stream-1")
        val event2 = SessionEvent.StreamReceived("stream-1")
        
        assertEquals(event1, event2)
    }

    @Test
    fun `StreamDropped with same ID should be equal`() {
        val event1 = SessionEvent.StreamDropped("stream-1")
        val event2 = SessionEvent.StreamDropped("stream-1")
        
        assertEquals(event1, event2)
    }

    @Test
    fun `should support when expression for sealed interface`() {
        val events: List<SessionEvent> = listOf(
            SessionEvent.Connected,
            SessionEvent.Disconnected,
            SessionEvent.StreamReceived("stream-1"),
            SessionEvent.StreamDropped("stream-2"),
            SessionEvent.Error(mockk())
        )

        val results = events.map { event ->
            when (event) {
                is SessionEvent.Connected -> "connected"
                is SessionEvent.Disconnected -> "disconnected"
                is SessionEvent.StreamReceived -> "stream_received"
                is SessionEvent.StreamDropped -> "stream_dropped"
                is SessionEvent.Error -> "error"
            }
        }

        assertEquals(5, results.size)
        assertEquals("connected", results[0])
        assertEquals("disconnected", results[1])
        assertEquals("stream_received", results[2])
        assertEquals("stream_dropped", results[3])
        assertEquals("error", results[4])
    }
}
