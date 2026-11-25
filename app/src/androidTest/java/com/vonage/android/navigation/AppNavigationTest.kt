package com.vonage.android.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.navigation.AppRoute.Goodbye
import com.vonage.android.navigation.AppRoute.Landing
import com.vonage.android.navigation.AppRoute.Meeting
import com.vonage.android.navigation.AppRoute.Waiting
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.reflect.KClass

/**
 * Guards against finished calls staying alive in the back stack: every screen left in the back
 * stack keeps its ViewModels, and the Meeting ViewModel holds the call, session and camera.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createComposeRule()

    private lateinit var navController: NavHostController

    @Before
    fun setup() {
        hiltRule.inject()
        compose.setContent {
            VonageVideoTheme {
                navController = rememberNavController()
                NavHost(navController = navController, startDestination = Landing) {
                    composable<Landing> { Box {} }
                    composable<Waiting> { Box {} }
                    composable<Meeting> { Box {} }
                    composable<Goodbye> { Box {} }
                }
            }
        }
    }

    @Test
    fun given_meeting_ended_THEN_goodbye_replaces_waiting_and_meeting() {
        joinMeeting()

        navigate { navigateToGoodbye(ROOM) }

        assertBackStack(Landing::class, Goodbye::class)
    }

    @Test
    fun given_repeated_rejoins_from_goodbye_THEN_back_stack_does_not_grow() {
        repeat(REJOINS) {
            joinMeeting()
            navigate { navigateToGoodbye(ROOM) }
            navigate { navigateToWaitingFromGoodbye(ROOM) }
        }

        assertBackStack(Landing::class, Waiting::class)
    }

    private fun joinMeeting() {
        if (navController.currentDestination?.hasRoute(Waiting::class) != true) {
            navigate { navigate(Waiting(ROOM)) }
        }
        navigate { navigate(Meeting(ROOM)) }
    }

    private fun navigate(action: NavHostController.() -> Unit) {
        compose.runOnIdle { navController.action() }
        compose.waitForIdle()
    }

    private fun assertBackStack(vararg expected: KClass<out AppRoute>) {
        val actual = compose.runOnIdle {
            navController.currentBackStack.value
                .filter { entry -> entry.destination !is NavGraph }
                .map { entry -> ROUTES.first { entry.destination.hasRoute(it) } }
        }
        assertEquals(expected.toList(), actual)
    }

    private companion object {
        const val ROOM = "room"
        const val REJOINS = 3
        val ROUTES = listOf(Landing::class, Waiting::class, Meeting::class, Goodbye::class)
    }
}
