package com.vonage.android.okta.data

import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64
import kotlin.test.assertEquals
import kotlin.test.assertNull

class IdTokenDecoderTest {

    @Test
    fun `given token with name claim when name returns it`() {
        val token = jwt("""{"sub":"user-1","name":"Jane Doe"}""")

        assertEquals("Jane Doe", IdTokenDecoder.name(token))
    }

    @Test
    fun `given token without name claim when name returns null`() {
        val token = jwt("""{"sub":"user-1"}""")

        assertNull(IdTokenDecoder.name(token))
    }

    @Test
    fun `given null token when name returns null`() {
        assertNull(IdTokenDecoder.name(null))
    }

    @Test
    fun `given malformed token when name returns null`() {
        assertNull(IdTokenDecoder.name("not-a-jwt"))
        assertNull(IdTokenDecoder.name("a.%%%.c"))
    }

    private fun jwt(payload: String): String {
        val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
        val header = encoder.encode("""{"alg":"RS256"}""".encodeToByteArray())
        val body = encoder.encode(payload.encodeToByteArray())
        return "$header.$body.signature"
    }
}
