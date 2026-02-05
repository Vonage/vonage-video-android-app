package com.vonage.android.captions.ui

import androidx.compose.runtime.Composable
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.ClosedCaptioning
import com.vonage.android.compose.vivid.icons.solid.ClosedCaptioningOff

@Composable
fun captionsAction(
    onEnableCaptions: () -> Unit,
    onDisableCaptions: () -> Unit,
    enableCaptionsLabel: String,
    disableCaptionsLabel: String,
    captionsUiState: CaptionsUiState,
): BottomBarAction? =
    BottomBarAction(
        type = BottomBarActionType.CAPTIONS,
        icon = when (captionsUiState) {
            CaptionsUiState.IDLE,
            CaptionsUiState.DISABLING -> VividIcons.Solid.ClosedCaptioning

            CaptionsUiState.ENABLING,
            CaptionsUiState.ENABLED -> VividIcons.Solid.ClosedCaptioningOff
        },
        label = when (captionsUiState) {
            CaptionsUiState.IDLE,
            CaptionsUiState.ENABLING,
            CaptionsUiState.DISABLING -> enableCaptionsLabel

            CaptionsUiState.ENABLED -> disableCaptionsLabel
        },
        isSelected = when (captionsUiState) {
            CaptionsUiState.IDLE,
            CaptionsUiState.DISABLING -> false

            CaptionsUiState.ENABLING,
            CaptionsUiState.ENABLED -> true
        },
        onClick = {
            when (captionsUiState) {
                CaptionsUiState.IDLE -> onEnableCaptions()
                CaptionsUiState.ENABLED -> onDisableCaptions()
                CaptionsUiState.ENABLING,
                CaptionsUiState.DISABLING -> null
            }
        },
    )
