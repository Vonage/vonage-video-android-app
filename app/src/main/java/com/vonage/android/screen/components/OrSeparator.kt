package com.vonage.android.screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme

@Suppress("MagicNumber")
@Composable
fun OrSeparator(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(.5f),
            thickness = 1.dp,
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(R.string.landing_or),
            color = VonageVideoTheme.colors.textPrimary,
            fontSize = 14.sp,
        )
        HorizontalDivider(
            modifier = Modifier.weight(.5f),
            thickness = 1.dp,
        )
    }
}
