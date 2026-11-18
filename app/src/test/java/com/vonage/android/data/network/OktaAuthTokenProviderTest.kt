package com.vonage.android.data.network

import com.vonage.android.okta.VonageOktaAuth
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OktaAuthTokenProviderTest {

    private val oktaAuth: VonageOktaAuth = mockk()
    private val sut = OktaAuthTokenProvider(oktaAuth)

    @Test
    fun `returns the Okta token when authenticated`() {
        coEvery { oktaAuth.currentToken() } returns "access-token"

        assertEquals("access-token", sut.currentToken())
    }

    @Test
    fun `returns null when not authenticated`() {
        coEvery { oktaAuth.currentToken() } returns null

        assertNull(sut.currentToken())
    }
}
