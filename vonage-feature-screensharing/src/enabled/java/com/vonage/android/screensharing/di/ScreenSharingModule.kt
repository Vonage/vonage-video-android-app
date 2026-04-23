package com.vonage.android.screensharing.di

import android.content.Context
import com.vonage.android.screensharing.EnabledScreenSharing
import com.vonage.android.screensharing.VonageScreenSharing

object ScreenSharingModule {

    fun provideVonageScreenSharing(
        context: Context
    ): VonageScreenSharing =
        EnabledScreenSharing(
            context = context.applicationContext,
        )

}
