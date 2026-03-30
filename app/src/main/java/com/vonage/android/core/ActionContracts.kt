package com.vonage.android.core

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * A discrete, self-contained unit of business logic that can be [dispatched][BaseViewModel.dispatch]
 * by a [BaseViewModel].
 *
 * Each action encapsulates a single use-case (e.g. "toggle a setting", "observe a flow").
 * It receives the screen's [ActionDependencies] and an [ActionScope] to read/update state
 * or emit one-time events, keeping the ViewModel thin and the logic easily unit-testable.
 *
 * @param D The concrete [ActionDependencies] subtype this action requires.
 * @param S The [ViewState] type managed by the hosting ViewModel.
 * @param E The [ViewEvent] type emitted by the hosting ViewModel.
 */
interface ViewAction<D : ActionDependencies, S : ViewState, E : ViewEvent> {
    /**
     * Executes this action.
     *
     * @param dependencies The screen-specific dependencies (repositories, holders, etc.).
     * @param actionScope  A scope that provides state mutation and event emission capabilities.
     */
    suspend fun execute(dependencies: D, actionScope: ActionScope<S, E>)
}

/**
 * Scoped environment passed to every [ViewAction.execute] call.
 *
 * Provides controlled access to the ViewModel's internal state and event channel
 * so that actions can:
 * - Read the latest state via [currentState].
 * - Atomically update state via [setState].
 * - Send one-time side-effect events via [sendEvent].
 *
 * @param S The [ViewState] type.
 * @param E The [ViewEvent] type.
 */
class ActionScope<S : ViewState, E : ViewEvent>(
    private val stateFlow: MutableStateFlow<S>,
    private val eventChannel: Channel<E>
) {
    /** Returns the current snapshot of the UI state. */
    val currentState: S get() = stateFlow.value

    /**
     * Atomically updates the state by applying [reducer] to the current value.
     *
     * @param reducer A function invoked with the current state as receiver,
     *                returning the new state (typically via `copy()`).
     */
    fun setState(reducer: S.() -> S) = stateFlow.update(reducer)

    /**
     * Sends a one-time [event] to the UI layer (e.g. navigation, snackbar).
     * Uses [Channel.trySend] so it never suspends.
     */
    fun sendEvent(event: E) = eventChannel.trySend(event)
}
