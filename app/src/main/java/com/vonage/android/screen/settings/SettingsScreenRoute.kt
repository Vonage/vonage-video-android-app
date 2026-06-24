package com.vonage.android.screen.settings

import android.content.Intent
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
    val noLogsToShareMessage = stringResource(SettingsR.string.settings_logs_share_empty)

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                SettingsViewEvent.LogsSent ->
                    Toast.makeText(context, logsSentMessage, Toast.LENGTH_SHORT).show()
                SettingsViewEvent.NoLogsAvailable ->
                    Toast.makeText(context, noLogsMessage, Toast.LENGTH_SHORT).show()
                SettingsViewEvent.LogsSendFailed ->
                    Toast.makeText(context, logsFailedMessage, Toast.LENGTH_SHORT).show()
                SettingsViewEvent.NoLogsToShare ->
                    Toast.makeText(context, noLogsToShareMessage, Toast.LENGTH_SHORT).show()
                is SettingsViewEvent.ShareLogs -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, event.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, context.getString(SettingsR.string.settings_share_logs_chooser_title)),
                    )
                }
            }
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
            onLogLevelChange = viewModel::updateLogLevel,
            onSendLogsClick = viewModel::sendLogs,
            onShareLogsClick = viewModel::shareLogs,
            onDismiss = onDismiss,
        ),
    )
}
