package com.vonage.android.data.network

import android.os.Build
import com.vonage.android.BuildConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VeraUserAgentTest {

    @Test
    fun `user agent has the expected format`() {
        assertEquals(
            "VeraNativeAndroid/${BuildConfig.VERSION_NAME} android ${Build.VERSION.RELEASE}",
            veraUserAgent(),
        )
    }

    @Test
    fun `user agent starts with VeraNativeAndroid`() {
        assertTrue(veraUserAgent().startsWith("VeraNativeAndroid/"))
    }
}
