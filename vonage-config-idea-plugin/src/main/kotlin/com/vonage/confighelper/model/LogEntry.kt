package com.vonage.confighelper.model

import java.util.Date

data class LogEntry(
    val timestamp: Date,
    val message: String,
    val level: LogLevel = LogLevel.INFO
) {
    enum class LogLevel {
        INFO, ERROR, SUCCESS
    }
}
