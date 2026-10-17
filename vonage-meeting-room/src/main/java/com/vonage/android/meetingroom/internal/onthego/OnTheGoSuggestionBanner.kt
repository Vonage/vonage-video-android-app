package com.vonage.android.meetingroom.internal.onthego

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.R

@Composable
internal fun OnTheGoSuggestionBanner(
    timeoutMs: Long,
    onSwitch: () -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(1f) }
    LaunchedEffect(timeoutMs) {
        progress.snapTo(1f)
        progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = timeoutMs.toInt()),
        )
    }
    val shape = VonageVideoTheme.shapes.large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(VonageVideoTheme.dimens.paddingMedium)
            .clip(shape)
            .background(VonageVideoTheme.colors.surface)
            .border(
                width = VonageVideoTheme.dimens.borderWidthThin,
                color = VonageVideoTheme.colors.border,
                shape = shape,
            )
            .testTag(OnTheGoTestTags.BANNER),
    ) {
        Row(
            modifier = Modifier.padding(VonageVideoTheme.dimens.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
        ) {
            Icon(
                imageVector = Icons.Filled.DirectionsWalk,
                contentDescription = null,
                tint = VonageVideoTheme.colors.textSecondary,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.on_the_go_suggestion_title),
                    style = VonageVideoTheme.typography.captionSemibold,
                    color = VonageVideoTheme.colors.textSecondary,
                )
                Text(
                    text = stringResource(R.string.on_the_go_suggestion_body),
                    style = VonageVideoTheme.typography.caption,
                    color = VonageVideoTheme.colors.textSecondary,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                BannerButton(
                    label = stringResource(R.string.on_the_go_switch),
                    filled = true,
                    onClick = onSwitch,
                    modifier = Modifier.testTag(OnTheGoTestTags.SWITCH),
                )
                BannerButton(
                    label = stringResource(R.string.on_the_go_not_now),
                    filled = false,
                    onClick = onNotNow,
                    modifier = Modifier.testTag(OnTheGoTestTags.NOT_NOW),
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = VonageVideoTheme.dimens.paddingMedium)
                .padding(bottom = VonageVideoTheme.dimens.paddingSmall)
                .fillMaxWidth()
                .height(VonageVideoTheme.dimens.borderWidthDefault),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.value)
                    .height(VonageVideoTheme.dimens.borderWidthDefault)
                    .clip(RoundedCornerShape(VonageVideoTheme.dimens.borderWidthDefault))
                    .background(VonageVideoTheme.colors.primary),
            )
        }
    }
}

@Composable
private fun BannerButton(
    label: String,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (filled) VonageVideoTheme.colors.primary else VonageVideoTheme.colors.surface
    val content = if (filled) VonageVideoTheme.colors.onPrimary else VonageVideoTheme.colors.textSecondary
    Text(
        text = label,
        style = VonageVideoTheme.typography.captionSemibold,
        color = content,
        modifier = modifier
            .padding(top = VonageVideoTheme.dimens.paddingXSmall)
            .clip(VonageVideoTheme.shapes.medium)
            .background(background)
            .clickable(onClick = onClick)
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingSmall,
                vertical = VonageVideoTheme.dimens.paddingXSmall,
            ),
    )
}
