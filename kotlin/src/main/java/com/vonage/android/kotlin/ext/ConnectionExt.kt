package com.vonage.android.kotlin.ext

import com.opentok.android.Connection
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.ParticipantState

fun Connection.extractSenderName(subs: Collection<Participant>): String =
    subs
        .filter { it.id == connectionId }
        .map { it.name }
        .firstOrNull()
        .orEmpty()
