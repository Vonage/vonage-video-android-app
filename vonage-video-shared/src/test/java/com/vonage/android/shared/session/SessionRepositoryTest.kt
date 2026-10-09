package com.vonage.android.shared.session

import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import com.vonage.android.shared.network.TrpcResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SessionRepositoryTest {

    private val apiService: SessionApiService = mockk()
    private val sut = SessionRepository(apiService)

    @Test
    fun `given both calls succeed returns mapped SessionInfo`() = runTest {
        givenCreateSessionSucceeds()
        givenJoinSessionSucceeds()

        val result = sut.getSession(ROOM_NAME)

        assertEquals(
            Result.success(
                SessionInfo(
                    applicationId = "applicationId",
                    sessionId = "sessionId",
                    token = "token",
                    sessionKey = SESSION_KEY,
                )
            ),
            result,
        )
        coVerify { apiService.createSession(CreateSessionRequest(ROOM_NAME)) }
        coVerify { apiService.joinSession(SessionKeyRequest(SESSION_KEY)) }
    }

    @Test
    fun `given createSession error response returns failure without joining`() = runTest {
        coEvery { apiService.createSession(any()) } returns Response.error(400, "".toResponseBody())

        val result = sut.getSession(ROOM_NAME)

        assertTrue(result.isFailure)
        assertEquals("Failed creating session", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { apiService.joinSession(any()) }
    }

    @Test
    fun `given createSession null body returns failure`() = runTest {
        coEvery { apiService.createSession(any()) } returns Response.success(null)

        val result = sut.getSession(ROOM_NAME)

        assertTrue(result.isFailure)
        assertEquals("Failed creating session", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given createSession unauthorized response returns SessionUnauthorizedException without joining`() =
        runTest {
            coEvery { apiService.createSession(any()) } returns Response.error(401, "".toResponseBody())

            val result = sut.getSession(ROOM_NAME)

            assertIs<SessionUnauthorizedException>(result.exceptionOrNull())
            coVerify(exactly = 0) { apiService.joinSession(any()) }
        }

    @Test
    fun `given joinSession error response returns failure`() = runTest {
        givenCreateSessionSucceeds()
        coEvery { apiService.joinSession(any()) } returns Response.error(500, "".toResponseBody())

        val result = sut.getSession(ROOM_NAME)

        assertTrue(result.isFailure)
        assertEquals("Failed joining session", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given joinSession null body returns failure`() = runTest {
        givenCreateSessionSucceeds()
        coEvery { apiService.joinSession(any()) } returns Response.success(null)

        val result = sut.getSession(ROOM_NAME)

        assertTrue(result.isFailure)
        assertEquals("Failed joining session", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given joinSession unauthorized response returns SessionUnauthorizedException`() = runTest {
        givenCreateSessionSucceeds()
        coEvery { apiService.joinSession(any()) } returns Response.error(401, "".toResponseBody())

        val result = sut.getSession(ROOM_NAME)

        assertIs<SessionUnauthorizedException>(result.exceptionOrNull())
    }

    @Test
    fun `given api throws exception returns failure`() = runTest {
        coEvery { apiService.createSession(any()) } throws Exception("Network error")

        val result = sut.getSession(ROOM_NAME)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `decodes backend payloads ignoring extra fields`() {
        val json = Json { ignoreUnknownKeys = true }
        val create = json.decodeFromString<TrpcResponse<CreateSessionResponse>>(
            """{"result":{"data":{"sessionId":"s","sessionKey":"k","applicationId":"a",""" +
                """"roomName":"room","p2p":false,"partnerId":"a"}}}"""
        )
        val join = json.decodeFromString<TrpcResponse<JoinSessionResponse>>(
            """{"result":{"data":{"token":"t","applicationId":"a","sessionId":"s","sessionKey":"k"}}}"""
        )

        assertEquals(CreateSessionResponse(sessionId = "s", sessionKey = "k", applicationId = "a"), create.result.data)
        assertEquals("t", join.result.data.token)
    }

    @Test
    fun `session info toString does not leak secrets`() {
        val info = SessionInfo(applicationId = "a", sessionId = "s", token = "secret-token", sessionKey = "secret-key")

        assertTrue("secret" !in info.toString())
    }

    private fun givenCreateSessionSucceeds() {
        coEvery { apiService.createSession(CreateSessionRequest(ROOM_NAME)) } returns Response.success(
            TrpcResponse(
                TrpcResult(
                    CreateSessionResponse(
                        sessionId = "sessionId",
                        sessionKey = SESSION_KEY,
                        applicationId = "applicationId",
                    )
                )
            )
        )
    }

    private fun givenJoinSessionSucceeds() {
        coEvery { apiService.joinSession(SessionKeyRequest(SESSION_KEY)) } returns Response.success(
            TrpcResponse(TrpcResult(JoinSessionResponse(token = "token")))
        )
    }

    private companion object {
        const val ROOM_NAME = "any-room-name"
        const val SESSION_KEY = "session-key"
    }
}
