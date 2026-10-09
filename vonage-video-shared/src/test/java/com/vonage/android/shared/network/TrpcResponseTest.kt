package com.vonage.android.shared.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrpcResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    private data class Payload(val id: String)

    @Test
    fun `decodes tRPC envelope payload`() {
        val body = """{"result":{"data":{"id":"abc","extra":1}}}"""

        val response = json.decodeFromString<TrpcResponse<Payload>>(body)

        assertEquals("abc", response.result.data.id)
    }

    @Test
    fun `encodes session key request body`() {
        assertEquals("""{"sessionKey":"key"}""", json.encodeToString(SessionKeyRequest("key")))
    }
}
