package com.vonage.logger

data class LogEvent(
    val level: LogLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val thread: String = Thread.currentThread().name,
)
