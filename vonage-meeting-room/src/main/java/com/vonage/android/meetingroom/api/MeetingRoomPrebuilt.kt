package com.vonage.android.meetingroom.api

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.vonage.android.meetingroom.internal.MeetingRoomActivity
import com.vonage.android.meetingroom.internal.MeetingRoomContent
import com.vonage.android.meetingroom.internal.MeetingRoomPrebuiltHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The fully configured meeting room, produced by [MeetingRoomBuilder.build].
 *
 * ## Entry points
 *
 * ### Activity (simplest)
 * ```kotlin
 * prebuilt.launch(context)
 * ```
 *
 * ### Composable (embedded)
 * ```kotlin
 * setContent { prebuilt.content() }
 * ```
 *
 * ## State observation
 * ```kotlin
 * val state by prebuilt.stateHolder.callState.collectAsStateWithLifecycle()
 * ```
 *
 * @property stateHolder Read-only view of the current call state. Populated once [content] is
 *   first composed and the call setup begins.
 * @property content     Fully composed meeting room UI. Embed in any Compose hierarchy.
 */
@Suppress("LongParameterList")
class MeetingRoomPrebuilt internal constructor(
    internal val baseUrl: String,
    internal val roomName: String,
    internal val enabledFeatures: Set<MeetingRoomFeature>,
    internal val onAction: (MeetingRoomSDKAction) -> Unit,
    internal val configuration: MeetingRoomConfiguration,
    internal val publisherSettings: PublisherSettings,
    internal val theme: MeetingRoomTheme,
    internal val isDebug: Boolean,
    internal val reportingContent: (@Composable (() -> Unit) -> Unit)?,
) {
    private val _callState = MutableStateFlow(MeetingRoomCallState(roomName = roomName))

    val stateHolder: MeetingRoomStateHolder = object : MeetingRoomStateHolder {
        override val callState: StateFlow<MeetingRoomCallState> = _callState.asStateFlow()
    }

    /** Called internally by [com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModel]. */
    internal fun updateCallState(state: MeetingRoomCallState) {
        _callState.value = state
    }

    /**
     * Fully composed meeting room UI. Present via a `fullScreenCover`, push it onto a
     * `NavHost`, or embed it directly as a child composable.
     */
    val content: @Composable () -> Unit = {
        MeetingRoomContent(prebuilt = this)
    }

    /**
     * Launch the meeting room in a standalone [MeetingRoomActivity].
     *
     * @param context The calling context.
     */
    fun launch(context: Context) {
        MeetingRoomPrebuiltHolder.set(this)
        val intent = Intent(context, MeetingRoomActivity::class.java).apply {
            putExtra(MeetingRoomPrebuiltHolder.EXTRA_HELD, true)
        }
        context.startActivity(intent)
    }
}
