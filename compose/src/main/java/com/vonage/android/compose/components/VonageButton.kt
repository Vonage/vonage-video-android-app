package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.icons.VideoCameraIcon
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun VonageButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = VonageVideoTheme.colors.buttonPrimary,
        ),
        enabled = enabled,
    ) {
        leadingIcon?.let {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@PreviewLightDark
@Composable
internal fun VonageButtonPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(16.dp)
        ) {
            VonageButton(
                text = "Button label",
                onClick = {},
                leadingIcon = {
                    VideoCameraIcon()
                }
            )
        }
    }
}
