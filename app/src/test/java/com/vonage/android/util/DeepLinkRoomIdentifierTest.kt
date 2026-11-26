package com.vonage.android.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64

class DeepLinkRoomIdentifierTest {

    @Test
    fun `given a room name THEN it is returned unchanged`() {
        assertEquals("my-room", resolveDeepLinkRoomName("my-room"))
    }

    @Test
    fun `given a session key with roomName claim THEN the room name is returned`() {
        val jwt = jwt("""{"sessionId":"sid","roomName":"my-room","exp":1}""")

        assertEquals("my-room", resolveDeepLinkRoomName(jwt))
    }

    @Test
    fun `given a session key without roomName claim THEN null is returned`() {
        assertNull(resolveDeepLinkRoomName(jwt("""{"sessionId":"sid"}""")))
    }

    @Test
    fun `given a session key with null roomName THEN null is returned`() {
        assertNull(resolveDeepLinkRoomName(jwt("""{"sessionId":"sid","roomName":null}""")))
    }

    @Test
    fun `given a jwt-shaped value with an undecodable payload THEN null is returned`() {
        assertNull(resolveDeepLinkRoomName("a.%%%.c"))
    }

    @Test
    fun `given a dotted value with an empty part THEN it is treated as a room name`() {
        assertEquals("a..c", resolveDeepLinkRoomName("a..c"))
    }

    private fun jwt(payload: String): String {
        val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
        return listOf("""{"alg":"HS256"}""", payload)
            .joinToString(".") { encoder.encode(it.encodeToByteArray()) } + ".signature"
    }
}
