package com.vonage.android.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.vonage.android.kotlin.model.NoiseSuppression
import com.vonage.android.kotlin.model.PublisherParticipant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberNoiseSuppression(publisher: PublisherParticipant?): StateFlow<NoiseSuppression> =
    remember(publisher?.noiseSuppression) {
        publisher?.let { publisher.noiseSuppression } ?: MutableStateFlow(NoiseSuppression.DISABLED)
    }
