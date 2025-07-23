package com.vonage.android.screen.goodbye

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.TopBanner

@Composable
fun GoodbyeScreen(
    actions: GoodbyeScreenActions,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBanner(
                onBack = actions.onReEnter,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.goodbye_title),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    textAlign = TextAlign.Center,
                    lineHeight = 46.sp,
                )

                Text(
                    text = stringResource(R.string.goodbye_subtitle),
                    fontSize = 16.sp,
                    color = VonageVideoTheme.colors.textPrimaryDisabled,
                    textAlign = TextAlign.Center,
                )

                VonageOutlinedButton(
                    text = stringResource(R.string.goodbye_rejoin_button_label),
                    onClick = actions.onReEnter,
                )

                VonageButton(
                    text = stringResource(R.string.goodbye_return_home_button_label),
                    onClick = actions.onGoHome,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun GoodbyeScreenPreview() {
    VonageVideoTheme {
        GoodbyeScreen(
            actions = GoodbyeScreenActions(),
        )
    }
}
