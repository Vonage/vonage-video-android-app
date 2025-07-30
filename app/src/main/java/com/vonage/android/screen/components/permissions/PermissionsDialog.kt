package com.vonage.android.screen.components.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PermissionsDialog(
    multiplePermissionsState: MultiplePermissionsState,
    modifier: Modifier = Modifier,
    navigateToPermissions: () -> Unit = {},
) {
    BasicAlertDialog(
        modifier = modifier.background(VonageVideoTheme.colors.background),
        onDismissRequest = {}, // intentionally block dismissing
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.camera_and_microphone_permissions_are_needed))
            Spacer(modifier = Modifier.height(8.dp))
            if (multiplePermissionsState.shouldShowRationale) {
                Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                    Text(stringResource(R.string.request_permissions))
                }
            } else {
                Button(onClick = navigateToPermissions) {
                    Text(stringResource(R.string.request_permissions_to_system))
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@PreviewLightDark
internal fun PermissionsDialogPreview() {
    VonageVideoTheme {
        PermissionsDialog(
            multiplePermissionsState = rememberMultiplePermissionsState(
                mutableListOf()
            )
        )
    }
}
