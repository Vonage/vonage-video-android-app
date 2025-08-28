package com.vonage.android.kotlin.ext

import com.opentok.android.Connection
import com.opentok.android.Subscriber

fun Connection.extractSenderName(subs: Collection<Subscriber>): String =
    subs
        .filter { it.stream.connection.connectionId == connectionId }
        .map { it.stream.name }
        .firstOrNull()
        .orEmpty()
