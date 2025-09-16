package com.vonage.android.screen.reporting

import android.net.Uri
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.LinkifyText
import com.vonage.android.screen.reporting.components.ReportIssueForm

@Composable
fun ReportIssueScreen(
    onClose: () -> Unit,
    viewModel: ReportingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actions = remember {
        ReportIssueScreenActions(
            onSend = { title, name, issue, attachment ->
                viewModel.sendReport(title, name, issue, attachment)
            },
            onPickImage = { uri ->
                viewModel.processImage(uri)
            },
            onCaptureScreenshot = { window ->
                viewModel.processScreenshot(window)
            },
            onRemoveAttachment = {
                viewModel.onRemoveAttachment()
            },
            onTitleChange = {
                viewModel.updateTitle(it)
            },
            onUsernameChange = {
                viewModel.updateUsername(it)
            },
            onDescriptionChange = {
                viewModel.updateDescription(it)
            },
            onClose = {
                onClose()
                viewModel.reset()
            },
        )
    }

    ReportIssueScreenContent(
        uiState = uiState,
        actions = actions,
    )
}

@Stable
data class ReportIssueScreenActions(
    val onSend: (String, String, String, ImageBitmap?) -> Unit = { _, _, _, _ -> },
    val onPickImage: (Uri) -> Unit = {},
    val onCaptureScreenshot: (Window) -> Unit = {},
    val onRemoveAttachment: () -> Unit = {},
    val onClose: () -> Unit = {},
    val onTitleChange: (String) -> Unit = {},
    val onUsernameChange: (String) -> Unit = {},
    val onDescriptionChange: (String) -> Unit = {},
)

@Composable
private fun ReportIssueScreenContent(
    uiState: ReportIssueScreenUiState,
    actions: ReportIssueScreenActions,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.report_title),
                style = MaterialTheme.typography.titleLarge,
            )
            IconButton(onClick = actions.onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }

        if (uiState.isError) {
            LaunchedEffect(uiState) {
                Toast.makeText(
                    context,
                    context.getString(R.string.report_error_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (uiState.isSuccess != null) {
            Text(uiState.isSuccess.message)
            LinkifyText(uiState.isSuccess.ticketUrl)
            VonageButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.report_success_button),
                enabled = !uiState.isProcessingAttachment || !uiState.isSending || !uiState.isError,
                onClick = actions.onClose,
            )
        } else {
            ReportIssueForm(
                uiState = uiState,
                actions = actions,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun ReportingContentPreview() {
    VonageVideoTheme {
        Surface {
            ReportIssueScreenContent(
                uiState = ReportIssueScreenUiState(),
                actions = ReportIssueScreenActions(),
            )
        }
    }
}
