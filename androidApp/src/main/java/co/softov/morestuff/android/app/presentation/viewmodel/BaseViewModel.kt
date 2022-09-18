package co.softov.morestuff.android.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.AppState
import co.softov.morestuff.android.domain.redux.AppStore
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.Delegates

abstract
class BaseViewModel<ViewState : BaseViewState, ViewEvent : BaseViewEvent>(
    initialState: ViewState
) : ViewModel(), KoinComponent {

    private val store: AppStore by inject()
    protected val router: Router by inject()

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ViewState> = _uiState

    protected open var enableDebug = false
    private val stateTimeTravelDebugger: StateTimeTravelDebugger? by lazy {
        when (BuildConfig.DEBUG && enableDebug) {
            true -> StateTimeTravelDebugger(this::class.java.simpleName)
            false -> null
        }
    }

    // Delegate will handle state event deduplication
    // (multiple states of the same type holding the same data will not be dispatched multiple times to LiveData stream)
    protected var state by Delegates.observable(initialState) { _, old, new ->
        _uiState.value = new

        viewModelScope.launch(Dispatchers.Default) {
            if (new != old) {
                stateTimeTravelDebugger?.apply {
                    addStateTransition(old, new)
                    logLast()
                }
            }
        }
    }

    fun loadData() {
        onLoadData()

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

    protected open fun onLoadData() {}

    protected open fun onAppStateChange(state: AppState) {}

    protected abstract fun onReduceState(event: ViewEvent): ViewState

    fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(block = block)
}
