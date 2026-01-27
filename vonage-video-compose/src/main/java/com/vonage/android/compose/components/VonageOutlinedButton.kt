package com.vonage.android.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun VonageOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedButton(
        onClick = onClick,
        shape = VonageVideoTheme.shapes.medium,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = VonageVideoTheme.colors.primary,
        ),
        contentPadding = PaddingValues(VonageVideoTheme.dimens.paddingMedium),
        border = BorderStroke(
            width = VonageVideoTheme.dimens.borderWidthThin,
            color = VonageVideoTheme.colors.primary,
        ),
        enabled = enabled,
    ) {
        leadingIcon?.let {
            leadingIcon()
            Spacer(modifier = Modifier.width(VonageVideoTheme.dimens.spaceSmall))
        }
        Text(
            text = text,
            color = VonageVideoTheme.colors.primary,
            style = VonageVideoTheme.typography.bodyBase,
        )
    }
}

@PreviewLightDark
@Composable
internal fun VonageOutlinedButtonPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(VonageVideoTheme.dimens.paddingDefault)
        ) {
            VonageOutlinedButton(
                text = "Button label",
                onClick = {},
            )
        }
    }
}
