package com.vonage.android.meetingroom.internal.permissions

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.internal.util.pip.findActivity

/**
 * Built-in permission gate composable. Requests [permissions] immediately on first
 * composition. Calls [onGrant] as soon as all permissions are granted.
 *
 * Shows a non-dismissible blocking dialog when any permission is denied:
 * - If rationale can be shown (soft deny): presents a "Request Permissions" button.
 * - If permanently denied: presents an "Open Settings" button that navigates the user
 *   to the system app-settings screen.
 *
 * Callers that need custom permission UI should supply a `permissionContent` lambda via
 * [com.vonage.android.meetingroom.api.MeetingRoomBuilder.permissionContent] instead.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DefaultPermissionContent(
    permissions: List<String>,
    onGrant: () -> Unit,
) {
    if (permissions.isEmpty()) {
        onGrant()
        return
    }

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        if (results.values.all { it }) {
            onGrant()
        } else {
            val activity = context.findActivity()
            shouldShowRationale = results
                .filter { !it.value }
                .any { (permission, _) ->
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                }
            showDialog = true
        }
    }

    LaunchedEffect(Unit) {
        val allGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            onGrant()
        } else {
            launcher.launch(permissions.toTypedArray())
        }
    }

    if (showDialog) {
        BasicAlertDialog(
            modifier = Modifier.background(VonageVideoTheme.colors.background),
            onDismissRequest = {}, // intentionally non-dismissible
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.meeting_room_permission_required_title),
                    color = VonageVideoTheme.colors.onSurface,
                    style = VonageVideoTheme.typography.heading1,
                    textAlign = TextAlign.Center,
                )
                if (shouldShowRationale) {
                    VonageButton(
                        text = stringResource(R.string.meeting_room_permission_request_button),
                        onClick = {
                            showDialog = false
                            launcher.launch(permissions.toTypedArray())
                        },
                    )
                } else {
                    VonageButton(
                        text = stringResource(R.string.meeting_room_permission_open_settings_button),
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            )
                            context.startActivity(intent)
                        },
                    )
                }
            }
        }
    }
}
