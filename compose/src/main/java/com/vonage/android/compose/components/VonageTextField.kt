package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.icons.KeyboardIcon
import com.vonage.android.compose.theme.VonageVideoTheme

@Suppress("LongParameterList")
@Composable
fun VonageTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    maxLength: Int = 1024,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            if (it.length <= maxLength) {
                onValueChange(it)
            }
        },
        isError = isError,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VonageVideoTheme.colors.buttonPrimary,
            unfocusedBorderColor = VonageVideoTheme.colors.buttonPrimaryDisabled,
        ),
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        supportingText = supportingText,
    )
}

@PreviewLightDark
@Composable
internal fun VonageTextFieldPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(16.dp)
        ) {
            VonageTextField(
                value = "user name",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    KeyboardIcon()
                },
            )
        }
    }
}
