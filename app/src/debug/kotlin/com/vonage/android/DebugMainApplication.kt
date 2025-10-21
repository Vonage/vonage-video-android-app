package com.vonage.android

import com.telefonica.tweaks.Tweaks
import com.telefonica.tweaks.domain.tweaksGraph
import kotlinx.coroutines.flow.flowOf

class DebugMainApplication : MainApplication() {

    override fun onCreate() {
        super.onCreate()
        Tweaks.init(this, demoTweakGraph())
    }

    private fun demoTweakGraph() = tweaksGraph {
        cover("Vonage Runtime Config") {
            label("Build type:") { flowOf(BuildConfig.BUILD_TYPE) }
            label("Version:") { flowOf(BuildConfig.VERSION_NAME) }
            label("API:") { flowOf(BuildConfig.BASE_API_URL) }

            editableBoolean(
                key = "chat.enabled",
                name = "Chat",
                defaultValue = true,
            )
        }
    }
}
