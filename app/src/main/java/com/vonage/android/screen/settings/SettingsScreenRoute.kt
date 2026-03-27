package com.vonage.android.screen.settings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.BuildConfig
import com.vonage.android.settings.SettingsScreenActions
import com.vonage.android.settings.R as SettingsR
import com.vonage.android.settings.ui.SettingsScreen
import kotlinx.coroutines.flow.collectLatest

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
    val context = LocalContext.current
    val logsSentMessage = stringResource(SettingsR.string.settings_logs_send_success)
    val noLogsMessage = stringResource(SettingsR.string.settings_logs_send_empty)
    val logsFailedMessage = stringResource(SettingsR.string.settings_logs_send_failed)

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            val message = when (event) {
                SettingsViewEvent.LogsSent -> logsSentMessage
                SettingsViewEvent.NoLogsAvailable -> noLogsMessage
                SettingsViewEvent.LogsSendFailed -> logsFailedMessage
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

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
            onLogsToggle = viewModel::toggleLogs,
            onSendLogsClick = viewModel::sendLogs,
            onDismiss = onDismiss,
        ),
    )
}
