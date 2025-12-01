package com.vonage.android.kotlin.ext

import com.opentok.android.Connection
import com.vonage.android.kotlin.model.Participant

internal fun Connection.extractSenderName(subs: Collection<Participant>): String =
    subs
        .filter { participant -> participant.connectionId == connectionId }
        .map { participant -> participant.name }
        .firstOrNull()
        .orEmpty()
