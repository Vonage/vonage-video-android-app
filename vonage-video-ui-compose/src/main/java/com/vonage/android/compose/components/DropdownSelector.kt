package com.vonage.android.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import kotlinx.collections.immutable.ImmutableList

/**
 * A reusable themed dropdown selector.
 *
 * Renders a title, optional note, a Material 3 [ExposedDropdownMenuBox], an optional
 * description below the dropdown, and optional extra trailing content (e.g. a slider).
 *
 * @param T The type of the backing value
 * @param title Section heading above the dropdown
 * @param selectedLabel Text shown inside the closed dropdown field
 * @param dropdownLabel Label floating inside the text field
 * @param items List of selectable options
 * @param onSelectItem Callback when the user picks an item
 * @param modifier Modifier for the root column
 * @param note Optional helper text shown between the title and dropdown
 * @param selectedDescription Optional description shown below the dropdown
 * @param extraContent Optional composable rendered after the description (e.g. a slider)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun <T> DropdownSelector(
    title: String,
    selectedLabel: String,
    dropdownLabel: String,
    items: ImmutableList<DropdownItem<T>>,
    onSelectItem: (T) -> Unit,
    modifier: Modifier = Modifier,
    note: String? = null,
    selectedDescription: String? = null,
    extraContent: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        Text(
            text = title,
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.secondary,
        )

        note?.let {
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            Text(
                text = note,
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.tertiary,
            )
        }

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        DropdownMenu(
            expanded = expanded,
            selectedLabel = selectedLabel,
            dropdownLabel = dropdownLabel,
            items = items,
            onSelectItem = onSelectItem,
        )

        selectedDescription?.let {
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))
            Text(
                text = selectedDescription,
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.tertiary,
            )
        }

        extraContent?.let {
            extraContent()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun <T> DropdownMenu(
    expanded: Boolean,
    selectedLabel: String,
    dropdownLabel: String,
    items: ImmutableList<DropdownItem<T>>,
    onSelectItem: (T) -> Unit
) {
    var expanded1 = expanded
    ExposedDropdownMenuBox(
        expanded = expanded1,
        onExpandedChange = { expanded1 = it },
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VonageVideoTheme.colors.primary,
                unfocusedBorderColor = VonageVideoTheme.colors.border,
                focusedTextColor = VonageVideoTheme.colors.secondary,
                unfocusedTextColor = VonageVideoTheme.colors.secondary,
                focusedTrailingIconColor = VonageVideoTheme.colors.primary,
                unfocusedTrailingIconColor = VonageVideoTheme.colors.tertiary,
            ),
            textStyle = VonageVideoTheme.typography.bodyBaseSemibold,
            label = {
                Text(
                    text = dropdownLabel,
                    style = VonageVideoTheme.typography.caption,
                )
            },
        )

        ExposedDropdownMenu(
            expanded = expanded1,
            onDismissRequest = { expanded1 = false },
            containerColor = VonageVideoTheme.colors.surface,
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        if (item.description != null) {
                            Column {
                                Text(
                                    text = item.label,
                                    style = VonageVideoTheme.typography.bodyBaseSemibold,
                                    color = VonageVideoTheme.colors.secondary,
                                )
                                Text(
                                    text = item.description,
                                    style = VonageVideoTheme.typography.caption,
                                    color = VonageVideoTheme.colors.tertiary,
                                )
                            }
                        } else {
                            Text(
                                text = item.label,
                                style = VonageVideoTheme.typography.bodyBaseSemibold,
                                color = VonageVideoTheme.colors.secondary,
                            )
                        }
                    },
                    onClick = {
                        expanded1 = false
                        onSelectItem(item.value)
                    },
                )
            }
        }
    }
}

/**
 * Represents a single item in a [DropdownSelector].
 *
 * @param T The type of the backing value
 * @property value The underlying value returned on selection
 * @property label Primary text displayed for the item
 * @property description Optional secondary text shown below the label
 */
data class DropdownItem<out T>(
    val value: T,
    val label: String,
    val description: String? = null,
)
