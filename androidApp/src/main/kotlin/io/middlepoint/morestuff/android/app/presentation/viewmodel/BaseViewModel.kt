package io.middlepoint.morestuff.android.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.middlepoint.morestuff.android.BuildConfig
import io.middlepoint.morestuff.android.domain.redux.AppState
import io.middlepoint.morestuff.android.domain.redux.AppStore
import io.middlepoint.morestuff.android.domain.redux.store.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.Delegates

abstract
class BaseViewModel<ViewState : BaseViewState, ViewEvent : BaseViewEvent>(
    initialState: ViewState
) : ViewModel(), KoinComponent {

    protected val store: AppStore by inject()

    private val _uiModel = MutableStateFlow(initialState)
    val uiModel: StateFlow<ViewState> = _uiModel

    protected open val enableDebug = false
    private val stateTimeTravelDebugger: StateTimeTravelDebugger? by lazy {
        when (BuildConfig.DEBUG && enableDebug) {
            true -> StateTimeTravelDebugger(this::class.java.simpleName)
            false -> null
        }
    }

    // Delegate will handle state event deduplication
    // (multiple states of the same type holding the same data will not be dispatched multiple times to LiveData stream)
    protected var state by Delegates.observable(initialState) { _, old, new ->
        _uiModel.update { new }
        viewModelScope.launch {
            if (new != old) {
                stateTimeTravelDebugger?.apply {
                    addStateTransition(old, new)
                    logLast()
                }
            }
        }
    }

    fun loadData() {
        store.state
            .onEach { onAppStateChange(it) }
            .launchIn(viewModelScope)
    }

    fun sendEvent(event: ViewEvent) {
        stateTimeTravelDebugger?.addEvent(event)
        state = onReduceState(event)
    }

    protected fun dispatchAppStoreAction(action: Action) {
        store.dispatch(action)
    }

    protected suspend fun dispatchSuspend(action: Action) {
        store.dispatchSuspend(action)
    }

    protected open fun onLoadData(appState: AppState) {}

    protected open fun onAppStateChange(state: AppState) {}

    protected open fun onReduceState(event: ViewEvent): ViewState = state

    fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(block = block)

}
