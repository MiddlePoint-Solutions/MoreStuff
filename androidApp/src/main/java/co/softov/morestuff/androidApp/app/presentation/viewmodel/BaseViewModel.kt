package co.softov.morestuff.androidApp.app.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.androidApp.app.presentation.extension.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

abstract
class BaseViewModel<ViewState : BaseViewState, ViewEvent : BaseViewEvent>(initialState: ViewState) :
    ViewModel() {

    private val stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData = stateMutableLiveData.toLiveData()

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
        stateMutableLiveData.value = new

        viewModelScope.launch(Dispatchers.Default) {
            if (new != old) {
                stateTimeTravelDebugger?.apply {
                    addStateTransition(old, new)
                    logLast()
                }
            }
        }
    }

    fun sendEvent(event: ViewEvent) {
        stateTimeTravelDebugger?.addEvent(event)
        state = onReduceState(event)
    }

    fun loadData() {
        onLoadData()
    }

    protected open fun onLoadData() {}

    protected abstract fun onReduceState(event: ViewEvent): ViewState

    fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(block = block)
}
