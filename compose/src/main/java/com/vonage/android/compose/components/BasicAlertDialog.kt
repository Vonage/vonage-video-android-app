package com.vonage.android.compose.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.R
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun BasicAlertDialog(
    text: String,
    acceptLabel: String,
    cancelLabel: String = stringResource(R.string.generic_cancel),
    onAccept: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(text = text)
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
