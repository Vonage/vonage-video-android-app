package com.vonage.android.screen.reporting

import android.net.Uri
import android.view.Window
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.LinkifyText
import com.vonage.android.util.pip.findActivity

@Composable
fun ReportIssueScreen(
    onClose: () -> Unit,
    viewModel: ReportingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReportingContent(
        uiState = uiState,
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

@Composable
private fun ReportingContent(
    uiState: ReportIssueScreenUiState,
    onSend: (String, String, String, ImageBitmap?) -> Unit,
    onPickImage: (Uri) -> Unit,
    onCaptureScreenshot: (Window) -> Unit,
    onRemoveAttachment: () -> Unit,
    onClose: () -> Unit,
    onTitleChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        }

        if (uiState.isError) {
            Text("Something failed, please try again")
        }

        if (uiState.isSuccess != null) {
            Text(uiState.isSuccess.message)
            LinkifyText(uiState.isSuccess.ticketUrl)
        } else {
            ReportIssueContent(
                uiState = uiState,
                onSend = onSend,
                onPickImage = onPickImage,
                onCaptureScreenshot = onCaptureScreenshot,
                onRemoveAttachment = onRemoveAttachment,
                onTitleChange = onTitleChange,
                onUsernameChange = onUsernameChange,
                onDescriptionChange = onDescriptionChange,
            )
        }
    }
}

@Composable
private fun ColumnScope.ReportIssueContent(
    uiState: ReportIssueScreenUiState,
    onSend: (String, String, String, ImageBitmap?) -> Unit,
    onPickImage: (Uri) -> Unit,
    onCaptureScreenshot: (Window) -> Unit,
    onRemoveAttachment: () -> Unit,
    onTitleChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    VonageTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.title,
        maxLength = 100,
        onValueChange = { onTitleChange(it) },
        placeholder = { Text(stringResource(R.string.report_title_placeholder)) },
        supportingText = { Text("${uiState.title.length}/100") },
        isError = uiState.isTitleValid.not(),
    )

    VonageTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.userName,
        maxLength = 100,
        onValueChange = { onUsernameChange(it) },
        placeholder = { Text(stringResource(R.string.report_name_placeholder)) },
        supportingText = { Text("${uiState.userName.length}/100") },
        isError = uiState.isUsernameValid.not(),
    )

    VonageTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.description,
        onValueChange = { onDescriptionChange(it) },
        singleLine = false,
        maxLines = 4,
        maxLength = 1000,
        placeholder = { Text(stringResource(R.string.report_issue_placeholder)) },
        supportingText = { Text("${uiState.description.length}/1000") },
        isError = uiState.isDescriptionValid.not(),
    )

    Text(
        stringResource(R.string.report_advice),
        style = MaterialTheme.typography.bodySmall
    )

    AttachmentRow(
        uiState = uiState,
        onCaptureScreenshot = onCaptureScreenshot,
        onRemoveAttachment = onRemoveAttachment,
        onPickImage = onPickImage,
    )

    VonageButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.report_send),
        enabled = !uiState.isProcessingScreenshot || !uiState.isSending || !uiState.isError,
        onClick = {
            onSend(uiState.title, uiState.userName, uiState.description, uiState.attachment)
        },
        leadingIcon = {
            if (uiState.isSending) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    )
}

@Composable
private fun ColumnScope.AttachmentRow(
    uiState: ReportIssueScreenUiState,
    onCaptureScreenshot: (Window) -> Unit,
    onRemoveAttachment: () -> Unit,
    onPickImage: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onPickImage(uri) }
        })

    Text(
        stringResource(R.string.report_add_screenshot_advice),
        style = MaterialTheme.typography.bodySmall
    )

    if (uiState.isProcessingScreenshot) {
        Row {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp)
            )
        }
    } else {
        uiState.attachment?.let { img ->
            Text(
                text = stringResource(R.string.report_screenshot_attached_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Box {
                Image(
                    modifier = Modifier.width(80.dp),
                    bitmap = img,
                    contentDescription = null,
                )
                IconButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = onRemoveAttachment,
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                    )
                }
            }
        } ?: run {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VonageOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.report_capture_screenshot),
                    onClick = { onCaptureScreenshot(context.findActivity().window) },
                )
                VonageOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.report_add_screenshot),
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun ReportingContentPreview() {
    VonageVideoTheme {
        Surface {
            ReportingContent(
                uiState = ReportIssueScreenUiState(),
                onSend = { _, _, _, _ -> },
                onPickImage = { },
                onCaptureScreenshot = { },
                onRemoveAttachment = { },
                onClose = { },
                onTitleChange = { },
                onUsernameChange = { },
                onDescriptionChange = { },
            )
        }
    }
}
