package com.vonage.logger.interceptor

import com.vonage.logger.LogEvent
import java.io.IOException
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class HttpLogInterceptor(
    private val url: String,
    private val executor: ExecutorService = Executors.newSingleThreadExecutor(),
    private val connectTimeout: Int = DEFAULT_TIMEOUT,
    private val readTimeout: Int = DEFAULT_TIMEOUT,
) : LogInterceptor {

    override fun intercept(chain: LogInterceptor.Chain): LogEvent {
        val event = chain.event()
        executor.submit { postEvent(event) }
        return chain.proceed(event)
    }

    /**
     * Visible-for-testing variant that performs the POST synchronously
     * on the calling thread and returns the HTTP response code.
     *
     * @return the HTTP status code, or `-1` if the request failed.
     */
    internal fun postEventSync(event: LogEvent): Int {
        return postEvent(event)
    }

    private fun postEvent(event: LogEvent): Int {
        var connection: HttpsURLConnection? = null
        return try {
            val json = eventToJson(event)
            val jsonBytes = json.toByteArray(Charsets.UTF_8)

            connection = (URL(url).openConnection() as HttpsURLConnection).apply {
                requestMethod = POST_METHOD
                setRequestProperty(HEADER_ACCEPT, CONTENT_TYPE_JSON)
                setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                connectTimeout = this@HttpLogInterceptor.connectTimeout
                readTimeout = this@HttpLogInterceptor.readTimeout
                doOutput = true
            }

            connection.outputStream.use { output ->
                output.write(jsonBytes)
                output.flush()
            }

            connection.responseCode
        } catch (_: IOException) {
            FAILURE_CODE
        } finally {
            connection?.disconnect()
        }
    }

    internal companion object {
        const val POST_METHOD = "POST"
        const val HEADER_ACCEPT = "Accept"
        const val HEADER_CONTENT_TYPE = "Content-type"
        const val CONTENT_TYPE_JSON = "application/json"
        const val DEFAULT_TIMEOUT = 5_000
        const val FAILURE_CODE = -1

        private const val KEY_LEVEL = "level"
        private const val KEY_TAG = "tag"
        private const val KEY_MESSAGE = "message"
        private const val KEY_THROWABLE = "throwable"

        /**
         * Serialises a [LogEvent] to a JSON string.
         *
         * Add a 3rd party json serialization library
         */
        internal fun eventToJson(event: LogEvent): String {
            return buildString {
                append('{')
                appendJsonKey(KEY_LEVEL, event.level.name)
                append(',')
                appendJsonKey(KEY_TAG, event.tag)
                append(',')
                appendJsonKey(KEY_MESSAGE, event.message)
                event.throwable?.let {
                    append(',')
                    appendJsonKey(KEY_THROWABLE, it.toString())
                }
                append('}')
            }
        }

        private fun StringBuilder.appendJsonKey(key: String, value: String) {
            append('"').append(escapeJson(key)).append('"')
            append(':')
            append('"').append(escapeJson(value)).append('"')
        }

        private fun escapeJson(value: String): String {
            return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
        }
    }
}
