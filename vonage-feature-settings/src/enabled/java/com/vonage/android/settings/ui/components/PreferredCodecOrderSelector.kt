package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.ChevronDown
import com.vonage.android.compose.vivid.icons.line.ChevronUp
import com.vonage.android.kotlin.model.DEFAULT_VIDEO_CODEC_ORDER
import com.vonage.android.kotlin.model.VideoCodec
import com.vonage.android.settings.R

@Composable
internal fun PreferredCodecOrderSelector(
    selectedOrder: List<VideoCodec>?,
    onOrderChange: (List<VideoCodec>?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isManual = selectedOrder != null
    val displayOrder = selectedOrder ?: DEFAULT_VIDEO_CODEC_ORDER

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        Text(
            text = stringResource(R.string.settings_codec_order_title),
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
                text = stringResource(R.string.settings_codec_order_auto),
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.secondary,
            )
            Switch(
                checked = !isManual,
                onCheckedChange = { auto ->
                    onOrderChange(if (auto) null else DEFAULT_VIDEO_CODEC_ORDER)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = VonageVideoTheme.colors.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = VonageVideoTheme.colors.border,
                    uncheckedBorderColor = VonageVideoTheme.colors.border,
                ),
            )
        }

        if (isManual) {
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            displayOrder.forEachIndexed { index, codec ->
                CodecRow(
                    position = index + 1,
                    codec = codec,
                    canMoveUp = index > 0,
                    canMoveDown = index < displayOrder.lastIndex,
                    onMoveUp = {
                        val mutable = displayOrder.toMutableList()
                        mutable[index] =
                            mutable[index - 1].also { mutable[index - 1] = mutable[index] }
                        onOrderChange(mutable)
                    },
                    onMoveDown = {
                        val mutable = displayOrder.toMutableList()
                        mutable[index] =
                            mutable[index + 1].also { mutable[index + 1] = mutable[index] }
                        onOrderChange(mutable)
                    },
                )
            }
        }
    }
}

@Composable
private fun CodecRow(
    position: Int,
    codec: VideoCodec,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$position.",
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.tertiary,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = codec.label,
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.secondary,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onMoveUp,
            enabled = canMoveUp,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = VividIcons.Line.ChevronUp,
                contentDescription = null,
                tint = if (canMoveUp) VonageVideoTheme.colors.primary else VonageVideoTheme.colors.border,
            )
        }
        IconButton(
            onClick = onMoveDown,
            enabled = canMoveDown,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = VividIcons.Line.ChevronDown,
                contentDescription = null,
                tint = if (canMoveDown) VonageVideoTheme.colors.primary else VonageVideoTheme.colors.border,
            )
        }
    }
}
