package com.vonage.android.screen.goodbye.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.goodbye.GoodbyeScreenActions

@Composable
fun GoodbyeScreenHeader(
    actions: GoodbyeScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.goodbye_title),
            style = VonageVideoTheme.typography.heading1,
            color = VonageVideoTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.goodbye_subtitle),
            style = VonageVideoTheme.typography.bodyBase,
            color = VonageVideoTheme.colors.textDisabled,
            textAlign = TextAlign.Center,
        )

        VonageOutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            text = stringResource(R.string.goodbye_rejoin_button_label),
            onClick = actions.onReEnter,
        )

        VonageButton(
            text = stringResource(R.string.goodbye_return_home_button_label),
            onClick = actions.onGoHome,
        )
    }
}

@PreviewLightDark
@Composable
internal fun GoodbyeScreenHeaderPreview() {
    VonageVideoTheme {
        Surface(
            modifier = Modifier
                .background(color = VonageVideoTheme.colors.background)
        ) {
            GoodbyeScreenHeader(
                actions = GoodbyeScreenActions(),
            )
        }
    }
}
