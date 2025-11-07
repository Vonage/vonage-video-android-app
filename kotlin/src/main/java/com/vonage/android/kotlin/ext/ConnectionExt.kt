package com.vonage.android.kotlin.ext

import com.opentok.android.Connection
import com.vonage.android.kotlin.model.ParticipantState

fun Connection.extractSenderName(subs: Collection<ParticipantState>): String =
    subs
        .filter { it.id == connectionId }
        .map { it.name }
        .firstOrNull()
        .orEmpty()
