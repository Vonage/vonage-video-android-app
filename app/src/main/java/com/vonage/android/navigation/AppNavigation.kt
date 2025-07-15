package com.vonage.android.navigation

enum class Screen {
    JOIN_ROOM,
    WAITING_ROOM,
    ROOM,
}

sealed class NavigationItem(val route: String) {
    data object JoinRoom : NavigationItem(Screen.JOIN_ROOM.name)
    data object WaitingRoom : NavigationItem("waiting-room/{roomName}/{apiKey}/{sessionId}/{token}") {
        fun createRoute(roomName: String, apiKey: String, sessionId: String, token: String) =
            "waiting-room/$roomName/$apiKey/$sessionId/$token"
    }
    data object Room : NavigationItem("room/{roomName}") {
        fun createRoute(roomName: String) = "room/$roomName"
    }
}
