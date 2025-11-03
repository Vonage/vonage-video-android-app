package com.vonage.android.integration.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider

/**
 * https://stackoverflow.com/questions/68267861/add-intent-extras-in-compose-ui-test
 * Uses a [ComposeTestRule] created via [createEmptyComposeRule] that allows setup before the activity
 * is launched via [onBefore].
 */
inline fun <reified A : Activity> launchApp(
    onBefore: () -> Unit = {},
    intentFactory: (Context) -> Intent = {
        Intent(
            ApplicationProvider.getApplicationContext(), A::class.java
        )
    },
): ActivityScenario<A> {
    onBefore()
    val context = ApplicationProvider.getApplicationContext<Context>()
    return ActivityScenario.launch(intentFactory(context))
}
