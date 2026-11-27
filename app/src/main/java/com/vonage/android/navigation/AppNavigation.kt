package com.vonage.android.navigation

import androidx.navigation.NavController
import com.vonage.android.navigation.AppRoute.Goodbye
import com.vonage.android.navigation.AppRoute.Landing
import com.vonage.android.navigation.AppRoute.Waiting

// Pops Waiting and Meeting so their ViewModels release the call.
internal fun NavController.navigateToGoodbye(roomName: String) {
    navigate(Goodbye(roomName = roomName)) { popUpTo<Landing>() }
}

// Pops Goodbye so repeated calls never stack screens up.
internal fun NavController.navigateToWaitingFromGoodbye(roomName: String) {
    navigate(Waiting(roomName = roomName)) { popUpTo<Landing>() }
}
