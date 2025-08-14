package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun GenericLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
