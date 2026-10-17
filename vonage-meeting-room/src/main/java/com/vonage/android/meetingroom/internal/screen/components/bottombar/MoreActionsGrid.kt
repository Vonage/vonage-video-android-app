package com.vonage.android.meetingroom.internal.screen.components.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.components.bottombar.ActionCell
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.theme.VonageVideoTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun MoreActionsGrid(
    modifier: Modifier = Modifier,
    actions: ImmutableList<BottomBarAction> = persistentListOf(),
) {
    LazyVerticalGrid(
        modifier = modifier
            .padding(bottom = VonageVideoTheme.dimens.paddingLarge),
        contentPadding = PaddingValues(VonageVideoTheme.dimens.paddingSmall),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall),
    ) {
        itemsIndexed(
            items = actions,
            // CUSTOM is shared by reporting and every host action, including On-the-Go.
            key = { index, action -> "${action.type.name}:$index:${action.label}" },
        ) { _, action ->
            ActionCell(
                icon = action.icon,
                label = action.label,
                isSelected = action.isSelected,
                onClickCell = action.onClick,
                badgeCount = action.badgeCount,
            )
        }
    }
}
