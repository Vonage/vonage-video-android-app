package com.vonage.android.archiving.di

import com.vonage.android.archiving.DisabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving

object ArchivingModule {

    fun provideVonageArchiving(): VonageArchiving =
        DisabledVonageArchiving()
}
