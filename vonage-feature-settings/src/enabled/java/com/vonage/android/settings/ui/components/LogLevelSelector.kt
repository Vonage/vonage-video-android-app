package com.vonage.android.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.settings.R
import com.vonage.logger.LogLevel
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun LogLevelSelector(
    selected: LogLevel,
    onSelectionChange: (LogLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        LogLevel.entries.map { level ->
            DropdownItem(
                value = level,
                label = level.name,
                description = null,
            )
        }
    }

    DropdownSelector(
        title = stringResource(R.string.settings_log_level_title),
        selectedLabel = selected.name,
        dropdownLabel = stringResource(R.string.settings_log_level_label),
        items = items.toImmutableList(),
        onSelectItem = onSelectionChange,
        modifier = modifier,
    )
}

