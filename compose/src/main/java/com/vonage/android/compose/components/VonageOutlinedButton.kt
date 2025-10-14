package com.vonage.android.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = VonageVideoTheme.colors.buttonPrimary,
        ),
        contentPadding = PaddingValues(12.dp),
        border = BorderStroke(1.dp, VonageVideoTheme.colors.buttonPrimary),
        enabled = enabled,
    ) {
        leadingIcon?.let {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = VonageVideoTheme.colors.buttonPrimary,
            style = VonageVideoTheme.typography.body,
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
                .padding(16.dp)
        ) {
            VonageOutlinedButton(
                text = "Button label",
                onClick = {},
            )
        }
    }
}
