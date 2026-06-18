package com.vonage.android.settings

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class NoOpCallSettingsStorageTest {

    private val sut = NoOpCallSettingsStorage()

    @Test
    fun `load returns default PersistedCallSettings`() = runTest {
        assertEquals(PersistedCallSettings(), sut.load())
    }

    @Test
    fun `save completes without error`() = runTest {
        sut.save(PersistedCallSettings())
    }
}
