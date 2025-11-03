package com.vonage.android.integration

import android.Manifest
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.vonage.android.MainActivity
import com.vonage.android.integration.helper.launchApp
import com.vonage.android.integration.robots.landing
import com.vonage.android.integration.robots.waiting
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class JoinRoomIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createEmptyComposeRule()

    @get:Rule(order = 2)
    var runtimePermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.BLUETOOTH_CONNECT,
        )

    @Before
    fun setUp() {
        launchApp<MainActivity>()
    }

    @Test
    fun shouldAllowToJoinRoom() {
        landing(compose) {
            enterRoomName("hithere")
            join()
        }
        waiting(compose) {
            checkRoomName("hithere")
            enterUserName("i am a test user")
            join()
        }
    }

    @Test
    fun shouldAllowToCreateRoom() {
        landing(compose) {
            clickCreateRoom()
        }
        waiting(compose) {
            enterUserName("my name is test")
            join()
        }
    }

    @Test
    fun shouldAllowToDisableVideo() {
        landing(compose) {
            enterRoomName("hithere")
            join()
        }
        waiting(compose) {
            checkRoomName("hithere")
            enterUserName("my name is test")
            disableCamera()
            checkInitials("MT")
            join()
        }
    }

    @Test
    fun shouldAllowToDisableAudio() {
        landing(compose) {
            enterRoomName("hithere")
            join()
        }
        waiting(compose) {
            checkRoomName("hithere")
            enterUserName("my name is test")
            disableAudio()
            join()
        }
    }
}
