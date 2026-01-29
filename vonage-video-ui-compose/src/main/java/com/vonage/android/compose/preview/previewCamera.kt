package com.vonage.android.compose.preview

import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.compose.R

@Composable
fun previewCamera(): View = ImageView(LocalContext.current)
    .apply {
        setImageResource(R.drawable.person)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }
