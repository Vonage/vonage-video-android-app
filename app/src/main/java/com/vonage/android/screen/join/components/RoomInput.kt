package com.vonage.android.screen.join.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.join.JoinMeetingRoomActions
import com.vonage.android.screen.join.LandingScreenTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.ROOM_INPUT_ERROR_TAG
import com.vonage.android.screen.join.LandingScreenTestTags.ROOM_INPUT_TAG

@Composable
internal fun RoomInput(
    roomName: String,
    isRoomNameWrong: Boolean,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        VonageTextField(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .testTag(ROOM_INPUT_TAG),
            value = roomName,
            onValueChange = actions.onRoomNameChange,
            isError = isRoomNameWrong,
            placeholder = { Text(text = stringResource(R.string.landing_enter_room_name)) },
            label = { Text(text = stringResource(R.string.landing_enter_room_name_label)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            supportingText = {
                if (isRoomNameWrong) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = VonageVideoTheme.dimens.paddingSmall)
                            .testTag(ROOM_INPUT_ERROR_TAG),
                        text = stringResource(R.string.landing_room_name_error_message),
                        color = VonageVideoTheme.colors.error,
                    )
                }
            }
        )

        VonageOutlinedButton(
            modifier = Modifier
                .padding(vertical = VonageVideoTheme.dimens.paddingSmall)
                .fillMaxWidth()
                .height(VonageVideoTheme.dimens.buttonHeight)
                .testTag(JOIN_BUTTON_TAG),
            onClick = { actions.onJoinRoomClick(roomName) },
            enabled = isRoomNameWrong.not() && roomName.isNotEmpty(),
            text = stringResource(R.string.landing_join),
        )
    }
}

@PreviewLightDark
@Composable
internal fun RoomInputPreview() {
    VonageVideoTheme {
        Surface(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
        ) {
            RoomInput(
                roomName = "room-name",
                isRoomNameWrong = false,
                actions = JoinMeetingRoomActions(),
            )
        }
    }
}
