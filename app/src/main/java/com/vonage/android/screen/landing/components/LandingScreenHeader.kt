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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
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
    // Read only in the draw phase, so the animation does not recompose or re-lay out the text.
    val offset = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        Text(
            modifier = Modifier
                .testTag(TITLE_TAG)
                // Offscreen layer so SrcIn only masks the gradient with this text's glyphs.
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    val progress = offset.value
                    drawContent()
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = gradientColors,
                            start = Offset(size.width * progress, size.height * progress),
                            end = Offset(size.width * (1 + progress), size.height * (1 + progress)),
                            tileMode = TileMode.Mirror,
                        ),
                        blendMode = BlendMode.SrcIn,
                    )
                },
            text = stringResource(R.string.landing_title),
            style = VonageVideoTheme.typography.headline,
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
