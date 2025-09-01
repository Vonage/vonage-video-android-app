package com.vonage.android.screen.room.components.captions

import android.R
import android.graphics.fonts.FontFamily
import android.graphics.fonts.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun CaptionsOverlay(captions: String?) {
    Box(
        modifier = Modifier
            .zIndex(12f)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        captions?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = it,
                    color = Color.White,
                    fontSize = 14.sp,
                    overflow = TextOverflow.StartEllipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun LimitedText(
    text: String,
    maxBottomLines: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    SubcomposeLayout(modifier) { constraints ->
        var slotId = 0
        fun placeText(
            text: String,
            onTextLayout: (TextLayoutResult) -> Unit,
            constraints: Constraints,
        ) = subcompose(slotId++) {
            Text(
                text = text,
                onTextLayout = onTextLayout,
                maxLines = 2,
            )
//            Text(
//                text = text,
//                color = color,
//                fontSize = fontSize,
//                fontStyle = fontStyle,
//                fontWeight = fontWeight,
//                fontFamily = fontFamily,
//                letterSpacing = letterSpacing,
//                textDecoration = textDecoration,
//                textAlign = textAlign,
//                lineHeight = lineHeight,
//                overflow = overflow,
//                softWrap = softWrap,
//                onTextLayout = onTextLayout,
//                style = style,
//                modifier = Modifier,
//                maxLines = 3,
//                minLines = 1,
//            )
        }[0].measure(constraints)
        var substringStartIndex: Int? = null
        val initialPlaceable = placeText(
            text = text,
            // override maxWidth to get full text size
            constraints = constraints.copy(maxHeight = Int.MAX_VALUE),
            onTextLayout = { textLayoutResult ->
                val extraLines = textLayoutResult.lineCount - maxBottomLines
                if (extraLines > 0) {
                    substringStartIndex = textLayoutResult.run {
                        getOffsetForPosition(
                            Offset(1f, getLineTop(extraLines) + 1f)
                        )
                    }
                } else {
                    onTextLayout(textLayoutResult)
                }
            },
        )
        val finalPlaceable = substringStartIndex?.let {
            placeText(
                text = text.substring(startIndex = it),
                constraints = constraints,
                onTextLayout = onTextLayout,
            )
        } ?: initialPlaceable

        layout(
            width = finalPlaceable.width,
            height = finalPlaceable.height
        ) {
            finalPlaceable.place(0, 0)
        }
    }
}

@Preview
@Composable
internal fun LimitedTextPreview() {
    VonageVideoTheme {
        LimitedText(
            text = "12312312312313123 1231231 23 12 3123 asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a asdfasdf asdf asdf a ",
            maxBottomLines = 2,
        )
    }
}
