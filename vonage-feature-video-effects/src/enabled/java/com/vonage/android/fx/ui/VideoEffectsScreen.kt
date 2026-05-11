@file:Suppress("LongMethod", "TooManyFunctions")

package com.vonage.android.fx.ui

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.ParticipantVideoRenderer
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.line.Blur as LineBlur
import com.vonage.android.compose.vivid.icons.solid.Blur as SolidBlur
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VideoEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Full-screen sheet for selecting a video effect.
 *
 * Displays a camera preview area, a flat scrollable grid with "Effects" and "Backgrounds"
 * section headers, and Cancel / Apply actions. Effects are applied to [previewPublisher]
 * immediately on selection so the user can preview them locally; the real session publisher
 * is not touched until [onApply] is invoked.
 *
 * @param previewPublisher     Isolated preview publisher for the camera preview. Effects
 *                             selected here are applied to this publisher only.
 * @param isCameraEnabled      Whether the preview camera is currently active.
 * @param backgrounds          List of virtual background thumbnails to display.
 * @param selectedEffect       Currently active video effect (reflects preview publisher state).
 * @param onDismiss            Invoked when the user taps Close or Cancel (no effect applied
 *                             to the real publisher).
 * @param onApply              Invoked when the user taps Apply; receives the chosen
 *                             [VideoEffect] so the caller can commit it to the real publisher.
 * @param onEffectSelect       Invoked when the user selects an effect; caller should apply
 *                             it to [previewPublisher] only.
 * @param modifier             Optional [Modifier] for the root layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun VideoEffectsScreen(
    previewPublisher: Participant?,
    isCameraEnabled: Boolean,
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    onDismiss: () -> Unit,
    onApply: (VideoEffect) -> Unit,
    onEffectSelect: (VideoEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background),
    ) {
        VideoEffectsTopBar(onDismiss = onDismiss)

        if (isLandscape) {
            LandscapeContent(
                publisher = previewPublisher,
                isCameraEnabled = isCameraEnabled,
                backgrounds = backgrounds,
                selectedEffect = selectedEffect,
                onEffectSelect = onEffectSelect,
                modifier = Modifier.weight(1f),
            )
        } else {
            PortraitContent(
                publisher = previewPublisher,
                isCameraEnabled = isCameraEnabled,
                backgrounds = backgrounds,
                selectedEffect = selectedEffect,
                onEffectSelect = onEffectSelect,
                modifier = Modifier.weight(1f),
            )
        }

        VideoEffectsBottomBar(
            onCancel = onDismiss,
            onApply = { onApply(selectedEffect) },
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun PortraitContent(
    publisher: Participant?,
    isCameraEnabled: Boolean,
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    onEffectSelect: (VideoEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CameraPreview(
            publisher = publisher,
            isCameraEnabled = isCameraEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(VonageVideoTheme.dimens.paddingDefault),
        )

        EffectsAndBackgroundsGrid(
            backgrounds = backgrounds,
            selectedEffect = selectedEffect,
            onEffectSelect = onEffectSelect,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun LandscapeContent(
    publisher: Participant?,
    isCameraEnabled: Boolean,
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    onEffectSelect: (VideoEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        CameraPreview(
            publisher = publisher,
            isCameraEnabled = isCameraEnabled,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(VonageVideoTheme.dimens.paddingDefault),
        )

        EffectsAndBackgroundsGrid(
            backgrounds = backgrounds,
            selectedEffect = selectedEffect,
            onEffectSelect = onEffectSelect,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
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
            SectionHeader(text = "Effects")
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
                SectionHeader(text = "Backgrounds")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoEffectsTopBar(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = "Video effects",
                style = VonageVideoTheme.typography.heading3,
                color = VonageVideoTheme.colors.secondary,
            )
        },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = VividIcons.Line.Close,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.secondary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VonageVideoTheme.colors.background,
        ),
    )
}

@Composable
private fun CameraPreview(
    publisher: Participant?,
    isCameraEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(CAMERA_PREVIEW_ASPECT_RATIO)
            .clip(VonageVideoTheme.shapes.large)
            .background(VonageVideoTheme.colors.disabled),
        contentAlignment = Alignment.Center,
    ) {
        if (isCameraEnabled && publisher != null) {
            ParticipantVideoRenderer(
                participant = publisher,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = "Your camera is turned off.",
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
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

@Composable
private fun VideoEffectsBottomBar(
    onCancel: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingDefault,
                vertical = VonageVideoTheme.dimens.paddingMedium,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onCancel) {
            Text(
                text = "Cancel",
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.secondary,
            )
        }

        VonageButton(
            text = "Apply",
            onClick = onApply,
        )
    }
}

private const val CAMERA_PREVIEW_ASPECT_RATIO = 16f / 9f
private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f
private const val EFFECT_ICON_SIZE = 56
private const val GRID_COLUMNS = 2
private const val SELECTED_ALPHA = 0.12f

@Preview(showBackground = true)
@Composable
internal fun VideoEffectsScreenPortraitPreview() {
    VonageVideoTheme {
        VideoEffectsScreen(
            previewPublisher = null,
            isCameraEnabled = false,
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3"),
                VideoBackgroundItem(id = "bg4"),
                VideoBackgroundItem(id = "bg5"),
                VideoBackgroundItem(id = "bg6"),
            ),
            selectedEffect = VideoEffect.None,
            onDismiss = {},
            onApply = {},
            onEffectSelect = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
internal fun VideoEffectsScreenLandscapePreview() {
    VonageVideoTheme {
        VideoEffectsScreen(
            previewPublisher = null,
            isCameraEnabled = false,
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3"),
                VideoBackgroundItem(id = "bg4"),
            ),
            selectedEffect = VideoEffect.BlurLow,
            onDismiss = {},
            onApply = {},
            onEffectSelect = {},
        )
    }
}
