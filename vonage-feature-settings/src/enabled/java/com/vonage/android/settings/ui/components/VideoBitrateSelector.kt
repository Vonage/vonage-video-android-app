package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.settings.R
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private const val MIN_BITRATE = 5_000f
private const val MAX_BITRATE = 10_000_000f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoBitrateSelector(
    config: VideoBitrateConfig,
    onConfigChange: (VideoBitrateConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var sliderValue by remember(config.maxBitrate) {
        mutableFloatStateOf(
            config.maxBitrate?.toFloat()?.coerceIn(MIN_BITRATE, MAX_BITRATE) ?: MIN_BITRATE
        )
    }
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        Text(
            text = stringResource(R.string.settings_bitrate_title),
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.secondary,
        )

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = config.preset.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VonageVideoTheme.colors.primary,
                    unfocusedBorderColor = VonageVideoTheme.colors.border,
                    focusedTextColor = VonageVideoTheme.colors.secondary,
                    unfocusedTextColor = VonageVideoTheme.colors.secondary,
                    focusedTrailingIconColor = VonageVideoTheme.colors.primary,
                    unfocusedTrailingIconColor = VonageVideoTheme.colors.tertiary,
                ),
                textStyle = VonageVideoTheme.typography.bodyBaseSemibold,
                label = {
                    Text(
                        text = stringResource(R.string.settings_bitrate_preset_label),
                        style = VonageVideoTheme.typography.caption,
                    )
                },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = VonageVideoTheme.colors.surface,
            ) {
                VideoBitratePreset.entries.forEach { preset ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = preset.label,
                                    style = VonageVideoTheme.typography.bodyBaseSemibold,
                                    color = VonageVideoTheme.colors.secondary,
                                )
                                Text(
                                    text = preset.description,
                                    style = VonageVideoTheme.typography.caption,
                                    color = VonageVideoTheme.colors.tertiary,
                                )
                            }
                        },
                        onClick = {
                            expanded = false
                            val newConfig = VideoBitrateConfig(
                                preset = preset,
                                maxBitrate = preset.defaultMaxBitrate,
                            )
                            onConfigChange(newConfig)
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        Text(
            text = config.preset.description,
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.tertiary,
        )

        if (config.preset == VideoBitratePreset.CUSTOM) {
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))

            Text(
                text = stringResource(R.string.settings_bitrate_max_label),
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.tertiary,
            )

            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    onConfigChange(config.copy(maxBitrate = sliderValue.roundToInt()))
                },
                valueRange = MIN_BITRATE..MAX_BITRATE,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = VonageVideoTheme.colors.primary,
                    activeTrackColor = VonageVideoTheme.colors.primary,
                    inactiveTrackColor = VonageVideoTheme.colors.border,
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${numberFormat.format(MIN_BITRATE.roundToInt())} bps",
                    style = VonageVideoTheme.typography.caption,
                    color = VonageVideoTheme.colors.tertiary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${numberFormat.format(sliderValue.roundToInt())} bps",
                    style = VonageVideoTheme.typography.bodyBaseSemibold,
                    color = VonageVideoTheme.colors.primary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${numberFormat.format(MAX_BITRATE.roundToInt())} bps",
                    style = VonageVideoTheme.typography.caption,
                    color = VonageVideoTheme.colors.tertiary,
                )
            }
        }
    }
}
