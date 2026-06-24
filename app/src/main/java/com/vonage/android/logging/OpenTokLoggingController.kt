package com.vonage.android.logging

import com.opentok.android.OpenTokConfig
import com.vonage.logger.LogLevel
import com.vonage.logger.VonageLogger
import com.vonage.logger.interceptor.OpenTokLogcatInterceptor
import com.vonage.logger.vonageLogger

/**
 * Keeps OpenTok SDK log emission and interception aligned with app logging settings.
 */
object OpenTokLoggingController {

    private val lock = Any()

    @Volatile
    private var interceptor: OpenTokLogcatInterceptor? = null

    @Volatile
    private var currentMinLogLevel: LogLevel? = null

    fun apply(
        enabled: Boolean,
        minLogLevel: LogLevel,
        logger: VonageLogger = vonageLogger,
    ) {
        synchronized(lock) {
            if (!enabled) {
                setOpenTokLogsEnabled(false)
                interceptor?.stop()
                interceptor = null
                currentMinLogLevel = null
                return
            }

            setOpenTokLogsEnabled(true)

            if (interceptor != null && currentMinLogLevel == minLogLevel) return

            interceptor?.stop()
            interceptor = OpenTokLogcatInterceptor(
                logger = logger,
                minLogLevel = minLogLevel,
            ).also { it.start() }
            currentMinLogLevel = minLogLevel
        }
    }

    private fun setOpenTokLogsEnabled(enabled: Boolean) {
        OpenTokConfig.setWebRTCLogs(enabled)
        OpenTokConfig.setOTKitLogs(enabled)
        OpenTokConfig.setJNILogs(enabled)
    }
}
