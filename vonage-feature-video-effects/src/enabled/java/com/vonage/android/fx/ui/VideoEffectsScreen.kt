package com.vonage.android.fx.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.AddImage
import com.vonage.android.compose.vivid.icons.line.Blur as LineBlur
import com.vonage.android.compose.vivid.icons.line.BlurOff
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.compose.vivid.icons.solid.Blur as SolidBlur
import com.vonage.android.kotlin.model.VideoEffect
import com.vonage.android.videofx.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Bottom-sheet content for selecting a video effect.
 *
 * Displays a "Video effects" title and a scrollable grid of blur effects and virtual
 * background thumbnails. The selected effect is applied to the publisher immediately on
 * tap ([onEffectSelect] is called with every selection). There is no Cancel/Apply step —
 * the sheet is dismissed by swiping it down.
 *
 * @param backgrounds            List of virtual background thumbnails to display.
 * @param selectedEffect         Currently active video effect (used to highlight the active tile).
 * @param remainingBackgroundSlots Number of additional user backgrounds the app can accept before
 *                               reaching [UserBackgroundRepository.MAX_USER_BACKGROUNDS]. The
 *                               "Add image" tile is hidden when this is 0. When it is 1, the
 *                               system single-item photo picker is shown; for 2+ the multi-item
 *                               picker is used with the count capped to this value.
 * @param onEffectSelect         Invoked when the user taps an effect tile; caller should apply
 *                               it to the publisher immediately.
 * @param onAddBackground        Invoked with the list of content [Uri]s when the user picks one or
 *                               more images from the system photo picker. Caller is responsible for
 *                               IO / file saving.
 * @param onDeleteBackground     Invoked when the user taps the delete button on a user-uploaded
 *                               background tile.
 * @param modifier               Optional [Modifier] for the root layout.
 */
@Composable
fun VideoEffectsScreen(
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    remainingBackgroundSlots: Int,
    onEffectSelect: (VideoEffect) -> Unit,
    onAddBackground: (List<Uri>) -> Unit,
    onDeleteBackground: (VideoBackgroundItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val multiLauncher = rememberLauncherForActivityResult(
        contract = PickMultipleVisualMedia(maxItems = maxOf(2, remainingBackgroundSlots)),
    ) { uris -> if (uris.isNotEmpty()) onAddBackground(uris) }

    val singleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let { onAddBackground(listOf(it)) } }

    Column(
        modifier = modifier
            .testTag(VideoEffectsTestTags.VIDEO_EFFECTS_SHEET_CONTENT)
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
            canAddBackground = remainingBackgroundSlots > 0,
            onEffectSelect = onEffectSelect,
            onAddImageClick = {
                val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                if (remainingBackgroundSlots >= 2) {
                    multiLauncher.launch(request)
                } else {
                    singleLauncher.launch(request)
                }
            },
            onDeleteBackground = onDeleteBackground,
            modifier = Modifier.padding(horizontal = VonageVideoTheme.dimens.paddingDefault),
        )
    }
}

