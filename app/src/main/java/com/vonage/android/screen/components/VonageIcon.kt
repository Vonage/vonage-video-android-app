package com.vonage.android.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun VonageIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(R.drawable.ic_vonage),
        tint = MaterialTheme.colorScheme.inverseSurface,
        contentDescription = null,
        modifier = modifier.size(80.dp)
    )
}

@Preview
@Composable
internal fun VonageIconPreview() {
    VonageVideoTheme {
        VonageIcon()
    }
}

@Preview
@Composable
internal fun VonageIconPreviewDark() {
    VonageVideoTheme(
        darkTheme = true
    ) {
        VonageIcon()
    }
}