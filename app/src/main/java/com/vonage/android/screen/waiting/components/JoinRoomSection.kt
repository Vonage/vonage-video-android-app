package com.vonage.android.screen.waiting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.icons.PersonIcon
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
            .padding(horizontal = 32.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.testTag(PREPARE_TO_JOIN_TEXT_TAG),
            text = stringResource(R.string.waiting_room_prepare_to_join),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.testTag(ROOM_NAME_TEXT_TAG),
            text = roomName,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.testTag(WHATS_YOU_NAME_TEXT_TAG),
            text = stringResource(R.string.waiting_room_whats_your_name),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            textAlign = TextAlign.Center,
        )

        VonageTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(USER_NAME_INPUT_TAG),
            value = username,
            onValueChange = onUsernameChange,
            leadingIcon = {
                PersonIcon()
            },
        )

        VonageButton(
            modifier = Modifier.testTag(JOIN_BUTTON_TAG),
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
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            roomName = "your-name-is-a-room",
            username = "Slim shady",
            onUsernameChange = { },
            onJoinRoom = { },
        )
    }
}