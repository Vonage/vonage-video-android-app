package com.vonage.android.navigation

enum class Screen {
    JOIN_ROOM,
    WAITING_ROOM,
}

sealed class NavigationItem(val route: String) {
    data object JoinRoom : NavigationItem(Screen.JOIN_ROOM.name)
    data object WaitingRoom : NavigationItem(Screen.WAITING_ROOM.name)
}
