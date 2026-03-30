package com.vonage.android.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base class for ViewModels following the unidirectional data flow (UDF) pattern.
 *
 * Manages a single immutable [state] of type [S] and exposes a stream of one-time [events]
 * of type [E] (e.g. navigation commands, snackbar messages).
 *
 * Subclasses must provide their [dependencies] (an [ActionDependencies] instance that bundles
 * the use-cases, repositories, or holders the screen needs) and interact with the state
 * exclusively through [dispatch], which accepts a [ViewAction] and executes it inside
 * [viewModelScope].
 *
 * Each [ViewAction] receives the [dependencies] together with an [ActionScope] that
 * allows it to read/update the state and emit events, keeping the ViewModel itself thin
 * and the business logic easily testable in isolation.
 *
 * @param S The [ViewState] type that represents the UI state for this screen.
 * @param E The [ViewEvent] type that represents one-time side-effects for this screen.
 * @param initialState The initial value of the UI state exposed via [state].
 */
abstract class BaseViewModel<S : ViewState, E : ViewEvent>(initialState: S) : ViewModel() {

    /** Dependencies injected into every [ViewAction] dispatched by this ViewModel. */
    protected abstract val dependencies: ActionDependencies

    /** Observable UI state. Collect this from the Compose layer via `collectAsStateWithLifecycle`. */
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    /** Channel-backed flow of one-time events consumed by the UI (navigation, toasts, etc.). */
    private val _events = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = _events.receiveAsFlow()

    /**
     * Dispatches a [ViewAction] for execution inside [viewModelScope].
     *
     * The action's [ViewAction.execute] method receives the concrete [ActionDependencies]
     * (cast from [dependencies]) and an [ActionScope] to mutate state or emit events.
     *
     * @param D The concrete [ActionDependencies] subtype expected by [action].
     * @param action The action to execute.
     */
    protected fun <D : ActionDependencies> dispatch(action: ViewAction<D, S, E>) {
        viewModelScope.launch {
            action.execute(dependencies as D, ActionScope(_state, _events))
        }
    }
}
