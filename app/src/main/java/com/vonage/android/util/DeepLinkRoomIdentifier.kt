package com.vonage.android.util

import android.net.Uri
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.io.encoding.Base64

private const val JWT_PARTS = 3
private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

/**
 * Resolves the room identifier of a `/room/{id}` or `/waiting-room/{id}` link to a room name.
 *
 * Web links carry the backend session key (a JWT) instead of the room name. The room name is
 * read from its `roomName` claim (signature not verified) so the normal `createSession` flow,
 * including the expiration check, runs on a fresh key. Keeping the JWT out of the route also
 * keeps the session key out of nav args and the saved back stack.
 *
 * Returns [identifier] unchanged when it is not JWT-shaped (legacy room-name links), or `null`
 * when it is JWT-shaped but has no readable `roomName` claim.
 */
internal fun resolveDeepLinkRoomName(identifier: String): String? {
    val parts = identifier.split('.')
    if (parts.size != JWT_PARTS || parts.any(String::isEmpty)) return identifier
    return runCatching {
        val payload = Json.parseToJsonElement(base64Url.decode(parts[1]).decodeToString()).jsonObject
        (payload["roomName"] as? JsonPrimitive)?.takeIf { it.isString }?.content
    }.getOrNull()?.takeIf(String::isNotBlank)
}

/**
 * Rewrites a session-key deep link (`/room/<jwt>`) to its room-name form (`/room/<roomName>`).
 * Any other URI is returned unchanged.
 */
internal fun Uri.withRoomNameFromSessionKey(): Uri {
    val segments = pathSegments
    if (segments.size != 2 || segments[0] !in DEEP_LINK_ROOM_PATHS) return this
    val roomName = resolveDeepLinkRoomName(segments[1])
    if (roomName == null || roomName == segments[1]) return this
    return buildUpon().path(null).appendPath(segments[0]).appendPath(roomName).build()
}

private val DEEP_LINK_ROOM_PATHS = setOf("room", "waiting-room")
