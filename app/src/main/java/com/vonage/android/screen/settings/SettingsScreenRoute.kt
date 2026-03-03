package com.vonage.android.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.BuildConfig
import com.vonage.android.settings.ui.SettingsScreen
import com.vonage.android.settings.ui.SettingsScreenViewModel
import com.vonage.android.settings.ui.SettingsScreenViewModelFactory

@Composable
fun SettingsScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = hiltViewModel<SettingsScreenViewModel, SettingsScreenViewModelFactory> { factory ->
        factory.create(
            appVersion = BuildConfig.VERSION_NAME,
            sdkVersion = BuildConfig.OPENTOK_SDK_VERSION,
        )
    },
    onDismiss: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onSenderStatsTrackToggle = viewModel::toggleSenderStatsTrack,
        onDismiss = onDismiss,
    )
}
