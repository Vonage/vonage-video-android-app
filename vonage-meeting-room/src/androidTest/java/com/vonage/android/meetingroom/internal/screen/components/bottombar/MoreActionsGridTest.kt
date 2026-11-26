package com.vonage.android.meetingroom.internal.screen.components.bottombar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Warning
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoreActionsGridTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun given_two_custom_actions_THEN_both_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                MoreActionsGrid(
                    actions = persistentListOf(
                        customAction("Host action"),
                        customAction("Report"),
                    ),
                )
            }
        }

        compose.onNodeWithText("Host action").assertIsDisplayed()
        compose.onNodeWithText("Report").assertIsDisplayed()
    }

    private fun customAction(label: String) = BottomBarAction(
        type = BottomBarActionType.CUSTOM,
        icon = VividIcons.Solid.Warning,
        label = label,
        onClick = {},
    )
}
