package com.vonage.android.screensharing

import android.app.NotificationManager
import android.content.Intent
import com.vonage.android.kotlin.model.CallFacade

/**
 * Abstraction for starting and stopping screen sharing during a call.
 */
interface VonageScreenSharing {

    /**
     * Starts screen sharing using a MediaProjection [Intent].
     *
     * @param intent MediaProjection permission intent obtained from the system.
     * @param call Active [CallFacade] to start screen capture on.
     * @param onStarted Callback invoked when screen sharing has started.
     * @param onStopped Callback invoked when screen sharing has stopped.
     */
    fun startScreenSharing(
        intent: Intent,
        call: CallFacade,
        onStarted: () -> Unit,
        onStopped: () -> Unit,
    )

    /**
     * Stops screen sharing and releases any resources held by the implementation.
     */
    fun stopSharingScreen()

    /**
     * Returns `true` when the app has the `SYSTEM_ALERT_WINDOW` permission
     * needed to render the screen-sharing border overlay.
     */
    fun canDrawOverlays(): Boolean

    /**
     * Shows a coloured border overlay to remind the user that screen sharing
     * is in progress. Requires [canDrawOverlays] to return `true`.
     */
    fun showOverlay()

    /**
     * Hides the screen-sharing border overlay.
     */
    fun hideOverlay()

    /**
     * Creates a notification channel required for screen sharing foreground service.
     *
     * @param manager [NotificationManager] used to register the channel.
     */
    fun createNotificationChannel(manager: NotificationManager)

}
