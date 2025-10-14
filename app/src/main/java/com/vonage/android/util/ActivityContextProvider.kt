package com.vonage.android.util

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides access to the current Activity context in a safe way for ViewModels.
 * This is a better alternative to directly injecting @ActivityContext into ViewModels,
 * which is not supported by Hilt's ViewModel component scope.
 */
@Singleton
class ActivityContextProvider @Inject constructor() {
    
    private var currentActivityContext: Context? = null
    
    fun setActivityContext(context: Context) {
        currentActivityContext = context
    }
    
    fun getActivityContext(): Context? = currentActivityContext
    
    fun requireActivityContext(): Context = 
        currentActivityContext ?: error("Activity context not set")
    
    fun clearActivityContext() {
        currentActivityContext = null
    }
}