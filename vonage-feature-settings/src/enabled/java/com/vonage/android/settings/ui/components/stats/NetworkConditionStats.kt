package com.vonage.android.settings.ui.components.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.kotlin.model.NetworkCondition
import com.vonage.android.settings.R

@Composable
internal fun NetworkConditionStats(
    condition: NetworkCondition,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        StatsRow(
            label = stringResource(R.string.settings_stats_network_condition),
            value = condition.score.toString(),
        )
        StatsRow(
            label = stringResource(R.string.settings_stats_network_condition_reason),
            value = condition.reason,
        )
    }
}
