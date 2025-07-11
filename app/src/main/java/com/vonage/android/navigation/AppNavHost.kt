package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vonage.android.screen.join.JoinMeetingRoomRoute
import com.vonage.android.screen.waiting.WaitingRoomRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationItem.JoinRoom.route,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.JoinRoom.route) {
            JoinMeetingRoomRoute(
                navigateToRoom = { params ->
                    navController.navigate(
                        NavigationItem.WaitingRoom.createRoute(
                            apiKey = params.apiKey,
                            sessionId = params.sessionId,
                            token = params.token,
                        )
                    )
                },
            )
        }
        composable(
            route = NavigationItem.WaitingRoom.route,
            arguments = listOf(
                navArgument("apiKey") { type = NavType.StringType },
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val apikey = backStackEntry.arguments?.getString("apiKey")
            val sessionId = backStackEntry.arguments?.getString("sessionId")
            val token = backStackEntry.arguments?.getString("token")
            WaitingRoomRoute(
                apiKey = apikey.toString(),
                sessionId = sessionId.toString(),
                token = token.toString(),
            )
        }
    }
}
