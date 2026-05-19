package com.vonage.android.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class IsValidUserNameTest {

    @ParameterizedTest
    @MethodSource("userNames")
    fun `should validate user name`(input: String, isValid: Boolean) {
        assertEquals(isValid, input.isValidUserName())
    }

    companion object {
        @JvmStatic
        fun userNames() = listOf(
            Arguments.of("Alice", true),
            Arguments.of("  Alice  ", true),          // leading/trailing spaces trimmed → valid
            Arguments.of("a", true),
            Arguments.of("a".repeat(MAX_USER_NAME_LENGTH), true),
            Arguments.of("a".repeat(MAX_USER_NAME_LENGTH + 1), false),
            Arguments.of("", false),
            Arguments.of("   ", false),               // whitespace-only → empty after trim
            Arguments.of(" ", false),                 // single space → empty after trim
        )
    }
}
