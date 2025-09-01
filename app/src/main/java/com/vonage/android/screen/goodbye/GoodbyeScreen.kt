package com.vonage.android.screen.goodbye

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.data.Archive
import com.vonage.android.data.ArchiveStatus.AVAILABLE
import com.vonage.android.data.ArchiveStatus.FAILED
import com.vonage.android.data.ArchiveStatus.PENDING
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.goodbye.components.ArchivesContainer
import com.vonage.android.screen.goodbye.components.GoodbyeScreenHeader
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GoodbyeScreen(
    actions: GoodbyeScreenActions,
    uiState: GoodbyeScreenUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBanner(
                onBack = actions.onReEnter,
            )
        }
    ) { paddingValues ->
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = spacedBy(48.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxSize()
                .background(VonageVideoTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .consumeWindowInsets(paddingValues)
                .padding(horizontal = 24.dp),
        ) {
            GoodbyeScreenHeader(
                modifier = Modifier
                    .widthIn(0.dp, 380.dp),
                actions = actions,
            )
            ArchivesContainer(
                modifier = Modifier
                    .widthIn(0.dp, 320.dp),
                actions = actions,
                uiState = uiState,
            )
        }
    }
}

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
