package com.vonage.android.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vonage.android.R

@Composable
fun VonageIcon(
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(R.drawable.ic_vonage),
        contentDescription = null,
        modifier = modifier.size(80.dp)
    )
}
