package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.VONAGE_VIDEO_MAX_BITRATE
import com.vonage.android.kotlin.model.VONAGE_VIDEO_MIN_BITRATE
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.settings.R
import com.vonage.android.settings.util.formatBitrate
import kotlin.math.roundToInt

@Composable
internal fun VideoBitrateSelector(
    config: VideoBitrateConfig,
    onConfigChange: (VideoBitrateConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        VideoBitratePreset.entries.map {
            DropdownItem(value = it, label = it.label, description = it.description)
        }
    }

    DropdownSelector(
        title = stringResource(R.string.settings_bitrate_title),
        selectedLabel = config.preset.label,
        dropdownLabel = stringResource(R.string.settings_bitrate_preset_label),
        items = items,
        onItemSelected = { preset ->
            onConfigChange(
                VideoBitrateConfig(preset = preset, maxBitrate = preset.defaultMaxBitrate),
            )
        },
        modifier = modifier,
        selectedDescription = config.preset.description,
        extraContent = if (config.preset == VideoBitratePreset.CUSTOM) {
            { BitrateSlider(config = config, onConfigChange = onConfigChange) }
        } else {
            null
        },
    )
}

@Composable
private fun BitrateSlider(
    config: VideoBitrateConfig,
    onConfigChange: (VideoBitrateConfig) -> Unit,
) {
    var sliderValue by remember(config.maxBitrate) {
        mutableFloatStateOf(
            config.maxBitrate?.toFloat()
                ?.coerceIn(VONAGE_VIDEO_MIN_BITRATE, VONAGE_VIDEO_MAX_BITRATE)
                ?: VONAGE_VIDEO_MIN_BITRATE,
        )
    }
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
        valueRange = VONAGE_VIDEO_MIN_BITRATE..VONAGE_VIDEO_MAX_BITRATE,
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
            text = VONAGE_VIDEO_MIN_BITRATE.formatBitrate(),
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.tertiary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = sliderValue.formatBitrate(),
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.primary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = VONAGE_VIDEO_MAX_BITRATE.formatBitrate(),
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.tertiary,
        )
    }
}
