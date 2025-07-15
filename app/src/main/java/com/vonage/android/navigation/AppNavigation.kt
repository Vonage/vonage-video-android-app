package com.vonage.android.navigation

enum class Screen {
    JOIN_ROOM,
    WAITING_ROOM,
    ROOM,
    GOODBYE,
}

sealed class NavigationItem(val route: String) {
    data object JoinRoom : NavigationItem(Screen.JOIN_ROOM.name)
    data object WaitingRoom : NavigationItem("waiting-room/{roomName}") {
        fun createRoute(roomName: String) = "waiting-room/$roomName"
    }
    data object Room : NavigationItem("room/{roomName}") {
        fun createRoute(roomName: String) = "room/$roomName"
    }
    data object GoodbyeRoom : NavigationItem(Screen.GOODBYE.name)
}
