package com.vonage.android.meetingroom.internal.factory

import android.content.Context
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.screensharing.di.ScreenSharingModule

internal fun createVonageScreenSharing(context: Context): VonageScreenSharing =
    ScreenSharingModule.provideVonageScreenSharing(context)
