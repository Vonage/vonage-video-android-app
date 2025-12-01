package com.vonage.android.kotlin.ext

import com.opentok.android.Connection
import com.vonage.android.kotlin.model.Participant

/**
 * Extracts the display name of a participant based on their connection ID.
 *
 * Searches through the provided participants collection to find a match by connection ID
 * and returns their name. Used when processing signals to identify the sender.
 *
 * @param subs Collection of participants to search through
 * @return The participant's display name, or empty string if not found
 */
fun Connection.extractSenderName(subs: Collection<Participant>): String =
    subs
        .filter { it.id == connectionId }
        .map { it.name }
        .firstOrNull()
        .orEmpty()
