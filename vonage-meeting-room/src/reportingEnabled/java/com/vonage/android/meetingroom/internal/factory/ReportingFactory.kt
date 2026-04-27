package com.vonage.android.meetingroom.internal.factory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.R

@Composable
internal fun reportingContent(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(VonageVideoTheme.dimens.paddingLarge),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(R.string.report_bottombar_button_label))
    }
}
