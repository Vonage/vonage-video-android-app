package com.vonage.android.shared.network

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RedactSecretsTest {

    @Test
    fun `redacts session key and token values`() {
        val line = """{"result":{"data":{"sessionId":"s","sessionKey":"eyJ.a.b","token" : "T1==","applicationId":"a"}}}"""

        assertEquals(
            """{"result":{"data":{"sessionId":"s","sessionKey":"[REDACTED]","token":"[REDACTED]","applicationId":"a"}}}""",
            line.redactSecrets(),
        )
    }

    @Test
    fun `leaves lines without secrets untouched`() {
        val line = "--> POST https://example.com/v2/startArchive (37-byte body)"

        assertEquals(line, line.redactSecrets())
    }
}
