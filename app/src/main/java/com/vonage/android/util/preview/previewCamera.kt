package com.vonage.android.util.preview

import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.vonage.android.R

@Composable
fun previewCamera(): View = ImageView(LocalContext.current)
    .apply {
        setImageResource(R.drawable.person)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }
