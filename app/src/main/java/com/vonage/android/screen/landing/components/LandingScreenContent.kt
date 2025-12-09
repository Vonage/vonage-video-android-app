package com.vonage.android.screen.landing.components

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.icons.PlusIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.OrSeparator
import com.vonage.android.screen.landing.JoinMeetingRoomActions
import com.vonage.android.screen.landing.LandingScreenTestTags.CREATE_ROOM_BUTTON_TAG

@Composable
internal fun LandingScreenContent(
    roomName: String,
    isRoomNameWrong: Boolean,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceLarge),
    ) {
        Text(
            text = stringResource(R.string.landing_create_room_title),
            style = VonageVideoTheme.typography.heading4,
        )
        VonageButton(
            text = stringResource(R.string.landing_create_room),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(CREATE_ROOM_BUTTON_TAG),
            onClick = actions.onCreateRoomClick,
            leadingIcon = { PlusIcon() },
        )
        OrSeparator()
        Text(
            text = stringResource(R.string.landing_enter_room_name_title),
            style = VonageVideoTheme.typography.heading4,
        )
        RoomInput(
            roomName = roomName,
            isRoomNameWrong = isRoomNameWrong,
            actions = actions,
        )
    }
}
