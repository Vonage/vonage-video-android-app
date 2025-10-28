package com.vonage.android.chat

import app.cash.turbine.test
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.signal.RawSignal
import com.vonage.android.shared.DateProvider
import com.vonage.android.shared.ForegroundChecker
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

class EnabledChatSignalPluginTest {

    private val date = mockk<Date>()

    private val chatNotifications: ChatNotifications = mockk(relaxed = true)
    private val foregroundChecker: ForegroundChecker = mockk()
    private val dateProvider: DateProvider = mockk {
        every { current() } returns date
    }
    private val sut: EnabledChatSignalPlugin = EnabledChatSignalPlugin(
        chatNotifications = chatNotifications,
        foregroundChecker = foregroundChecker,
        dateProvider = dateProvider,
    )

    @Test
    fun `should handle chat signal type`() {
        assertFalse(sut.canHandle("whatever"))
        assertTrue(sut.canHandle("chat"))
    }

    @Test
    fun `should handle signal and returns correct state`() = runTest {
        givenAppInForeground()
        sut.handleSignal(
            type = "chat",
            data = """{"participantName": "hola", "text":"message"}""",
            senderName = "vonage",
            isYou = false,
        )
        sut.output.test {
            awaitItem().let {
                assertEquals(1, (it as ChatState).unreadCount)
                assertEquals("hola", it.messages[0].participantName)
                assertEquals("message", it.messages[0].text)
            }
        }
    }

    @Test
    fun `should handle signal and returns correct state with updated unread count`() = runTest {
        givenAppInForeground()
        sut.handleSignal(
            type = "chat",
            data = """{"participantName": "hola", "text":"message"}""",
            senderName = "vonage",
            isYou = false,
        )
        sut.output.test {
            assertEquals(1, (awaitItem() as ChatState).unreadCount)
            sut.handleSignal(
                type = "chat",
                data = """{"participantName": "hola", "text":"message"}""",
                senderName = "vonage",
                isYou = false,
            )
            assertEquals(2, (awaitItem() as ChatState).unreadCount)
        }
    }

    @Test
    fun `should handle signal and returns correct state with not updated unread count`() = runTest {
        givenAppInForeground()
        sut.listenUnread(false)
        sut.handleSignal(
            type = "chat",
            data = """{"participantName": "hola", "text":"message"}""",
            senderName = "vonage",
            isYou = false,
        )
        sut.output.test {
            assertEquals(0, (awaitItem() as ChatState).unreadCount)
            sut.handleSignal(
                type = "chat",
                data = """{"participantName": "hola", "text":"message"}""",
                senderName = "vonage",
                isYou = false,
            )
            assertEquals(0, (awaitItem() as ChatState).unreadCount)
        }
        verify(exactly = 1) { chatNotifications.cancelNotification() }
    }

    @Test
    fun `should ignore signal when malformed`() = runTest {
        givenAppInForeground()
        sut.handleSignal(
            type = "chat",
            data = """{"other": "hola", "thing":"message"}""",
            senderName = "vonage",
            isYou = false,
        )
        sut.output.test {
            awaitItem() // initial state
            expectNoEvents()
        }
    }

    @Test
    fun `should show notification when app in background`() = runTest {
        givenAppInBackground()
        sut.handleSignal(
            type = "chat",
            data = """{"participantName": "hola", "text":"message"}""",
            senderName = "vonage",
            isYou = false,
        )
        verify { chatNotifications.showChatNotification(any()) }
    }

    @Test
    fun `should send signal`() = runTest {
        val signal = sut.sendSignal("sender name", "any kind of message")

        assertEquals(
            RawSignal(
                SignalType.CHAT.signal,
                """{"participantName":"sender name","text":"any kind of message"}"""
            ), signal
        )
    }

    private fun givenAppInForeground() {
        every { foregroundChecker.isInBackground() } returns false
    }

    private fun givenAppInBackground() {
        every { foregroundChecker.isInBackground() } returns true
    }
}
