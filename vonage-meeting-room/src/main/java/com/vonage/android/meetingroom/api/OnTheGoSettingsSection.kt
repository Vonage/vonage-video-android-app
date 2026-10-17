package com.vonage.android.meetingroom.api

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.internal.onthego.OnTheGoTestTags
import com.vonage.android.meetingroom.internal.onthego.SharedPreferencesOnTheGoPreferences

/**
 * Settings row for suggesting On-the-Go mode while the user is moving.
 *
 * Drop this into the app settings screen when `meetingRoomSettings.allowOnTheGo` is on.
 * It shares its preference with the in-call controller.
 */
@ExperimentalMeetingRoomApi
@Composable
fun OnTheGoSettingsSection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val preferences = remember(context) { SharedPreferencesOnTheGoPreferences(context) }
    var enabled by remember { mutableStateOf(preferences.suggestWhenMoving) }
    DisposableEffect(preferences) {
        val listener: (Boolean) -> Unit = { value -> enabled = value }
        preferences.addListener(listener)
        onDispose {
            preferences.removeListener(listener)
            preferences.close()
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.on_the_go_suggest_when_moving),
                style = VonageVideoTheme.typography.bodyBaseSemibold,
                color = VonageVideoTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = enabled,
                onCheckedChange = { checked ->
                    preferences.suggestWhenMoving = checked
                    enabled = checked
                },
                modifier = Modifier.testTag(OnTheGoTestTags.SETTINGS_TOGGLE),
            )
        }
        Text(
            text = stringResource(R.string.on_the_go_settings_footer),
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.textSecondary,
        )
    }
}
