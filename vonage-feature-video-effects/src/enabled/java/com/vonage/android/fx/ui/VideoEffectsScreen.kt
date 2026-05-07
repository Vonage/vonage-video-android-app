@file:Suppress("LongMethod", "TooManyFunctions")

package com.vonage.android.fx.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.AddImage
import com.vonage.android.compose.vivid.icons.line.Blur
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.compose.vivid.icons.solid.Blur
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents the type of video effect the user can apply.
 */
sealed interface VideoEffectCategory {
    data object None : VideoEffectCategory
    data object BlurLow : VideoEffectCategory
    data object BlurHigh : VideoEffectCategory
    data object VirtualBackground : VideoEffectCategory
}

/**
 * Full-screen sheet for selecting a video effect.
 *
 * Displays a camera preview area, a horizontal toolbar of effect categories,
 * an optional scrollable background thumbnail grid (visible when [selectedCategory]
 * is [VideoEffectCategory.VirtualBackground]), and Cancel / Apply actions.
 *
 * @param publisher              Publisher participant for camera preview rendering.
 * @param isCameraEnabled        Whether the camera is currently active.
 * @param backgrounds            List of virtual background thumbnails to display.
 * @param selectedCategory       Currently active effect category.
 * @param selectedBackgroundId   ID of the selected virtual background, if any.
 * @param onDismiss              Invoked when the user taps Close or Cancel.
 * @param onApply                Invoked when the user taps Apply.
 * @param onCategorySelected     Invoked when the user taps an effect category icon.
 * @param onBackgroundSelected   Invoked when the user taps a background thumbnail.
 * @param modifier               Optional [Modifier] for the root layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun VideoEffectsScreen(
    publisher: Participant?,
    isCameraEnabled: Boolean,
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedCategory: VideoEffectCategory,
    selectedBackgroundId: String?,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onCategorySelected: (VideoEffectCategory) -> Unit,
    onBackgroundSelected: (VideoBackgroundItem) -> Unit,
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
                publisher = publisher,
                isCameraEnabled = isCameraEnabled,
                backgrounds = backgrounds,
                selectedCategory = selectedCategory,
                selectedBackgroundId = selectedBackgroundId,
                onCategorySelected = onCategorySelected,
                onBackgroundSelected = onBackgroundSelected,
                modifier = Modifier.weight(1f),
            )
        } else {
            PortraitContent(
                publisher = publisher,
                isCameraEnabled = isCameraEnabled,
                backgrounds = backgrounds,
                selectedCategory = selectedCategory,
                selectedBackgroundId = selectedBackgroundId,
                onCategorySelected = onCategorySelected,
                onBackgroundSelected = onBackgroundSelected,
                modifier = Modifier.weight(1f),
            )
        }

        VideoEffectsBottomBar(
            onCancel = onDismiss,
            onApply = onApply,
        )
    }
}

@Suppress("LongParameterList")
@Composable
private fun PortraitContent(
    publisher: Participant?,
    isCameraEnabled: Boolean,
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedCategory: VideoEffectCategory,
    selectedBackgroundId: String?,
    onCategorySelected: (VideoEffectCategory) -> Unit,
    onBackgroundSelected: (VideoBackgroundItem) -> Unit,
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

        EffectCategoryToolbar(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )

        VirtualBackgroundGrid(
            backgrounds = backgrounds,
            selectedBackgroundId = selectedBackgroundId,
            onBackgroundSelected = onBackgroundSelected,
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
    selectedCategory: VideoEffectCategory,
    selectedBackgroundId: String?,
    onCategorySelected: (VideoEffectCategory) -> Unit,
    onBackgroundSelected: (VideoBackgroundItem) -> Unit,
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

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            EffectCategoryToolbar(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
            )

            VirtualBackgroundGrid(
                backgrounds = backgrounds,
                selectedBackgroundId = selectedBackgroundId,
                onBackgroundSelected = onBackgroundSelected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
            )
        }
    }
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
        actions = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = VividIcons.Line.Close,
                    contentDescription = "Close",
                    tint = VonageVideoTheme.colors.secondary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VonageVideoTheme.colors.surface,
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
private fun EffectCategoryToolbar(
    selectedCategory: VideoEffectCategory,
    onCategorySelected: (VideoEffectCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = remember {
        listOf(
            VideoEffectCategory.None to VividIcons.Line.BlurOff,
            VideoEffectCategory.BlurLow to VividIcons.Line.Blur,
            VideoEffectCategory.BlurHigh to VividIcons.Solid.Blur,
            VideoEffectCategory.VirtualBackground to VividIcons.Line.AddImage,
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(VonageVideoTheme.dimens.paddingDefault),
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        categories.forEach { (category, icon) ->
            EffectCategoryItem(
                icon = icon,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
private fun EffectCategoryItem(
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
private fun VirtualBackgroundGrid(
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedBackgroundId: String?,
    onBackgroundSelected: (VideoBackgroundItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        items(items = backgrounds, key = { it.id }) { item ->
            BackgroundThumbnail(
                item = item,
                isSelected = item.id == selectedBackgroundId,
                onClick = { onBackgroundSelected(item) },
            )
        }
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
            publisher = null,
            isCameraEnabled = false,
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3"),
                VideoBackgroundItem(id = "bg4"),
                VideoBackgroundItem(id = "bg5"),
                VideoBackgroundItem(id = "bg6"),
            ),
            selectedCategory = VideoEffectCategory.VirtualBackground,
            selectedBackgroundId = "bg1",
            onDismiss = {},
            onApply = {},
            onCategorySelected = {},
            onBackgroundSelected = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
internal fun VideoEffectsScreenLandscapePreview() {
    VonageVideoTheme {
        VideoEffectsScreen(
            publisher = null,
            isCameraEnabled = false,
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3"),
                VideoBackgroundItem(id = "bg4"),
            ),
            selectedCategory = VideoEffectCategory.VirtualBackground,
            selectedBackgroundId = "bg1",
            onDismiss = {},
            onApply = {},
            onCategorySelected = {},
            onBackgroundSelected = {},
        )
    }
}
