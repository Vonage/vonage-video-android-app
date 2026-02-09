package com.vonage.android.captions.ui

import androidx.compose.runtime.Composable
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.compose.components.bottombar.BottomBarAction

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
@Composable
fun captionsAction(
    onEnableCaptions: () -> Unit,
    onDisableCaptions: () -> Unit,
    enableCaptionsLabel: String,
    disableCaptionsLabel: String,
    captionsUiState: CaptionsUiState,
): BottomBarAction? = null
