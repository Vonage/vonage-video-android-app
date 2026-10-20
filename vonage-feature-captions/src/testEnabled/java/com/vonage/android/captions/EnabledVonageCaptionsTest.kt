package com.vonage.android.captions

import com.vonage.android.captions.data.CaptionsRepository
import com.vonage.android.kotlin.model.CallFacade
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EnabledVonageCaptionsTest {

    private val captionsRepository: CaptionsRepository = mockk()
    private val callFacade: CallFacade = mockk(relaxed = true)
    private val sut: VonageCaptions = EnabledVonageCaptions(
        captionsRepository = captionsRepository,
    )

    @Test
    fun `isCapable should be true`() {
        assertTrue(sut.isCapable)
    }

    @Test
    fun `when enable success then enableCaptions`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns success("captions-456")

        sut.init(callFacade, SESSION_KEY)
        val result = sut.enable()

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { captionsRepository.enableCaptions(SESSION_KEY) }
        verify { callFacade.enableCaptions() }
    }

    @Test
    fun `when enable succeeds with null captionsId then still enableCaptions`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns success(null)

        sut.init(callFacade, SESSION_KEY)
        val result = sut.enable()

        assertTrue(result.isSuccess)
        verify { callFacade.enableCaptions() }
    }

    @Test
    fun `when enable fails then returns failure`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns failure(Exception("Network error"))

        sut.init(callFacade, SESSION_KEY)
        val result = sut.enable()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        verify(exactly = 0) { callFacade.enableCaptions() }
    }

    @Test
    fun `when disable after enable then disableCaptions`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns success("captions-789")

        sut.init(callFacade, SESSION_KEY)
        sut.enable()
        val result = sut.disable()

        assertTrue(result.isSuccess)
        verify { callFacade.disableCaptions() }
    }

    @Test
    fun `when disable after enable with null captionsId then disableCaptions`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns success(null)

        sut.init(callFacade, SESSION_KEY)
        sut.enable()
        val result = sut.disable()

        assertTrue(result.isSuccess)
        verify { callFacade.disableCaptions() }
    }

    @Test
    fun `when disable without enable then returns failure`() = runTest {
        sut.init(callFacade, SESSION_KEY)
        val result = sut.disable()

        assertTrue(result.isFailure)
        verify(exactly = 0) { callFacade.disableCaptions() }
    }

    @Test
    fun `when disable twice then second call fails`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns success(null)

        sut.init(callFacade, SESSION_KEY)
        sut.enable()
        sut.disable()
        val result = sut.disable()

        assertTrue(result.isFailure)
        verify(exactly = 1) { callFacade.disableCaptions() }
    }

    @Test
    fun `when enable multiple times then calls repository each time`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returnsMany listOf(
            success("captions-111"),
            success(null),
        )

        sut.init(callFacade, SESSION_KEY)

        assertTrue(sut.enable().isSuccess)
        assertTrue(sut.enable().isSuccess)

        coVerify(exactly = 2) { captionsRepository.enableCaptions(SESSION_KEY) }
    }

    @Test
    fun `when init then captions are not enabled on the call`() = runTest {
        sut.init(callFacade, SESSION_KEY)

        verify(exactly = 0) { callFacade.enableCaptions() }
    }

    @Test
    fun `when re-init after enable then state is reset`() = runTest {
        coEvery { captionsRepository.enableCaptions(SESSION_KEY) } returns success(null)

        sut.init(callFacade, SESSION_KEY)
        sut.enable()
        sut.init(callFacade, "another-session-key")

        assertTrue(sut.disable().isFailure)
    }

    private companion object {
        const val SESSION_KEY = "test-session-key"
    }
}
