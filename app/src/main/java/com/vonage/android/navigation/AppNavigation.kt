package com.vonage.android.navigation

import androidx.navigation.NavController
import com.vonage.android.navigation.AppRoute.Goodbye
import com.vonage.android.navigation.AppRoute.Landing
import com.vonage.android.navigation.AppRoute.Waiting

/**
 * Leaves the call for the goodbye screen. Waiting and Meeting are popped so their ViewModels are
 * cleared and release the call, session, publishers and camera.
 */
internal fun NavController.navigateToGoodbye(roomName: String) {
    navigate(Goodbye(roomName = roomName)) { popUpTo<Landing>() }
}

/**
 * Opens the waiting room from the goodbye screen (re-enter or back). Goodbye is popped so repeated
 * calls never stack screens up.
 */
internal fun NavController.navigateToWaitingFromGoodbye(roomName: String) {
    navigate(Waiting(roomName = roomName)) { popUpTo<Landing>() }
}
