package com.vonage.android.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.R
import com.vonage.android.compose.theme.VonageVideoTheme

/**
 * @param content Optional extra controls shown under [text], e.g. an option of the confirmed action.
 */
@Composable
fun BasicAlertDialog(
    text: String,
    acceptLabel: String,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    cancelLabel: String = stringResource(R.string.generic_cancel),
    content: (@Composable () -> Unit)? = null,
) {
    AlertDialog(
        text = {
            if (content == null) {
                Text(text = text)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault)) {
                    Text(text = text)
                    content()
                }
            }
        },
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(
                onClick = onAccept,
            ) {
                Text(acceptLabel)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
            ) {
                Text(cancelLabel)
            }
        }
    )
}

@PreviewLightDark
@Composable
internal fun BasicAlertDialogPreview() {
    VonageVideoTheme {
        BasicAlertDialog(
            text = "Text alert sample",
            acceptLabel = "Okish",
            cancelLabel = "No way!",
            onAccept = { },
            onCancel = { },
        )
    }
}

@PreviewLightDark
@Composable
internal fun BasicAlertDialogWithContentPreview() {
    VonageVideoTheme {
        BasicAlertDialog(
            text = "Start transcribing this meeting?",
            acceptLabel = "Start Transcription",
            cancelLabel = "Cancel",
            onAccept = { },
            onCancel = { },
            content = { VonageCheckbox(label = "Include AI summary", checked = true, onCheckedChange = {}) },
        )
    }
}
