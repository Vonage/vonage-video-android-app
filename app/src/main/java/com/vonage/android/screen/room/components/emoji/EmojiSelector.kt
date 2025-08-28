package com.vonage.android.screen.room.components.emoji

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.util.ext.debouncedClickable

private const val DEBOUNCE_CLICK_MILLIS = 500L

@Composable
fun EmojiSelector(
    onEmojiClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
    ) {
        items(
            items = EmojiData.emojis,
            key = { emoji -> emoji },
        ) { emoji ->
            Text(
                text = emoji,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .debouncedClickable(
                        debounceTime = DEBOUNCE_CLICK_MILLIS, // debounce 500 milliseconds
                    ) { onEmojiClick(emoji) }
            )
        }
    }
}

@Preview
@Composable
internal fun EmojiSelectorPreview() {
    VonageVideoTheme {
        EmojiSelector(
            onEmojiClick = {},
        )
    }
}
