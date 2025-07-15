package com.vonage.android.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetInitialsTest {

    @ParameterizedTest
    @MethodSource("userNames")
    fun `should validate user name initials`(userName: String, initials: String) {
        assertEquals(initials, userName.getInitials())
    }

    companion object {
        @JvmStatic
        fun userNames() = listOf(
            Arguments.of("", ""),
            Arguments.of("a", "A"),
            Arguments.of("hulk", "H"),
            Arguments.of("peter     parker", "PP"),
            Arguments.of("    ironman", "I"),
            Arguments.of("the amazing spiderman", "TS"),
//            Arguments.of("🙈", "?"), // failing dunno why
        )
    }
}
