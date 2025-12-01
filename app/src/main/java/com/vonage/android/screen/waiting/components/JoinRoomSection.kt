package com.vonage.android.screen.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.waiting.WaitingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.PREPARE_TO_JOIN_TEXT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.ROOM_NAME_TEXT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.USER_NAME_INPUT_TAG
import com.vonage.android.screen.waiting.WaitingRoomTestTags.WHATS_YOU_NAME_TEXT_TAG

@Composable
fun JoinRoomSection(
    roomName: String,
    username: String,
    onUsernameChange: (String) -> Unit,
    onJoinRoom: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingSmall,
            ),
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceLarge),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier.testTag(WHATS_YOU_NAME_TEXT_TAG),
            text = stringResource(R.string.waiting_room_whats_your_name),
            style = VonageVideoTheme.typography.heading4,
            color = VonageVideoTheme.colors.textSecondary,
        )

        VonageTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(USER_NAME_INPUT_TAG),
            placeholder = {
                Text(
                    text = stringResource(R.string.waiting_room_name_input_label),
                    color = VonageVideoTheme.colors.textDisabled,
                )
            },
            value = username,
            label = { Text(text = stringResource(R.string.waiting_room_name_input_label)) },
            onValueChange = onUsernameChange,
        )

        HorizontalDivider()

        Text(
            modifier = Modifier.testTag(PREPARE_TO_JOIN_TEXT_TAG),
            text = stringResource(R.string.waiting_room_prepare_to_join),
            style = VonageVideoTheme.typography.heading4,
            color = VonageVideoTheme.colors.textSecondary,
        )

        Text(
            modifier = Modifier.testTag(ROOM_NAME_TEXT_TAG),
            text = roomName,
            style = VonageVideoTheme.typography.heading4,
            color = VonageVideoTheme.colors.textTertiary,
        )

        VonageButton(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(JOIN_BUTTON_TAG),
            text = stringResource(R.string.waiting_room_join),
            onClick = { onJoinRoom(username) },
            enabled = username.isNotEmpty(),
        )
    }
}

@PreviewLightDark
@Composable
internal fun JoinRoomSectionPreview() {
    VonageVideoTheme {
        JoinRoomSection(
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            roomName = "your-name-is-a-room",
            username = "Slim shady",
            onUsernameChange = { },
            onJoinRoom = { },
        )
    }
}
