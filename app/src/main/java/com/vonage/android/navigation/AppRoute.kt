package com.vonage.android.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    object Landing : AppRoute

    @Serializable
    data class Waiting(val roomName: String) : AppRoute

    @Serializable
    data class Meeting(val roomName: String) : AppRoute

    @Serializable
    data class Goodbye(val roomName: String) : AppRoute
}