@Composable
private fun EffectsAndBackgroundsGrid(
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    canAddBackground: Boolean,
    onEffectSelect: (VideoEffect) -> Unit,
    onAddImageClick: () -> Unit,
    onDeleteBackground: (VideoBackgroundItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val blurEffects = remember {
        listOf(
            VideoEffect.None to VividIcons.Line.BlurOff,
            VideoEffect.BlurLow to VividIcons.Line.LineBlur,
            VideoEffect.BlurHigh to VividIcons.Solid.SolidBlur,
        )
    }

    // Show the backgrounds section when there are existing backgrounds OR when a new one can be added.
    val showBackgroundsSection = backgrounds.isNotEmpty() || canAddBackground

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
                    val tileTag = when (effect) {
                        VideoEffect.None -> VideoEffectsTestTags.VIDEO_EFFECTS_NONE_TILE
                        VideoEffect.BlurLow -> VideoEffectsTestTags.VIDEO_EFFECTS_BLUR_LOW_TILE
                        else -> VideoEffectsTestTags.VIDEO_EFFECTS_BLUR_HIGH_TILE
                    }
                    EffectItem(
                        modifier = Modifier.testTag(tileTag),
                        icon = icon,
                        isSelected = selectedEffect == effect,
                        onClick = { onEffectSelect(effect) },
                    )
                }
            }
        }

        if (showBackgroundsSection) {
            // Backgrounds section header
            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                SectionHeader(text = stringResource(R.string.video_effects_section_backgrounds))
            }
            // Background thumbnails (built-in and user-uploaded)
            items(items = backgrounds, key = { it.id }) { item ->
                BackgroundThumbnail(
                    item = item,
                    isSelected = selectedEffect is VideoEffect.BackgroundImage &&
                        selectedEffect.id == item.id,
                    onClick = {
                        val path = item.imagePath ?: return@BackgroundThumbnail
                        onEffectSelect(VideoEffect.BackgroundImage(id = item.id, imagePath = path))
                    },
                    onDelete = if (item.isUserUploaded) {
                        { onDeleteBackground(item) }
                    } else {
                        null
                    },
                )
            }
            // "Add image" tile — shown when the user has not yet reached the limit
            if (canAddBackground) {
                item {
                    AddBackgroundTile(
                        modifier = Modifier.testTag(VideoEffectsTestTags.VIDEO_EFFECTS_ADD_BACKGROUND_TILE),
                        onClick = onAddImageClick,
                    )
                }
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
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val primary = VonageVideoTheme.colors.primary
    val borderColor = if (isSelected) primary else Color.Transparent

    // For user-uploaded images (thumbnailRes == null), load the bitmap from disk asynchronously.
    // Use inSampleSize to decode a thumbnail-sized bitmap rather than the full saved image.
    val painter: Painter = if (item.thumbnailRes != null) {
        painterResource(item.thumbnailRes)
    } else {
        val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, item.imagePath) {
            value = withContext(Dispatchers.IO) {
                item.imagePath?.let { path ->
                    runCatching {
                        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        BitmapFactory.decodeFile(path, opts)
                        opts.inSampleSize = calculateInSampleSize(opts, THUMBNAIL_TARGET_PX, THUMBNAIL_TARGET_PX)
                        opts.inJustDecodeBounds = false
                        BitmapFactory.decodeFile(path, opts)
                    }.getOrNull()
                }
            }
        }
        remember(bitmap) {
            bitmap?.asImageBitmap()?.let { BitmapPainter(it) } ?: ColorPainter(Color.DarkGray)
        }
    }

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
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Delete affordance — only for user-uploaded backgrounds
        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .testTag(VideoEffectsTestTags.VIDEO_EFFECTS_DELETE_BACKGROUND_BUTTON)
                    .padding(DELETE_BUTTON_PADDING.dp)
                    .size(DELETE_BUTTON_SIZE.dp)
                    .background(
                        color = Color.Black.copy(alpha = DELETE_SCRIM_ALPHA),
                        shape = VonageVideoTheme.shapes.small,
                    ),
            ) {
                Icon(
                    imageVector = VividIcons.Line.Close,
                    contentDescription = stringResource(R.string.video_effects_delete_background_description),
                    tint = Color.White,
                    modifier = Modifier.size(DELETE_ICON_SIZE.dp),
                )
            }
        }
    }
}

@Composable
private fun AddBackgroundTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(THUMBNAIL_ASPECT_RATIO)
            .clip(VonageVideoTheme.shapes.medium)
            .background(VonageVideoTheme.colors.surface)
            .border(
                BorderStroke(VonageVideoTheme.dimens.borderWidthDefault, Color.Transparent),
                VonageVideoTheme.shapes.medium
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = VividIcons.Line.AddImage,
            contentDescription = stringResource(R.string.video_effects_add_background),
            tint = VonageVideoTheme.colors.secondary,
            modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
        )
    }
}

private const val THUMBNAIL_ASPECT_RATIO = 16f / 9f
/** Target pixel size used for inSampleSize calculation when decoding user-uploaded thumbnails. */
private const val THUMBNAIL_TARGET_PX = 256
private const val EFFECT_ICON_SIZE = 56
private const val GRID_COLUMNS = 3
private const val SELECTED_ALPHA = 0.12f
private const val DELETE_BUTTON_PADDING = 4
private const val DELETE_BUTTON_SIZE = 24
private const val DELETE_ICON_SIZE = 16
private const val DELETE_SCRIM_ALPHA = 0.5f

@Preview(showBackground = true)
@Composable
internal fun VideoEffectsScreenPortraitPreview() {
    VonageVideoTheme {
        VideoEffectsScreen(
            backgrounds = persistentListOf(
                VideoBackgroundItem(id = "bg1"),
                VideoBackgroundItem(id = "bg2"),
                VideoBackgroundItem(id = "bg3", isUserUploaded = true),
                VideoBackgroundItem(id = "bg4"),
                VideoBackgroundItem(id = "bg5"),
                VideoBackgroundItem(id = "bg6"),
            ),
            selectedEffect = VideoEffect.None,
            remainingBackgroundSlots = 7,
            onEffectSelect = {},
            onAddBackground = {},
            onDeleteBackground = {},
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
            remainingBackgroundSlots = 0,
            onEffectSelect = {},
            onAddBackground = {},
            onDeleteBackground = {},
        )
    }
}

/**
 * Calculates the largest power-of-2 [BitmapFactory.Options.inSampleSize] such that the decoded
 * bitmap is still at least [reqWidth] × [reqHeight] pixels. This avoids loading the full
 * high-resolution file into memory when only a small thumbnail is needed.
 */
private fun calculateInSampleSize(opts: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    var sampleSize = 1
    if (opts.outHeight > reqHeight || opts.outWidth > reqWidth) {
        val halfH = opts.outHeight / 2
        val halfW = opts.outWidth / 2
        while (halfH / sampleSize >= reqHeight && halfW / sampleSize >= reqWidth) {
            sampleSize *= 2
        }
    }
    return sampleSize
}
