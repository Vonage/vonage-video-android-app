package com.vonage.android.screen.landing.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.landing.LandingScreenTestTags.SUBTITLE_TAG
import com.vonage.android.screen.landing.LandingScreenTestTags.TITLE_TAG

@Composable
internal fun LandingScreenHeader(
    modifier: Modifier = Modifier,
) {
    val gradientColors = listOf(
        VonageVideoTheme.colors.primary,
        VonageVideoTheme.colors.secondary,
    )
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val brush = remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height * offset
                return LinearGradientShader(
                    colors = gradientColors,
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, heightOffset + size.height),
                    tileMode = TileMode.Mirror,
                )
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        Text(
            modifier = Modifier.testTag(TITLE_TAG),
            text = stringResource(R.string.landing_title),
            style = VonageVideoTheme.typography.headline
                .copy(brush = brush),
            color = VonageVideoTheme.colors.textSecondary,
            textAlign = TextAlign.Start,
        )

        Text(
            modifier = Modifier.testTag(SUBTITLE_TAG),
            text = stringResource(R.string.landing_subtitle),
            style = VonageVideoTheme.typography.heading2,
            color = VonageVideoTheme.colors.textTertiary,
            textAlign = TextAlign.Start,
        )
    }
}

@PreviewLightDark
@Composable
internal fun LandingScreenHeaderPreview() {
    VonageVideoTheme {
        LandingScreenHeader()
    }
}
