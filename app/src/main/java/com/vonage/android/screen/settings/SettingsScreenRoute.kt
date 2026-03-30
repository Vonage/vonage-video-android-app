package com.vonage.android.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.BuildConfig
import com.vonage.android.settings.SettingsScreenActions
import com.vonage.android.settings.ui.SettingsScreen

@Composable
fun SettingsScreenRoute(
    viewModel: SettingsScreenViewModel = hiltViewModel<SettingsScreenViewModel, SettingsScreenViewModelFactory> { factory ->
        factory.create(
            appVersion = BuildConfig.VERSION_NAME,
            sdkVersion = BuildConfig.OPENTOK_SDK_VERSION,
        )
    },
    onDismiss: () -> Unit = {},
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        actions = SettingsScreenActions(
            onSenderStatsTrackToggle = viewModel::toggleSenderStatsTrack,
            onOpusDtxToggle = viewModel::toggleOpusDtx,
            onPublisherAudioFallbackToggle = viewModel::togglePublisherAudioFallback,
            onSubscriberAudioFallbackToggle = viewModel::toggleSubscriberAudioFallback,
            onVideoBitrateConfigChange = viewModel::updateVideoBitrateConfig,
            onDegradationPreferenceChange = viewModel::updateDegradationPreference,
            onFrameRateChange = viewModel::updateCaptureFrameRate,
            onResolutionChange = viewModel::updateCaptureResolution,
            onPreferredVideoCodecOrderChange = viewModel::updatePreferredVideoCodecOrder,
            onAudioBitrateChange = viewModel::updateAudioBitrate,
            onDismiss = onDismiss,
        ),
    )
}
