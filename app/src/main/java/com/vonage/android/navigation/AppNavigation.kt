package com.vonage.android.navigation

enum class Screen {
    JOIN_ROOM,
    WAITING_ROOM,
}

sealed class NavigationItem(val route: String) {
    data object JoinRoom : NavigationItem(Screen.JOIN_ROOM.name)
    data object WaitingRoom : NavigationItem("waiting-room/{apiKey}/{sessionId}/{token}") {
        fun createRoute(apiKey: String, sessionId: String, token: String) =
            "waiting-room/$apiKey/$sessionId/$token"
    }
}
