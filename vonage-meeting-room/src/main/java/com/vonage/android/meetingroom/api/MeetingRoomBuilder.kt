package com.vonage.android.meetingroom.api

import androidx.compose.runtime.Composable

/**
 * Fluent builder for the meeting room SDK.
 *
 * All methods return `this` for chaining. Call [build] once all parameters are set to receive
 * a [MeetingRoomPrebuilt] that can be launched as an Activity or embedded as a composable.
 *
 * ## Quick start
 * ```kotlin
 * val prebuilt = MeetingRoomBuilder(
 *     baseUrl  = "https://api.example.com/",
 *     roomName = "my-room",
 * )
 *     .enabledFeatures(setOf(MeetingRoomFeature.CHAT, MeetingRoomFeature.CAPTIONS))
 *     .onAction { action ->
 *         when (action) {
 *             is MeetingRoomSDKAction.CallDidEnd -> // navigate to goodbye screen
 *             is MeetingRoomSDKAction.GoBack     -> // return to waiting room
 *         }
 *     }
 *     .publisherSettings(PublisherSettings(username = "Alice"))
 *     .build()
 *
 * // Option A — Activity
 * prebuilt.launch(context)
 *
 * // Option B — Composable
 * setContent { prebuilt.content() }
 * ```
 *
 * @param baseUrl  Base URL of the Vonage Video backend (e.g. `"https://my-backend.example.com"`).
 * @param roomName Name of the meeting room to join.
 */
@ExperimentalMeetingRoomApi
class MeetingRoomBuilder(
    private val baseUrl: String,
    private val roomName: String,
) {
    private var enabledFeatures: Set<MeetingRoomFeature> = MeetingRoomFeature.all
    private var onAction: (MeetingRoomSDKAction) -> Unit = {}
    private var configuration: MeetingRoomConfiguration = MeetingRoomConfiguration()
    private var publisherSettings: PublisherSettings = PublisherSettings()
    private var theme: MeetingRoomTheme = MeetingRoomTheme.vonage
    private var isDebug: Boolean = false
    private var reportingContent: (@Composable (() -> Unit) -> Unit)? = null
    private var foregroundServiceEnabled: Boolean = true

    /**
     * Defines which optional features are active at runtime.
     *
     * This is an additional runtime filter on top of the compile-time Gradle flavor system: a
     * feature is only active when its flavor is `enabled` **and** it is included in [features].
     * Defaults to [MeetingRoomFeature.all] (all features on — same behavior as before the
     * builder API).
     */
    fun enabledFeatures(features: Set<MeetingRoomFeature>): MeetingRoomBuilder = apply {
        enabledFeatures = features
    }

    /**
     * Registers a handler for navigation callbacks emitted by the SDK.
     *
     * The host app must handle all [MeetingRoomSDKAction] cases. Permission prompts and error
     * alerts are presented automatically by the SDK itself.
     */
    fun onAction(handler: (MeetingRoomSDKAction) -> Unit): MeetingRoomBuilder = apply {
        onAction = handler
    }

    /**
     * Customises the meeting room UI controls (camera, microphone, participant list toggles).
     * Defaults to [MeetingRoomConfiguration] with all controls visible.
     */
    fun configuration(config: MeetingRoomConfiguration): MeetingRoomBuilder = apply {
        configuration = config
    }

    /**
     * Sets the initial publisher configuration (username, audio/video enabled flags).
     * Useful when a waiting room already captured user preferences.
     */
    fun publisherSettings(settings: PublisherSettings): MeetingRoomBuilder = apply {
        publisherSettings = settings
    }

    /**
     * Applies a custom color theme. Defaults to [MeetingRoomTheme.vonage].
     *
     * Use [MeetingRoomTheme.vonage] as a starting point and call [copy] to override
     * individual color roles:
     * ```kotlin
     * .theme(
     *     MeetingRoomTheme(
     *         lightColors = MeetingRoomTheme.vonage.lightColors.copy(primary = Color.Blue),
     *         darkColors  = MeetingRoomTheme.vonage.darkColors.copy(primary  = Color.Blue),
     *     )
     * )
     * ```
     */
    fun theme(theme: MeetingRoomTheme): MeetingRoomBuilder = apply {
        this.theme = theme
    }

    /**
     * Enables verbose HTTP logging. Defaults to `false`.
     */
    fun isDebug(debug: Boolean): MeetingRoomBuilder = apply {
        isDebug = debug
    }

    /**
     * Controls whether the SDK starts its own foreground service for the duration of the call.
     * Defaults to `true`.
     *
     * Set to `false` when the host application already manages a foreground service that keeps
     * the process alive during the call (e.g. a host-owned in-call notification service). In
     * that case, use [MeetingRoomPrebuilt.hangUp] to forward hang-up signals from the host
     * notification to the SDK.
     */
    fun foregroundServiceEnabled(enabled: Boolean): MeetingRoomBuilder = apply {
        foregroundServiceEnabled = enabled
    }

    /**
     * Provides a custom composable shown inside the report-issue bottom sheet.
     *
     * The composable receives an `onDismiss` callback. When `null` (the default), the SDK
     * uses its built-in placeholder or hides the panel when the reporting feature is disabled.
     */
    fun reportingContent(content: @Composable (() -> Unit) -> Unit): MeetingRoomBuilder = apply {
        reportingContent = content
    }

    /**
     * Constructs the [MeetingRoomPrebuilt] with the current configuration.
     *
     * Call [MeetingRoomPrebuilt.launch] or embed [MeetingRoomPrebuilt.content] to display the
     * meeting room.
     */
    fun build(): MeetingRoomPrebuilt = MeetingRoomPrebuilt(
        baseUrl = baseUrl,
        roomName = roomName,
        enabledFeatures = enabledFeatures,
        onAction = onAction,
        configuration = configuration,
        publisherSettings = publisherSettings,
        theme = theme,
        isDebug = isDebug,
        reportingContent = reportingContent,
        foregroundServiceEnabled = foregroundServiceEnabled,
    )
}
