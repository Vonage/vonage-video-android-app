package com.vonage.android.chat

import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.shared.ForegroundChecker
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnabledChatSignalPluginTest {

    private val chatNotifications: ChatNotifications = mockk()
    private val foregroundChecker: ForegroundChecker = mockk()
    private val sut: EnabledChatSignalPlugin = EnabledChatSignalPlugin(
        chatNotifications = chatNotifications,
        foregroundChecker = foregroundChecker,
    )

    @Test
    fun `should handle chat signal type`() {
        assertFalse(sut.canHandle("whatever"))

        assertTrue(sut.canHandle("chat"))
    }

    @Test
    fun `should handle signal and returns correct state`() {
        every { foregroundChecker.isInForeground() } returns true
        val result = sut.handleSignal(
            type = "chat",
            data = """{"participantName": "hola", "text":"message"}""",
            senderName = "vonage",
            isYou = false,
            callback = {},
        )
        assertNotNull(result)
        result.let { r ->
            assertEquals(1, (r as ChatState).unreadCount)
            assertEquals("hola", r.messages[0].participantName)
            assertEquals("message", r.messages[0].text)
        }
    }

    @Test
    fun `should ignore signal when malformed`() {
        every { foregroundChecker.isInForeground() } returns true
        val result = sut.handleSignal(
            type = "chat",
            data = """{"other": "hola", "thing":"message"}""",
            senderName = "vonage",
            isYou = false,
            callback = {},
        )
        assertNull(result)
    }
}
