package com.vonage.android.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme

/** Checkbox with a label; the whole row is the touch target and is announced as one checkbox. */
@Composable
fun VonageCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Checkbox(
            checked = checked,
            // Handled by the row's toggleable so the label is clickable too
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = VonageVideoTheme.colors.primary),
        )
        Text(
            text = label,
            style = VonageVideoTheme.typography.bodyBase,
            color = VonageVideoTheme.colors.onSurface,
        )
    }
}

@PreviewLightDark
@Composable
internal fun VonageCheckboxCheckedPreview() {
    VonageVideoTheme {
        VonageCheckbox(label = "Include AI summary", checked = true, onCheckedChange = {})
    }
}

@PreviewLightDark
@Composable
internal fun VonageCheckboxUncheckedPreview() {
    VonageVideoTheme {
        VonageCheckbox(label = "Include AI summary", checked = false, onCheckedChange = {})
    }
}
