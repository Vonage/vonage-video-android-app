package com.vonage.android.screen.goodbye

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.layout.TwoPaneScaffold
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.data.Archive
import com.vonage.android.data.ArchiveStatus.AVAILABLE
import com.vonage.android.data.ArchiveStatus.FAILED
import com.vonage.android.data.ArchiveStatus.PENDING
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.goodbye.components.ArchivesContainer
import com.vonage.android.screen.goodbye.components.GoodbyeScreenHeader
import com.vonage.android.screen.goodbye.components.RejoiningContainer
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GoodbyeScreen(
    actions: GoodbyeScreenActions,
    uiState: GoodbyeScreenUiState,
    modifier: Modifier = Modifier,
) {
    TwoPaneScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopBanner(onBack = actions.onReEnter) },
        firstPane = {
            GoodbyeScreenHeader(
                modifier = Modifier
                    .padding(VonageVideoTheme.dimens.paddingLarge)
                    .widthIn(0.dp, MAX_PANE_WIDTH.dp),
            )
        },
        secondPane = {
            Column(
                modifier = Modifier
                    .widthIn(0.dp, MAX_PANE_WIDTH.dp),
                verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
            ) {
                RejoiningContainer(
                    modifier = Modifier
                        .background(VonageVideoTheme.colors.surface, VonageVideoTheme.shapes.small)
                        .padding(VonageVideoTheme.dimens.paddingLarge),
                    actions = actions,
                )
                ArchivesContainer(
                    modifier = Modifier
                        .background(VonageVideoTheme.colors.surface, VonageVideoTheme.shapes.small)
                        .padding(VonageVideoTheme.dimens.paddingLarge),
                    actions = actions,
                    uiState = uiState,
                )
            }
        }
    )
}

private const val MAX_PANE_WIDTH = 550

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun GoodbyeScreenPreview() {
    VonageVideoTheme {
        GoodbyeScreen(
            actions = GoodbyeScreenActions(),
            uiState = GoodbyeScreenUiState.Content(
                archives = persistentListOf(
                    Archive(
                        id = "1",
                        name = "Recording 1",
                        url = "url",
                        status = AVAILABLE,
                        createdAt = 1231,
                    ),
                    Archive(
                        id = "2",
                        name = "Recording 2",
                        url = "url",
                        status = PENDING,
                        createdAt = 1231,
                    ),
                    Archive(
                        id = "3",
                        name = "Recording 3",
                        url = "url",
                        status = FAILED,
                        createdAt = 1231,
                    ),
                )
            ),
        )
    }
}
