package com.vonage.android.screen.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.util.linkify.Linkify

@Composable
fun LinkifyText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val layout = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }
    val linksList = Linkify.extract(text)
    val annotatedString = buildAnnotatedString {
        append(text)
        linksList.forEach {
            addStyle(
                style = SpanStyle(
                    color = VonageVideoTheme.colors.primary,
                    textDecoration = TextDecoration.Underline
                ),
                start = it.start,
                end = it.end
            )
            addStringAnnotation(
                tag = it.tag.name,
                annotation = it.url,
                start = it.start,
                end = it.end
            )
        }
    }

    Text(
        text = annotatedString,
        color = VonageVideoTheme.colors.inverseSurface,
        style = VonageVideoTheme.typography.body,
        onTextLayout = { layout.value = it },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    layout.value?.let { layoutResult ->
                        val position = layoutResult.getOffsetForPosition(offset)
                        annotatedString
                            .getStringAnnotations(position, position)
                            .firstOrNull()?.let { uriHandler.openUri(it.item) }
                    }
                }
            }
    )
}

@PreviewLightDark
@Composable
internal fun LinkifyTextPreview() {
    VonageVideoTheme {
        LinkifyText(
            text = """
                https://www.vonage.com
                support@vonage.com
            """.trimIndent(),
        )
    }
}
