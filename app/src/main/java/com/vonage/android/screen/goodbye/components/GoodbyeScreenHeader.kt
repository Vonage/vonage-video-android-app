package com.vonage.android.screen.goodbye.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun GoodbyeScreenHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        Text(
            text = stringResource(R.string.goodbye_title),
            style = VonageVideoTheme.typography.headline,
            color = VonageVideoTheme.colors.textSecondary,
            textAlign = TextAlign.Start,
        )

        Text(
            text = stringResource(R.string.goodbye_subtitle),
            style = VonageVideoTheme.typography.heading2,
            color = VonageVideoTheme.colors.textTertiary,
            textAlign = TextAlign.Start,
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
            GoodbyeScreenHeader()
        }
    }
}
