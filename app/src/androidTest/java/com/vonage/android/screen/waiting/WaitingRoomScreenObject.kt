package com.vonage.android.screen.waiting

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.vonage.android.elements.avatarInitials
import com.vonage.android.screen.waiting.WaitingRoomTestTags.CAMERA_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.MIC_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.PREPARE_TO_JOIN_TEXT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.ROOM_NAME_TEXT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_INITIALS_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_NAME_INPUT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.VOLUME_INDICATOR_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.WHATS_YOU_NAME_TEXT_TAG

class WaitingRoomScreenObject(
    compose: SemanticsNodeInteractionsProvider
) {
    val joinButton = compose.onNodeWithTag(JOIN_BUTTON_TAG)
    val prepareToJoinText = compose.onNodeWithTag(PREPARE_TO_JOIN_TEXT_TAG)
    val roomNameText = compose.onNodeWithTag(ROOM_NAME_TEXT_TAG)
    val whatsYourNameText = compose.onNodeWithTag(WHATS_YOU_NAME_TEXT_TAG)
    val userNameInput = compose.onNodeWithTag(USER_NAME_INPUT_TAG, useUnmergedTree = true)
    val cameraButtonEnabled = compose.onNodeWithTag("$CAMERA_BUTTON_TAG-on")
    val cameraButtonDisabled = compose.onNodeWithTag("$CAMERA_BUTTON_TAG-off")
    val micButtonEnabled = compose.onNodeWithTag("$MIC_BUTTON_TAG-on")
    val micButtonDisabled = compose.onNodeWithTag("$MIC_BUTTON_TAG-off")
    val volumeIndicator = compose.onNodeWithTag(VOLUME_INDICATOR_TAG)
    val initials = compose.avatarInitials(USER_INITIALS_TAG)
}
