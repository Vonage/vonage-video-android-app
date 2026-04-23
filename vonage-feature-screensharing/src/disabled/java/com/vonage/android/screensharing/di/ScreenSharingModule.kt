package com.vonage.android.screensharing.di

import com.vonage.android.screensharing.DisabledScreenSharing
import com.vonage.android.screensharing.VonageScreenSharing

object ScreenSharingModule {

    fun provideVonageScreenSharing(): VonageScreenSharing =
        DisabledScreenSharing()

}
