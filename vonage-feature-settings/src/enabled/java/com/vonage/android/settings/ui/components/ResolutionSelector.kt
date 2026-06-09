package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.settings.R
import kotlinx.collections.immutable.toImmutableList

private const val AUTO_LABEL = "Auto (device-optimal)"
private const val AUTO_DESCRIPTION = "Selects resolution based on device memory"
private const val DISABLED_ALPHA = 0.5f

@Composable
internal fun ResolutionSelector(
    selected: CaptureResolution?,
    onSelectionChange: (CaptureResolution?) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    helperText: String? = null,
) {
    val items = remember {
        buildList {
            add(
                DropdownItem(
                    value = null,
                    label = AUTO_LABEL,
                    description = AUTO_DESCRIPTION,
                ),
            )
            CaptureResolution.entries.forEach { resolution ->
                add(
                    DropdownItem(
                        value = resolution,
                        label = resolution.label,
                        description = resolution.description,
                    ),
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        DropdownSelector(
            title = stringResource(R.string.settings_resolution_title),
            selectedLabel = selected?.label ?: AUTO_LABEL,
            dropdownLabel = stringResource(R.string.settings_resolution_label),
            items = items.toImmutableList(),
            onSelectItem = if (enabled) onSelectionChange else { _ -> },
            modifier = Modifier.alpha(if (enabled) 1f else DISABLED_ALPHA),
        )

        helperText?.let {
            SettingHelperText(text = it)
        }
    }
}
