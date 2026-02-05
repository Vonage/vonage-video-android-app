package com.vonage.android.captions

import com.vonage.android.captions.data.CaptionsRepository
import com.vonage.android.kotlin.model.CallFacade
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
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
    fun `when enable success then enableCaptions`() = runTest {
        val roomName = "test-room"
        val captionsId = "captions-456"
        val expectedResult = success(captionsId)

        coEvery { captionsRepository.enableCaptions(roomName) } returns expectedResult

        sut.init(callFacade, roomName, null)
        val result = sut.enable()

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { captionsRepository.enableCaptions(roomName) }
        verify { callFacade.enableCaptions(true) }
    }

    @Test
    fun `when enable fails then returns failure`() = runTest {
        val roomName = "test-room"
        val exception = Exception("Network error")
        val expectedResult = failure<String>(exception)

        coEvery { captionsRepository.enableCaptions(roomName) } returns expectedResult

        sut.init(callFacade, roomName, null)
        val result = sut.enable()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        coVerify { captionsRepository.enableCaptions(roomName) }
        verify(exactly = 0) { callFacade.enableCaptions(any()) }
    }

    @Test
    fun `when disable success then disableCaptions and clears id`() = runTest {
        val roomName = "test-room"
        val captionsId = "captions-789"

        coEvery { captionsRepository.enableCaptions(roomName) } returns success(captionsId)
        coEvery { captionsRepository.disableCaptions(roomName, captionsId) } returns success("")

        sut.init(callFacade, roomName, captionsId)
        sut.enable()
        val result = sut.disable()

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { captionsRepository.disableCaptions(roomName, captionsId) }
        verify { callFacade.enableCaptions(false) }
    }

    @Test
    fun `when disable with no captionsId then returns failure`() = runTest {
        val roomName = "test-room"

        sut.init(callFacade, roomName, null)
        val result = sut.disable()

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { captionsRepository.disableCaptions(any(), any()) }
        verify(exactly = 0) { callFacade.enableCaptions(any()) }
    }

    @Test
    fun `when disable fails then returns failure`() = runTest {
        val roomName = "test-room"
        val captionsId = "captions-999"
        val exception = Exception("API error")

        coEvery { captionsRepository.enableCaptions(roomName) } returns success(captionsId)
        coEvery { captionsRepository.disableCaptions(roomName, captionsId) } returns failure(exception)

        sut.init(callFacade, roomName, null)
        sut.enable()
        val result = sut.disable()

        assertTrue(result.isFailure)
        assertEquals("API error", result.exceptionOrNull()?.message)
        verify(exactly = 0) { callFacade.enableCaptions(false) }
    }

    @Test
    fun `when enable multiple times then updates current captionsId`() = runTest {
        val roomName = "test-room"
        val firstCaptionsId = "captions-111"
        val secondCaptionsId = "captions-222"

        coEvery { captionsRepository.enableCaptions(roomName) } returnsMany listOf(
            success(firstCaptionsId),
            success(secondCaptionsId)
        )
        coEvery { captionsRepository.disableCaptions(roomName, any()) } returns success("")

        sut.init(callFacade, roomName, null)

        val firstResult = sut.enable()
        assertTrue(firstResult.isSuccess)

        // Second enable should work with new ID
        val secondResult = sut.enable()
        assertTrue(secondResult.isSuccess)

        coVerify(exactly = 2) { captionsRepository.enableCaptions(roomName) }
    }

    @Test
    fun `when disable with existing captionsId then delegate to call`() = runTest {
        val roomName = "test-room"
        val captionsId = "existing-captions-id"

        coEvery { captionsRepository.disableCaptions(roomName, captionsId) } returns success("")

        sut.init(callFacade, roomName, captionsId)
        val result = sut.disable()

        assertTrue(result.isSuccess)
        coVerify { captionsRepository.disableCaptions(roomName, captionsId) }
        verify { callFacade.enableCaptions(false) }
    }

    @Test
    fun `when enable then delegate to call`() = runTest {
        val roomName = "test-room"
        val captionsId = "captions-555"

        coEvery { captionsRepository.enableCaptions(roomName) } returns success(captionsId)

        sut.init(callFacade, roomName, null)
        sut.enable()

        verify { callFacade.enableCaptions(true) }
    }

}
