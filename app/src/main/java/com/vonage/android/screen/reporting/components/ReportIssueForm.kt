package com.vonage.android.screen.reporting.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.screen.reporting.ReportIssueScreenActions
import com.vonage.android.screen.reporting.ReportIssueScreenUiState
import com.vonage.android.screen.reporting.ReportingViewModel
import com.vonage.android.util.pip.findActivity

@Composable
fun ReportIssueForm(
    uiState: ReportIssueScreenUiState,
    actions: ReportIssueScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        VonageTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.title,
            maxLength = ReportingViewModel.TITLE_MAX_LENGTH,
            onValueChange = { actions.onTitleChange(it) },
            placeholder = { Text(stringResource(R.string.report_title_placeholder)) },
            supportingText = { Text("${uiState.title.length}/${ReportingViewModel.TITLE_MAX_LENGTH}") },
            isError = uiState.isTitleValid.not(),
        )

        VonageTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.userName,
            maxLength = ReportingViewModel.NAME_MAX_LENGTH,
            onValueChange = { actions.onUsernameChange(it) },
            placeholder = { Text(stringResource(R.string.report_name_placeholder)) },
            supportingText = { Text("${uiState.userName.length}/${ReportingViewModel.NAME_MAX_LENGTH}") },
            isError = uiState.isUsernameValid.not(),
        )

        VonageTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.description,
            onValueChange = { actions.onDescriptionChange(it) },
            singleLine = false,
            maxLines = 4,
            maxLength = ReportingViewModel.DESCRIPTION_MAX_LENGTH,
            placeholder = { Text(stringResource(R.string.report_issue_placeholder)) },
            supportingText = { Text("${uiState.description.length}/${ReportingViewModel.DESCRIPTION_MAX_LENGTH}") },
            isError = uiState.isDescriptionValid.not(),
        )

        Text(
            stringResource(R.string.report_advice),
            style = MaterialTheme.typography.bodySmall
        )

        AttachmentRow(
            uiState = uiState,
            onCaptureScreenshot = actions.onCaptureScreenshot,
            onRemoveAttachment = actions.onRemoveAttachment,
            onPickImage = actions.onPickImage,
        )

        VonageButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.report_send),
            enabled = !uiState.isProcessingAttachment && !uiState.isSending && !uiState.isError,
            onClick = {
                actions.onSend(uiState.title, uiState.userName, uiState.description, uiState.attachment)
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

    if (uiState.isProcessingAttachment) {
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
                        imageVector = VividIcons.Line.Close,
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
