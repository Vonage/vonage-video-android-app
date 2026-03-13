package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.settings.R

@Composable
internal fun FrameRateSelector(
    selected: CaptureFrameRate,
    onSelectionChange: (CaptureFrameRate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = CaptureFrameRate.entries

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        Text(
            text = stringResource(R.string.settings_frame_rate_title),
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.secondary,
        )

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            entries.forEachIndexed { index, frameRate ->
                SegmentedButton(
                    selected = frameRate == selected,
                    onClick = { onSelectionChange(frameRate) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = entries.size,
                        baseShape = RoundedCornerShape(8.dp),
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = VonageVideoTheme.colors.primary,
                        activeContentColor = VonageVideoTheme.colors.onPrimary,
                        inactiveContainerColor = VonageVideoTheme.colors.surface,
                        inactiveContentColor = VonageVideoTheme.colors.secondary,
                        activeBorderColor = VonageVideoTheme.colors.primary,
                        inactiveBorderColor = VonageVideoTheme.colors.border,
                    ),
                ) {
                    Text(
                        text = frameRate.label,
                        style = VonageVideoTheme.typography.caption,
                    )
                }
            }
        }
    }
}
