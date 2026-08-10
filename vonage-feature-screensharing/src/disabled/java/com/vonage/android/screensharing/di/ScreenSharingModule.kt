package com.vonage.android.screensharing.di

import com.vonage.android.screensharing.DisabledScreenSharing
import com.vonage.android.screensharing.VonageScreenSharing
import android.content.Context

object ScreenSharingModule {

    fun provideVonageScreenSharing(@Suppress("UNUSED_PARAMETER") context: Context): VonageScreenSharing =
        DisabledScreenSharing()

}
