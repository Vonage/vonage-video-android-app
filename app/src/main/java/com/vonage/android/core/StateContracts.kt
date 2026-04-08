package com.vonage.android.core

import kotlinx.coroutines.CoroutineScope

/** Marker interface for immutable data classes that represent a screen's UI state. */
typealias ViewState = com.vonage.android.shared.ViewState

/** Marker interface for sealed types that represent one-time side-effect events (navigation, toasts, etc.). */
typealias ViewEvent = com.vonage.android.shared.ViewEvent

/**
 * Base class for the dependency bundle provided to every [ViewAction] dispatched by a [BaseViewModel].
 *
 * Subclass this to expose the repositories, holders, or use-cases a particular screen needs.
 * The [coroutineScope] is typically backed by [androidx.lifecycle.viewModelScope].
 */
abstract class ActionDependencies {
    /** Coroutine scope used by actions that need to launch long-running or concurrent work. */
    abstract val coroutineScope: CoroutineScope
}
