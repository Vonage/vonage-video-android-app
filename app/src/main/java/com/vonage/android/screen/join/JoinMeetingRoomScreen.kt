package com.vonage.android.screen.join

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.icons.PlusIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.OrSeparator
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.CREATE_ROOM_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.JOIN_BUTTON_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_ERROR_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.ROOM_INPUT_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.SUBTITLE_TAG
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.TITLE_TAG

@Composable
fun JoinMeetingRoomScreen(
    uiState: JoinMeetingRoomUiState,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
    navigateToRoom: (JoinMeetingRoomRouteParams) -> Unit = {},
) {
    val context = LocalContext.current
    val errorMessage = stringResource(R.string.landing_room_generic_error_message)

    LaunchedEffect(uiState.isError, uiState.isSuccess) {
        if (uiState.isError) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
        if (uiState.isSuccess) {
            navigateToRoom(JoinMeetingRoomRouteParams(roomName = uiState.roomName))
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopBanner() },
    ) { contentPadding ->
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = spacedBy(VonageVideoTheme.dimens.spaceXXLarge, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxSize()
                .background(VonageVideoTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .consumeWindowInsets(contentPadding)
                .padding(horizontal = VonageVideoTheme.dimens.paddingLarge),
        ) {
            JoinMeetingRoomHeader(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .widthIn(0.dp, 380.dp)
            )
            JoinMeetingRoomContent(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .widthIn(0.dp, 320.dp),
                roomName = uiState.roomName,
                isRoomNameWrong = uiState.isRoomNameWrong,
                actions = actions,
            )
        }
    }
}

@Composable
private fun JoinMeetingRoomHeader(
    modifier: Modifier = Modifier,
) {
    val gradientColors = listOf(
        VonageVideoTheme.colors.primary,
        VonageVideoTheme.colors.secondary,
    )
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val brush = remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height * offset
                return LinearGradientShader(
                    colors = gradientColors,
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, heightOffset + size.height),
                    tileMode = TileMode.Mirror,
                )
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        Text(
            modifier = Modifier.testTag(TITLE_TAG),
            text = stringResource(R.string.landing_title),
            style = VonageVideoTheme.typography.headline
                .copy(brush = brush),
            color = VonageVideoTheme.colors.textSecondary,
            textAlign = TextAlign.Start,
        )

        Text(
            modifier = Modifier.testTag(SUBTITLE_TAG),
            text = stringResource(R.string.landing_subtitle),
            style = VonageVideoTheme.typography.heading2,
            color = VonageVideoTheme.colors.textTertiary,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun JoinMeetingRoomContent(
    roomName: String,
    isRoomNameWrong: Boolean,
    actions: JoinMeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceXLarge),
    ) {
        VonageButton(
            text = stringResource(R.string.landing_create_room),
            modifier = Modifier
                .testTag(CREATE_ROOM_BUTTON_TAG),
            onClick = actions.onCreateRoomClick,
            leadingIcon = { PlusIcon() },
        )
        OrSeparator()
        RoomInput(
            roomName = roomName,
            isRoomNameWrong = isRoomNameWrong,
            actions = actions,
        )
    }
}

@Composable
private fun RoomInput(
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
                .testTag(JOIN_BUTTON_TAG),
            onClick = { actions.onJoinRoomClick(roomName) },
            enabled = isRoomNameWrong.not() && roomName.isNotEmpty(),
            text = stringResource(R.string.landing_join),
        )
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun PreviewJoinRoomScreen() {
    VonageVideoTheme {
        JoinMeetingRoomScreen(
            uiState = JoinMeetingRoomUiState(
                roomName = "hithere",
                isRoomNameWrong = false,
            ),
            actions = JoinMeetingRoomActions(
                onJoinRoomClick = {},
                onCreateRoomClick = {},
                onRoomNameChange = {},
            ),
        )
    }
}
