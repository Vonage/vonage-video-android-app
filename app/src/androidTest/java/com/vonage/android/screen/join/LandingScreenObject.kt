package com.vonage.android.screen.join

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.vonage.android.screen.join.LandingScreenTestTags.CREATE_ROOM_BUTTON_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.ROOM_INPUT_ERROR_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.ROOM_INPUT_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.SUBTITLE_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.TITLE_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.VONAGE_ICON_TAG

class LandingScreenObject(
    compose: SemanticsNodeInteractionsProvider
) {
    val logo = compose.onNodeWithTag(VONAGE_ICON_TAG)
    val title = compose.onNodeWithTag(TITLE_TAG)
    val subTitle = compose.onNodeWithTag(SUBTITLE_TAG)
    val createRoomButton = compose.onNodeWithTag(CREATE_ROOM_BUTTON_TAG)
    val joinButton = compose.onNodeWithTag(JOIN_BUTTON_TAG)
    val roomInput = compose.onNodeWithTag(ROOM_INPUT_TAG)
    val roomInputLabel = compose.onNodeWithTag(ROOM_INPUT_ERROR_TAG, useUnmergedTree = true)
}
