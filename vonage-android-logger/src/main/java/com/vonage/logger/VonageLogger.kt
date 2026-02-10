package com.vonage.logger

import com.vonage.logger.annotations.Debug
import com.vonage.logger.annotations.Error
import com.vonage.logger.annotations.Info
import com.vonage.logger.annotations.Log
import com.vonage.logger.annotations.Tag
import com.vonage.logger.annotations.Verbose
import com.vonage.logger.annotations.Warn
import com.vonage.logger.interceptor.LogInterceptor
import com.vonage.logger.interceptor.RealInterceptorChain
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class VonageLogger private constructor(
    private val interceptors: List<LogInterceptor>,
) {

    @Suppress("UNCHECKED_CAST")
    fun <T> create(service: Class<T>): T {
        require(service.isInterface) {
            "${service.name} must be an interface."
        }
        validateInterface(service)

        val tag = resolveTag(service)

        val handler = InvocationHandler { _, method, args ->
            // Handle standard Object methods (toString, equals, hashCode)
            when (method.name) {
                "toString" -> return@InvocationHandler "VonageLogger proxy for ${service.simpleName}"
                "hashCode" -> return@InvocationHandler System.identityHashCode(service)
                "equals" -> return@InvocationHandler args?.firstOrNull() === service
            }

            val level = resolveLevel(method)
            val params = args.orEmpty()
            val throwable = params.filterIsInstance<Throwable>().firstOrNull()
            val messageArgs = params.filter { it !is Throwable }
            val message = "${method.name} [${messageArgs.joinToString()}]"

            val event = LogEvent(
                level = level,
                tag = tag,
                message = message,
                throwable = throwable,
            )

            dispatchEvent(event)
        }

        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf(service),
            handler,
        ) as T
    }

    /**
     * Inline reified version of [create] for Kotlin convenience.
     *
     * ```kotlin
     * val logger: AppLogger = vonageLogger.create()
     * ```
     */
    inline fun <reified T> create(): T = create(T::class.java)

    private fun dispatchEvent(event: LogEvent): LogEvent {
        if (interceptors.isEmpty()) return event
        val chain = RealInterceptorChain(interceptors, index = 0, event)
        return chain.proceed(event)
    }

    private fun validateInterface(service: Class<*>) {
        service.methods.forEach { method ->
            // Skip Object methods
            if (method.declaringClass == Object::class.java) return@forEach
            val level = method.resolveLevelOrNull()
            requireNotNull(level) {
                "Method ${service.simpleName}.${method.name}() must be annotated with " +
                        "@Log, @Verbose, @Debug, @Info, @Warn, or @Error."
            }
        }
    }

    private fun resolveTag(service: Class<*>): String {
        val tagAnnotation = service.getAnnotation(Tag::class.java)
        return tagAnnotation?.value ?: service.simpleName
    }

    private fun resolveLevel(method: Method): LogLevel =
        method.resolveLevelOrNull() ?: throw IllegalStateException(
            "Method ${method.name} has no log-level annotation."
        )

    private fun Method.resolveLevelOrNull(): LogLevel? {
        getAnnotation(Log::class.java)?.let { return it.level }
        if (isAnnotationPresent(Verbose::class.java)) return LogLevel.VERBOSE
        if (isAnnotationPresent(Debug::class.java)) return LogLevel.DEBUG
        if (isAnnotationPresent(Info::class.java)) return LogLevel.INFO
        if (isAnnotationPresent(Warn::class.java)) return LogLevel.WARN
        if (isAnnotationPresent(Error::class.java)) return LogLevel.ERROR
        return null
    }

    /**
     * ```kotlin
     * val logger = VonageLogger.Builder()
     *     .addInterceptor(LogLevelFilterInterceptor(LogLevel.DEBUG))
     *     .addInterceptor(AndroidLogInterceptor())
     *     .build()
     * ```
     */
    class Builder {
        private val interceptors = mutableListOf<LogInterceptor>()

        fun addInterceptor(interceptor: LogInterceptor): Builder = apply {
            interceptors.add(interceptor)
        }

        fun build(): VonageLogger = VonageLogger(interceptors.toList())
    }
}
