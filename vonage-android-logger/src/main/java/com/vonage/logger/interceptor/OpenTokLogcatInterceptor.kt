package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import com.vonage.logger.LogLevel
import com.vonage.logger.VonageLogger
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Captures log lines emitted by the OpenTok SDK directly to Logcat and re-routes
 * them through the [VonageLogger] pipeline so they are handled by every registered
 * [LogInterceptor] (e.g. written to file, forwarded to a remote sink, etc.).
 *
 * OpenTok does not expose a log-listener API; its internal classes write straight to
 * `android.util.Log` under the tags listed in [OPENTOK_TAGS]. This class spawns a
 * background `logcat` process filtered to those tags, reads each line, parses the
 * standard brief logcat format, and forwards a [LogEvent] to the supplied [logger].
 *
 * Usage – call [start] once at app startup (e.g. in `Application.onCreate`) and
 * [stop] when the process is about to terminate:
 *
 * ```kotlin
 * val openTokLogInterceptor = OpenTokLogcatInterceptor(vonageLogger)
 * openTokLogInterceptor.start()
 * ```
 *
 * @param logger   The [VonageLogger] instance that receives the forwarded events.
 * @param tags     The set of Logcat tags to capture. Defaults to [OPENTOK_TAGS].
 * @param minLogLevel Minimum OpenTok log level to capture. Defaults to [LogLevel.VERBOSE].
 */
class OpenTokLogcatInterceptor(
    private val logger: VonageLogger,
    private val tags: Set<String> = OPENTOK_TAGS,
    private val minLogLevel: LogLevel = LogLevel.VERBOSE,
) {

    @Volatile
    private var process: Process? = null

    @Volatile
    private var readerThread: Thread? = null

    /**
     * Starts reading OpenTok logcat output in a background daemon thread.
     * Safe to call multiple times – subsequent calls are no-ops while already running.
     */
    fun start() {
        if (readerThread?.isAlive == true) return

        val minPriority = logLevelToPriorityChar(minLogLevel)
        val tagFilter = tags.joinToString(separator = " ") { "$it:$minPriority" }
        // Suppress all other tags, then add our targets at the selected minimum level.
        val command = arrayOf("logcat", "-v", "brief", "-s", tagFilter)

        readerThread = Thread({
            try {
                val proc = Runtime.getRuntime().exec(command)
                process = proc
                BufferedReader(InputStreamReader(proc.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        parseLine(line!!)?.let { event -> logger.log(event.level, event.tag, event.message, event.throwable) }
                    }
                }
            } catch (_: Exception) {
                // Silently stop — logging must never crash the app.
            }
        }, THREAD_NAME).also { it.isDaemon = true; it.start() }
    }

    /**
     * Stops the background reader and destroys the logcat process.
     */
    fun stop() {
        process?.destroy()
        process = null
        readerThread?.interrupt()
        readerThread = null
    }

    /**
     * Parses a single logcat line in brief format:
     * `D/SomeTag( PID): message`
     */
    internal fun parseLine(line: String): LogEvent? {
        val match = BRIEF_PATTERN.matchEntire(line.trim()) ?: return null
        val (levelChar, tag, message) = match.destructured
        val level = levelCharToLogLevel(levelChar.firstOrNull()) ?: return null
        if (!shouldLog(level)) return null
        return LogEvent(level = level, tag = tag.trim(), message = message)
    }

    private fun shouldLog(level: LogLevel): Boolean = level.ordinal >= minLogLevel.ordinal

    private fun levelCharToLogLevel(char: Char?): LogLevel? = when (char) {
        'V' -> LogLevel.VERBOSE
        'D' -> LogLevel.DEBUG
        'I' -> LogLevel.INFO
        'W' -> LogLevel.WARN
        'E' -> LogLevel.ERROR
        'F' -> LogLevel.ERROR
        else -> null
    }

    private fun logLevelToPriorityChar(level: LogLevel): Char = when (level) {
        LogLevel.VERBOSE -> 'V'
        LogLevel.DEBUG -> 'D'
        LogLevel.INFO -> 'I'
        LogLevel.WARN -> 'W'
        LogLevel.ERROR -> 'E'
    }

    companion object {
        private const val THREAD_NAME = "opentok-logcat-reader"

        // Matches:  D/TagName(  123): the message text
        private val BRIEF_PATTERN = Regex("""^([VDIWEF])/([^(]+)\(\s*\d+\):\s*(.*)$""")

        /**
         * Known Logcat tags used by the OpenTok Android SDK.
         * Covers OTKit, WebRTC, and JNI layers.
         */
        val OPENTOK_TAGS: Set<String> = setOf(
            "OTK",
            "OTKAnalytics",
            "OTKCamera",
            "OTKSession",
            "OTKStream",
            "OTKPublisher",
            "OTKSubscriber",
            "OTKAudioDevice",
            "OTKVideoRenderer",
            "OTKSignal",
            "OTKConnection",
            "OTKArchive",
            "OTKScreenSharing",
            "opentok",
            "OpenTok",
            "OT",
            "libjingle",
            "webrtc",
            "WebRTC",
        )
    }
}

