package com.vonage.android.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FrameRateSelector(
    selected: CaptureFrameRate,
    onSelectionChange: (CaptureFrameRate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall),
    ) {
        Text(
            text = stringResource(R.string.settings_frame_rate_title),
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.secondary,
        )

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        Text(
            text = stringResource(R.string.settings_frame_rate_note),
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.tertiary,
        )

        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceXSmall))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selected.label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
                        text = stringResource(R.string.settings_frame_rate_label),
                        style = VonageVideoTheme.typography.caption,
                    )
                },
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = VonageVideoTheme.colors.surface,
            ) {
                CaptureFrameRate.entries.forEach { frameRate ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = frameRate.label,
                                style = VonageVideoTheme.typography.bodyBaseSemibold,
                                color = VonageVideoTheme.colors.secondary,
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelectionChange(frameRate)
                        },
                    )
                }
            }
        }
    }
}
