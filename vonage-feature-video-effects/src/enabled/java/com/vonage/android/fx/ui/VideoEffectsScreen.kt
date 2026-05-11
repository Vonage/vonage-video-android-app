package com.vonage.android.fx.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vonage.android.videofx.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.line.Blur as LineBlur
import com.vonage.android.compose.vivid.icons.solid.Blur as SolidBlur
import com.vonage.android.kotlin.model.VideoEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Bottom-sheet content for selecting a video effect.
 *
 * Displays a "Video effects" title and a scrollable grid of blur effects and virtual
 * background thumbnails. The selected effect is applied to the publisher immediately on
 * tap ([onEffectSelect] is called with every selection). There is no Cancel/Apply step —
 * the sheet is dismissed by swiping it down.
 *
 * @param backgrounds    List of virtual background thumbnails to display.
 * @param selectedEffect Currently active video effect (used to highlight the active tile).
 * @param onEffectSelect Invoked when the user taps an effect tile; caller should apply
 *                       it to the publisher immediately.
 * @param modifier       Optional [Modifier] for the root layout.
 */
@Composable
fun VideoEffectsScreen(
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    onEffectSelect: (VideoEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(VonageVideoTheme.colors.background),
    ) {
        // Title only — no close button; the sheet is dismissed via swipe-down.
        Text(
            text = stringResource(R.string.video_effects_title),
            style = VonageVideoTheme.typography.heading3,
            color = VonageVideoTheme.colors.secondary,
            modifier = Modifier.padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingMedium,
            ),
        )

        EffectsAndBackgroundsGrid(
            backgrounds = backgrounds,
            selectedEffect = selectedEffect,
            onEffectSelect = onEffectSelect,
            modifier = Modifier.padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
        )
    }
}

@Composable
private fun EffectsAndBackgroundsGrid(
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    onEffectSelect: (VideoEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val blurEffects = remember {
        listOf(
            VideoEffect.None to VividIcons.Line.BlurOff,
            VideoEffect.BlurLow to VividIcons.Line.LineBlur,
            VideoEffect.BlurHigh to VividIcons.Solid.SolidBlur,
        )
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        // Effects section header
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            SectionHeader(text = stringResource(R.string.video_effects_section_effects))
        }
        // All blur effects in a single full-width row
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
            ) {
                blurEffects.forEach { (effect, icon) ->
                    EffectItem(
                        icon = icon,
                        isSelected = selectedEffect == effect,
                        onClick = { onEffectSelect(effect) },
                    )
                }
            }
        }

        if (backgrounds.isNotEmpty()) {
            // Backgrounds section header
            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                SectionHeader(text = stringResource(R.string.video_effects_section_backgrounds))
            }
            // Background thumbnails
            items(items = backgrounds, key = { it.id }) { item ->
                BackgroundThumbnail(
                    item = item,
                    isSelected = selectedEffect is VideoEffect.BackgroundImage &&
                        selectedEffect.id == item.id,
                    onClick = {
                        val path = item.imagePath ?: return@BackgroundThumbnail
                        onEffectSelect(VideoEffect.BackgroundImage(id = item.id, imagePath = path))
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = VonageVideoTheme.typography.bodyBase,
        color = VonageVideoTheme.colors.secondary,
        modifier = modifier.padding(
            top = VonageVideoTheme.dimens.spaceSmall,
            bottom = VonageVideoTheme.dimens.spaceXSmall,
        ),
    )
}

@Composable
private fun EffectItem(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = VonageVideoTheme.colors.primary
    val surface = VonageVideoTheme.colors.surface
    val secondary = VonageVideoTheme.colors.secondary

    val borderColor = if (isSelected) primary else Color.Transparent
    val backgroundColor = if (isSelected) primary.copy(alpha = SELECTED_ALPHA) else surface
    val iconTint = if (isSelected) primary else secondary

    Box(
        modifier = modifier
            .size(EFFECT_ICON_SIZE.dp)
            .clip(VonageVideoTheme.shapes.medium)
            .background(backgroundColor)
            .border(
                BorderStroke(VonageVideoTheme.dimens.borderWidthDefault, borderColor),
                VonageVideoTheme.shapes.medium
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
    }
}

@Composable
private fun BackgroundThumbnail(
    item: VideoBackgroundItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = VonageVideoTheme.colors.primary
    val borderColor = if (isSelected) primary else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(THUMBNAIL_ASPECT_RATIO)
            .clip(VonageVideoTheme.shapes.medium)
            .background(VonageVideoTheme.colors.surface)
            .border(
                BorderStroke(VonageVideoTheme.dimens.borderWidthDefault, borderColor),
                VonageVideoTheme.shapes.medium
            )
            .clickable { onClick() },
    ) {
        if (item.thumbnailRes != null) {
            Image(
                painter = painterResource(item.thumbnailRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f
private const val EFFECT_ICON_SIZE = 56
private const val GRID_COLUMNS = 2
private const val SELECTED_ALPHA = 0.12f

@Preview(showBackground = true)
@Composable
internal fun VideoEffectsScreenPortraitPreview() {
    VonageVideoTheme {
        VideoEffectsScreen(
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3"),
                VideoBackgroundItem(id = "bg4"),
                VideoBackgroundItem(id = "bg5"),
                VideoBackgroundItem(id = "bg6"),
            ),
            selectedEffect = VideoEffect.None,
            onEffectSelect = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
internal fun VideoEffectsScreenLandscapePreview() {
    VonageVideoTheme {
        VideoEffectsScreen(
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3"),
                VideoBackgroundItem(id = "bg4"),
            ),
            selectedEffect = VideoEffect.BlurLow,
            onEffectSelect = {},
        )
    }
}
