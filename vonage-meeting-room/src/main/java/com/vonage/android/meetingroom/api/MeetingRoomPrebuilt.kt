package com.vonage.android.meetingroom.api

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import com.vonage.android.meetingroom.internal.MeetingRoomActivity
import com.vonage.android.meetingroom.internal.MeetingRoomContent
import com.vonage.android.meetingroom.internal.MeetingRoomPrebuiltHolder
import com.vonage.android.settings.CallSettingsHolder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
 * val state by prebuilt.callState.collectAsStateWithLifecycle()
 * ```
 *
 * The SDK proactively requests required permissions before rendering the meeting room UI.
 * Provide a custom permission composable via [MeetingRoomBuilder.permissionContent] to replace
 * the built-in permission UI with your own.
 *
 * @property callState  Read-only view of the current call state. Populated once [content] is
 *   first composed and the call setup begins.
 * @property content     Fully composed meeting room UI. Embed in any Compose hierarchy.
 */
@ExperimentalMeetingRoomApi
@Suppress("LongParameterList")
class MeetingRoomPrebuilt internal constructor(
    internal val baseUrl: String,
    internal val roomName: String,
    internal val enabledFeatures: Set<MeetingRoomFeature>,
    internal val onAction: (MeetingRoomSDKAction) -> Unit,
    internal val configuration: MeetingRoomConfiguration,
    internal val publisherSettings: PublisherSettings,
    internal val callSettingsHolder: CallSettingsHolder?,
    internal val theme: MeetingRoomTheme,
    internal val isDebug: Boolean,
    internal val reportingContent: (@Composable (() -> Unit) -> Unit)?,
    internal val permissionContent: @Composable (List<String>, () -> Unit) -> Unit,
    internal val foregroundServiceEnabled: Boolean,
    /** Dynamic list of extra buttons appended after the built-in bottom bar actions. */
    internal val additionalBottomBarActions: StateFlow<List<MeetingRoomBottomBarAction>>? = null,
    /** Full replacement for the bottom bar. When set, [additionalBottomBarActions] is ignored. */
    internal val customBottomBar: (@Composable (MeetingRoomBottomBarState, MeetingRoomCustomActions) -> Unit)? = null,
) {
    private val _callState = MutableStateFlow(MeetingRoomCallState(roomName = roomName))

    private val _hangUpCommand = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /**
     * Internal command flow observed by [com.vonage.android.meetingroom.internal.viewmodel.MeetingRoomViewModel].
     * Emitted by [hangUp].
     */
    internal val hangUpCommand: SharedFlow<Unit> = _hangUpCommand.asSharedFlow()

    /**
     * Ends the active call programmatically.
     *
     * Use this when the host application manages its own foreground service and notification.
     * Wire the notification's "Hang Up" `PendingIntent` to call this method so the SDK receives
     * the hang-up signal even when the SDK's own foreground service is disabled via
     * [MeetingRoomBuilder.foregroundServiceEnabled].
     *
     * Calling this before the meeting room composable is first shown is safe — the command is
     * buffered (capacity 1) and will be delivered as soon as the ViewModel initialises.
     * Calling it after the call has already ended is a no-op.
     */
    fun hangUp() {
        _hangUpCommand.tryEmit(Unit)
    }

    val callState: StateFlow<MeetingRoomCallState> = _callState.asStateFlow()
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
