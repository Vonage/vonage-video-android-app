package com.vonage.android.chat.ui

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.ChevronDown

private enum class Visibility {
    VISIBLE,
    GONE,
}

private const val BOTTOM_OFFSET = 32

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun JumpToBottom(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(
        targetState = if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "JumpToBottom visibility animation",
    )
    val bottomOffset = transition.animateDp(label = "JumpToBottom offset animation") {
        if (it == Visibility.GONE) {
            (-BOTTOM_OFFSET).dp
        } else {
            BOTTOM_OFFSET.dp
        }
    }
    // Composition only reacts to the visibility flip; the offset itself is read during placement.
    val isVisible by remember { derivedStateOf { bottomOffset.value > 0.dp } }
    if (isVisible) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    imageVector = VividIcons.Solid.ChevronDown,
                    modifier = Modifier.height(18.dp),
                    contentDescription = null,
                )
            },
            text = {
                Text(text = label)
            },
            onClick = onClick,
            containerColor = VonageVideoTheme.colors.primary,
            contentColor = VonageVideoTheme.colors.textSecondary,
            modifier = modifier
                .offset { IntOffset(x = 0, y = -bottomOffset.value.roundToPx()) }
                .height(36.dp),
        )
    }
}

@Preview
@Composable
internal fun JumpToBottomPreview() {
    JumpToBottom(
        label = "Jump to bottom preview",
        enabled = true,
        onClick = {},
    )
}
