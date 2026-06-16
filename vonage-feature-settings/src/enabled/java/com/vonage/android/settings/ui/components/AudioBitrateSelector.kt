package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.settings.R
import com.vonage.android.settings.util.formatBitrate
import kotlin.math.roundToInt

private const val AUDIO_BITRATE_MIN = 6_000f
private const val AUDIO_BITRATE_MAX = 510_000f
private const val AUDIO_BITRATE_DEFAULT = 40_000
private const val DISABLED_ALPHA = 0.5f

@Composable
internal fun AudioBitrateSelector(
    audioBitrate: Int?,
    onAudioBitrateChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    helperText: String? = null,
) {
    val isCustom = audioBitrate != null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall)
            .alpha(if (enabled) 1f else DISABLED_ALPHA),
    ) {
        Text(
            text = stringResource(R.string.settings_audio_bitrate_title),
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.secondary,
        )

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.settings_audio_bitrate_default),
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.secondary,
            )
            Switch(
                checked = !isCustom,
                onCheckedChange = if (enabled) {
                    { auto ->
                        onAudioBitrateChange(if (auto) null else AUDIO_BITRATE_DEFAULT)
                    }
                } else {
                    {}
                },
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = VonageVideoTheme.colors.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = VonageVideoTheme.colors.border,
                    uncheckedBorderColor = VonageVideoTheme.colors.border,
                ),
            )
        }

        if (isCustom) {
            AudioBitrateSlider(
                bitrate = audioBitrate,
                onBitrateChange = onAudioBitrateChange,
                enabled = enabled,
            )
        }

        helperText?.let {
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            SettingHelperText(text = it)
        }
    }
}

@Composable
private fun AudioBitrateSlider(
    bitrate: Int,
    onBitrateChange: (Int?) -> Unit,
    enabled: Boolean,
) {
    var sliderValue by remember(bitrate) {
        mutableFloatStateOf(
            bitrate.toFloat().coerceIn(AUDIO_BITRATE_MIN, AUDIO_BITRATE_MAX),
        )
    }

    Column {
        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.settings_audio_bitrate_label),
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.tertiary,
            )
            Text(
                text = sliderValue.formatBitrate(),
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.secondary,
            )
        }

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        Slider(
            value = sliderValue,
            onValueChange = if (enabled) {
                { sliderValue = it }
            } else {
                {}
            },
            onValueChangeFinished = if (enabled) {
                {
                    onBitrateChange(sliderValue.roundToInt())
                }
            } else {
                {}
            },
            valueRange = AUDIO_BITRATE_MIN..AUDIO_BITRATE_MAX,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = VonageVideoTheme.colors.primary,
                activeTrackColor = VonageVideoTheme.colors.primary,
                inactiveTrackColor = VonageVideoTheme.colors.border,
            ),
        )
    }
}
