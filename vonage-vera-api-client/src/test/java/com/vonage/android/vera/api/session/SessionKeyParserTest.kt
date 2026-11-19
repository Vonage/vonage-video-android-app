package com.vonage.android.vera.api.session

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionKeyParserTest {

    @Test
    fun `given jwt with exp claim returns expiration`() {
        val jwt = makeJwt(mapOf("exp" to 1_800_000_000L, "roomName" to ROOM_NAME))

        assertEquals(1_800_000_000.0, SessionKeyParser.extractExpiration(jwt))
    }

    @Test
    fun `given jwt with fractional exp claim returns expiration`() {
        val jwt = makeJwt(mapOf("exp" to 1_800_000_000.5, "roomName" to ROOM_NAME))

        assertEquals(1_800_000_000.5, SessionKeyParser.extractExpiration(jwt))
    }

    @Test
    fun `given jwt without exp claim returns null expiration`() {
        assertNull(SessionKeyParser.extractExpiration(makeJwt(mapOf("roomName" to ROOM_NAME))))
    }

    @Test
    fun `given non numeric exp claim returns null expiration`() {
        assertNull(SessionKeyParser.extractExpiration(makeJwt(mapOf("exp" to "soon"))))
    }

    @Test
    fun `given invalid jwt returns null expiration`() {
        assertNull(SessionKeyParser.extractExpiration("not-a-jwt"))
        assertNull(SessionKeyParser.extractExpiration("a.%%%.c"))
    }

    @Test
    fun `given jwt without exp claim is not expired`() {
        assertFalse(SessionKeyParser.isExpired(makeJwt(mapOf("roomName" to ROOM_NAME))))
    }

    @Test
    fun `given invalid jwt is not expired`() {
        assertFalse(SessionKeyParser.isExpired("not-a-jwt"))
    }

    @Test
    fun `given future exp claim is not expired`() {
        val jwt = makeJwt(mapOf("exp" to epochSecondsFromNow(3_600)))

        assertFalse(SessionKeyParser.isExpired(jwt))
    }

    @Test
    fun `given past exp claim is expired`() {
        val jwt = makeJwt(mapOf("exp" to epochSecondsFromNow(-3_600)))

        assertTrue(SessionKeyParser.isExpired(jwt))
    }

    @Test
    fun `given exp equal to now is expired`() {
        val nowSeconds = 1_800_000_000L
        val jwt = makeJwt(mapOf("exp" to nowSeconds))

        assertTrue(SessionKeyParser.isExpired(jwt, nowMillis = nowSeconds * 1_000))
        assertFalse(SessionKeyParser.isExpired(jwt, nowMillis = nowSeconds * 1_000 - 1))
    }

    private companion object {
        const val ROOM_NAME = "heart-of-gold"
    }
}
